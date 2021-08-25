var stompClient = null;

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
          connectToMain();
         } else {
               console.log(req.responseText);
           }
      }
    };
    req.send(msg);
}

function instantConnect() {
    var aux = sessionStorage.getItem("username");
    if (aux) {
        var socket = new SockJS('/client');
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
            var text = 'route---';
            text = text.concat(username);
            stompClient.send("/app/client", {}, JSON.stringify({'from':username, 'text':text}));
            console.log('Sent');
        });
    }
}


function connectToMain() {
    var aux = sessionStorage.getItem("username");
    console.log(aux)
    var socket = new SockJS('/client');
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
        var text = 'route---';
        text = text.concat(username);
        stompClient.send("/app/client", {}, JSON.stringify({'from':username, 'text':text}));
        console.log('Sent');
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }

    setConnected(false);
    sessionStorage.removeItem("username");
    console.log("Disconnected");
}

function createRoom() {
    var username = sessionStorage.getItem("username");
    var chatRoom = document.getElementById('chatRoom').value;

    var req = new XMLHttpRequest();
    var msg = username;
    msg = msg.concat("&");
    msg = msg.concat(chatRoom);
    console.log(msg);
    req.open('POST', 'http://localhost:8080/Grupos', false);
    req.send(msg);
    if (req.status == 200) {
        console.log("Grupo CREADO");

        var text = 'createRoom---';
        text = text.concat(document.getElementById('chatRoom').value);
        stompClient.send("/app/client", {}, JSON.stringify({'from':username, 'text':text}));
    } else {
        // TODO: Presentar excepcion
    }
}

function addToRoom() {
    var UserRoom = document.getElementById("userToRoom").value;
    var groupname = document.getElementById("room").value;

    var req = new XMLHttpRequest();
    var msg = groupname;
    msg = msg.concat("&");
    msg = msg.concat(UserRoom);
    console.log(msg);
    req.open('POST', 'http://localhost:8080/Grupos/addToGroup', false);
    req.send(msg);
    if (req.status == 200) {
        console.log("Grupo CREADO");

        var from = document.getElementById('from').value;
        var text = 'addToRoom---';
        text = text.concat(document.getElementById('room').value);
        text = text.concat(':::');
        text = text.concat(UserRoom);
        stompClient.send("/app/client", {}, JSON.stringify({'from':from, 'text':text}));
    } else {
        // TODO: Presentar excepcion
    }
}

function sendMessage() {
    var from = document.getElementById('from').value;
    if(from == ""){
        from = username;
    }
    var text = 'chat---';
    text = text.concat(document.getElementById('text').value);
    text = text.concat(':::');
    text = text.concat(document.getElementById('destination').value);
    stompClient.send("/app/client", {}, JSON.stringify({'from':from, 'text':text}));
}

function sendToRoom() {
    var from = document.getElementById('from').value;
    if(from == ""){
        from = username;
    }
    var text = 'chatRoom---';
    text = text.concat(document.getElementById('textRoom').value);
    var dest = document.getElementById('destRoom').value
    from = from.concat('@');
    from = from.concat(dest);
    text = text.concat(':::');
    text = text.concat(dest);
    stompClient.send("/app/client", {}, JSON.stringify({'from':from, 'text':text}));
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