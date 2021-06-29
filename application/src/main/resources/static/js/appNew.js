var stompClient = null;
var stompClientRC = null;
var stompClientUA = null;
var stompClientAdmin = null;

var username = null;

function wait(ms){
   var start = new Date().getTime();
   var end = start;
   while(end < start + ms) {
     end = new Date().getTime();
  }
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connectOld() {
    // Connect to main chat
    connectToMain();
    // Connect to room creator
    // connectToRoomCreator();

}

function connect() {
    // Check user & password
    var username = document.getElementById('username').value;
    var psw = document.getElementById('psw').value;
    var msg = username;
    msg = msg.concat("&");
    msg = msg.concat(psw);
    console.log(msg);
    var req = new XMLHttpRequest();
    req.open('POST', 'http://localhost:8080/Usuarios/login', true); //TODO: Comprobar URI
    req.onreadystatechange = function (aEvt) {
      if (req.readyState == 4) {
         if(req.status == 200) {
          console.log(req.status);
          connectOld();
         } else {
               console.log(req.responseText);
           }
      }
    };
    req.send(msg);
}

function instantConnectGroup() {
    instantConnect();
    connectToUserAdder();
}

function instantConnect() {
    var aux = sessionStorage.getItem("username");
    if (aux) {
        var socket = new SockJS('/route');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            setConnected(true);
            console.log('Connected to main: ' + frame);
            console.log("Instant correcto");
            console.log(aux);
            username = aux;
            var topic = '/topic/';
            topic = topic.concat(username);
            console.log('Connected: ' + frame);
            console.log(topic);
            stompClient.subscribe(topic, function(message) {
                showMessageOutput(JSON.parse(message.body));
            });
            console.log('Subscribed to queue');
            stompClient.send("/app/route", {}, JSON.stringify({'from':username, 'text':username}));
            console.log('Sent');
        });
        socket = new SockJS('/createRoom');
        stompClientRC = Stomp.over(socket);
        stompClientRC.connect({}, function(frame) {
            setConnected(true);
            console.log('Connected to group creator: ' + frame);
        });
    }
}


function connectToMain() {
    var aux = sessionStorage.getItem("username");
    console.log(aux)
    var socket = new SockJS('/route');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected to main: ' + frame);
        if (aux){
            console.log(aux);
            username = aux;
        } else {
            username = document.getElementById('username').value;
            console.log(username)
            sessionStorage.setItem("username", username);
        }
        var topic = '/topic/';
        topic = topic.concat(username);
        console.log('Connected: ' + frame);
        console.log(topic);
        stompClient.subscribe(topic, function(message) {
            showMessageOutput(JSON.parse(message.body));
        });
        console.log('Subscribed to queue');
        stompClient.send("/app/route", {}, JSON.stringify({'from':username, 'text':username}));
        console.log('Sent');
    });
}

function connectToRoomCreator() {
    var socket = new SockJS('/createRoom');
    stompClientRC = Stomp.over(socket);
    stompClientRC.connect({}, function(frame) {
        setConnected(true);
        console.log('Connected to group creator: ' + frame);
    });
}

function connectToUserAdder() {
    // Add user to chat room
    var socket = new SockJS('/addToRoom');
    stompClientUA = Stomp.over(socket);
    stompClientUA.connect({}, function(frame) {
        setConnected(true);
        console.log('Connected to user adder: ' + frame);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    if (stompClientRC !== null) {
        stompClientRC.disconnect();
    }
    if (stompClientUA !== null) {
        stompClientUA.disconnect();
    }
    setConnected(false);
    sessionStorage.removeItem("username");
    console.log("Disconnected");
}

function createRoom() {
    var text = document.getElementById('chatRoom').value;
    stompClientRC.send("/app/createRoom", {}, JSON.stringify({'from':username, 'text':text}));
}

function addToRoom() {
    var from = document.getElementById('from').value;
    var text = document.getElementById('room').value;
    var user = document.getElementById('userToRoom').value;
    text = text.concat(':::');
    text = text.concat(user);
    stompClient3.send("/app/addToRoom", {}, JSON.stringify({'from':from, 'text':text}));
}

function sendMessage() {
    var from = document.getElementById('from').value;
    var text = document.getElementById('text').value;
    text = text.concat(':::');
    text = text.concat(document.getElementById('destination').value);
    stompClient.send("/app/chat", {}, JSON.stringify({'from':from, 'text':text}));
}

function sendToRoom() {
    var from = document.getElementById('from').value;
    var text = document.getElementById('textRoom').value;
    var dest = document.getElementById('destRoom').value
    from = from.concat('@');
    from = from.concat(dest);
    text = text.concat(':::');
    text = text.concat(dest);
    stompClient.send("/app/chatRoom", {}, JSON.stringify({'from':from, 'text':text}));
}

function showMyGroups(){

    var req = new XMLHttpRequest();
    req.open('GET', 'http://localhost:8080/Usuarios/get/' + username, false);
    req.send(null);
    console.log(req.status);
    if (req.status == 200)
      console.log(req.responseText);

    var jsn = JSON.parse(req.responseText);
    var groupName = jsn.Grupo

    // TODO: Recuperar grupo mediante el nombre y conseguir la URL

    /*
    var groups = document.getElementById('groups');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode(messageOutput.from + ": " + messageOutput.text + " (" + messageOutput.time + ")"));
    response.appendChild(p);
    */
}

function showMessageOutput(messageOutput) {
    var response = document.getElementById('response');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode(messageOutput.from + ": " + messageOutput.text + " (" + messageOutput.time + ")"));
    response.appendChild(p);
}
/*
$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendMessage(); });
});
*/