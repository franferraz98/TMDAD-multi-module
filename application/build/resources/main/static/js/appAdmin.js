var stompClientAdmin = null;

function setConnectedAdmin(connected) {
    $("#connectAdmin").prop("disabled", connected);
    $("#disconnectAdmin").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connectToAdmin() {
    // Connect to notification publisher
    var socket = new SockJS('/client');
    stompClientAdmin = Stomp.over(socket);
    var password = document.getElementById('password').value;
    var result = password.localeCompare('admin');
    if (password == 'admin') {
        stompClientAdmin.connect({}, function(frame) {
            console.log('Connected to admin: ' + frame);
            var from = 'notification';
            var text = 'notifications---';
            stompClientAdmin.send("/app/client", {}, JSON.stringify({'from':from, 'text':text}));
        });
        setConnectedAdmin(true);

    }
}

function disconnectAdmin() {
    if (stompClientAdmin !== null) {
        stompClientAdmin.disconnect();
    }
    console.log("Disconnected Admin")
    setConnectedAdmin(false);
}

function sendNotification() {
    var from = 'notification';
    var text = 'notify---';
    text = text.concat(document.getElementById('textNoti').value);
    stompClientAdmin.send("/app/client", {}, JSON.stringify({'from':from, 'text':text}));
}