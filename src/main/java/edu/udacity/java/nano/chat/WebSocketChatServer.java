package edu.udacity.java.nano.chat;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket Server
 *
 * @see ServerEndpoint WebSocket Client
 * @see Session   WebSocket Session
 */

@Component
@ServerEndpoint("/chat/{username}")
public class WebSocketChatServer {
    private static final String ENTER = "ENTER";
    private static final String CHAT = "CHAT";
    private static final String LEAVE = "LEAVE";

    /**
     * All chat sessions.
     */
    private static Map<String, Session> onlineSessions = new ConcurrentHashMap<>();

    private static void sendMessageToAll(Message message) {
        Set<Map.Entry<String, Session>> sessionEntries = onlineSessions.entrySet();
        for (Map.Entry<String, Session> entry : sessionEntries) {
            try {
                Session session = entry.getValue();
                session.getBasicRemote().sendText(JSON.toJSONString(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Open connection, 1) add session, 2) add user.
     */
    @OnOpen
    public void onOpen(Session session,  @PathParam("username") String username) {
        if (!onlineSessions.containsKey(username)) {
            onlineSessions.put(username, session);
            sendMessageToAll(new Message(username, " joined the chat room.", onlineSessions.size(), ENTER));
        }
    }

    /**
     * Send message, 1) get username and session, 2) send message to all.
     */
    @OnMessage
    public void onMessage(Session session, String jsonMessage) {
        Message message = JSON.parseObject(jsonMessage, Message.class);
        message.setOnlineCount(onlineSessions.size());
        message.setType(CHAT);
        sendMessageToAll(message);
    }

    /**
     * Close connection, 1) remove session, 2) update user.
     */
    @OnClose
    public void onClose(Session session, @PathParam("username") String username) throws IOException {
        if (onlineSessions.containsKey(username)) {
            onlineSessions.remove(username);
            session.close();
            sendMessageToAll(new Message(username, " left the chat room.", onlineSessions.size(), LEAVE));
        }
    }

    /**
     * Print exception.
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

}
