var stompClientAdmin = null;

function connectToAdmin() {
    // Connect to notification publisher
    var socket = new SockJS('/notifications');
    stompClientAdmin = Stomp.over(socket);
    var password = document.getElementById('password').value;
    var result = password.localeCompare('admin');
    if (password == 'admin') {
        stompClientAdmin.connect({}, function(frame) {
                console.log('Connected to admin: ' + frame);
        });
    }
}

function disconnectAdmin() {
    if (stompClientAdmin !== null) {
        stompClientAdmin.disconnect();
    }
    console.log("Disconnected Admin")
}

function sendNotification() {
    var from = 'notification';
    var text = document.getElementById('textNoti').value;
    stompClientAdmin.send("/app/notify", {}, JSON.stringify({'from':from, 'text':text}));
}