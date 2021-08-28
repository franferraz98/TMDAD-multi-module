var stompClient = null;

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
    req.open('POST', 'http://localhost:8080/Usuarios/login', false); //TODO: Comprobar URI
    req.onreadystatechange = function (aEvt) {
      if (req.readyState == 4) {
         if(req.status == 200) {
          console.log(req.status);
          sessionStorage.setItem("username", username);
          connectToMain();
         } else {
             console.log(req.status);
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

function connectInfo() {
    instantConnect();

    /*
    var req = new XMLHttpRequest();
    var username = sessionStorage.getItem("username");
    req.open('GET', 'http://localhost:8080/getGroups/' + username, false);
    req.send(null);
    if (req.status == 200) {
        var json = JSON.parse(req.responseText);
        console.log(json);



        let text = "<table border='1'>"
        for (let x in json) {
            let url = "http://localhost:8080/group/" + json[x].name;
            text += "<tr><td>" + json[x].name + "</td>" + "<td><a href=\ " + url + ">" + url + "</td></a><tr/>";
        }
        text += "</table>"
        document.getElementById("response").innerHTML = text;

    } else {
        // TODO: Presentar excepcion
    }
    */
}

function showGroups() {
    var username = sessionStorage.getItem("username");
    var text = 'showGroups---';
    text = text.concat(username);
    stompClient.send("/app/client", {}, JSON.stringify({'from':username, 'text':text}));
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

    var text = 'createRoom---';
    text = text.concat(document.getElementById('chatRoom').value);
    stompClient.send("/app/client", {}, JSON.stringify({'from':username, 'text':text}));

    /*
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
     */
}

function addToRoom(activePage) {
    var UserRoom = document.getElementById("userToRoom").value;
    var username = sessionStorage.getItem("username");
    var groupname = activePage.replace('group/', '');

    var from = document.getElementById('from').value;
    var text = 'addToRoom---';
    text = text.concat(groupname);
    text = text.concat(':::');
    text = text.concat(UserRoom);
    stompClient.send("/app/client", {}, JSON.stringify({'from':from, 'text':text}));

    // console.log(groupname);

    /*
    var req = new XMLHttpRequest();
    var msg = groupname;
    msg = msg.concat("&");
    msg = msg.concat(UserRoom);
    msg = msg.concat("&")
    msg = msg.concat(username);
    // console.log(msg);
    req.open('POST', 'http://localhost:8080/Grupos/addToGroup', false);
    req.send(msg);
    if (req.status == 200) {
        console.log("Usuario AÑADIDO");

        var from = document.getElementById('from').value;
        var text = 'addToRoom---';
        text = text.concat(groupname);
        text = text.concat(':::');
        text = text.concat(UserRoom);
        stompClient.send("/app/client", {}, JSON.stringify({'from':from, 'text':text}));
    } else {
        // TODO: Presentar excepcion
    }

     */
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

function sendToRoom(activePage) {

    var req = new XMLHttpRequest();
    var username = sessionStorage.getItem("username");

    var from = document.getElementById('from').value;
    if(from == ""){
        from = username;
    }
    var text = 'chatRoom---';
    text = text.concat(document.getElementById('textRoom').value);
    var dest = activePage.replace('group/', '');
    from = from.concat('@');
    from = from.concat(dest);
    text = text.concat(':::');
    text = text.concat(dest);

    var msg = username;
    msg = msg.concat("&");
    msg = msg.concat(dest);
    msg = msg.concat("&");
    msg = msg.concat(document.getElementById('textRoom').value);
    req.open('POST', 'http://localhost:8080/Mensajes', false);
    req.send(msg);
    if (req.status == 200) {
        console.log("Mesnaje guardado");
    } else {
        // Movidas
    }

    stompClient.send("/app/client", {}, JSON.stringify({'from':from, 'text':text}));
}

function showMessageOutput(messageOutput) {
    let mO = messageOutput.text.toString();
    let parts = mO.split(":::");
    console.log(messageOutput);
    console.log(mO);
    console.log(parts);
    if(parts[0] === "chat"){
        var response = document.getElementById('response');
        var p = document.createElement('p');
        p.style.wordWrap = 'break-word';
        p.appendChild(document.createTextNode(messageOutput.from + ": " + parts[1] + " (" + messageOutput.time + ")"));
        response.appendChild(p);
    } else if (parts[0] === "showGroups"){
        console.log(parts[1]);
        var json = parts[1].split(";");

        let text = "<table border='1'>"
        json.forEach(myFunction)
        text += "</table>"
        document.getElementById("response").innerHTML = text;

        function myFunction(value, index, array) {
            let url = "http://localhost:8080/group/" + value;
            text += "<tr><td>" + value + "</td>" + "<td><a href=\ " + url + ">" + url + "</td></a><tr/>";
        }

    } else{
        // Movidas
    }


}
