/**
 * Login
 */
function login() {
    var username = $("#username").val();
    if (username !== "") {
        window.location.href = "/index?username=" + username;
    }
}

/**
 * Enter to login.
 */
document.onkeydown = function (event) {
    var e = event || window.event || arguments.callee.caller.arguments[0];
    if (e.keyCode === 13) {
        login();
        return false;
    }
};
