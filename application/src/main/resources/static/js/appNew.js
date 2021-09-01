var stompClient = null;

function setConnected(connected) {
    $("#connectBtn").prop("disabled", connected);
    $("#disconnectBtn").prop("disabled", !connected);
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
    let ref = window.location.href;
    let parts = ref.split("/login");
    req.open('POST', parts[0] + '/Usuarios/login', false); //TODO: Comprobar URI
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

    /*
    var text = 'login---';
    text = text.concat(username);
    text = text.concat("&");
    text = text.concat(psw);
    stompClient.send("/app/client", {}, JSON.stringify({'from':username, 'text':text}));
     */
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
        setConnected(true);
    });
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
            setConnected(true);
        });
    }
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }

    setConnected(false);
    sessionStorage.removeItem("username");
    console.log("Disconnected");
}

function signup() {
    var username = document.getElementById('username').value;
    var psw = document.getElementById('psw').value;
    var pswrepeat = document.getElementById('psw-repeat').value;

    if (psw == pswrepeat) {

        /*
        var from = document.getElementById('from').value;
        var text = 'signup---';
        text = text.concat(username);
        text = text.concat(':::');
        text = text.concat(psw);
        stompClient.send("/app/client", {}, JSON.stringify({'from':from, 'text':text}));
        */

        var req = new XMLHttpRequest();
        var msg = username;
        msg = msg.concat("&");
        msg = msg.concat(psw);
        console.log(msg);
        let ref = window.location.href;
        let parts = ref.split("/signup");
        req.open('POST', parts[0] + '/Usuarios', false);
        req.send(msg);
        if (req.status == 200) {
            console.log("USUARIO CREADO");
            // window.location.replace("localhost:8080/login");
        } else {
            // TODO: Presentar excepcion
        }

    } else {
        // TODO: Presentar excepcion
    }
}

function showGroups() {
    var username = sessionStorage.getItem("username");
    var text = 'showGroups---';
    text = text.concat(username);
    stompClient.send("/app/client", {}, JSON.stringify({'from':username, 'text':text}));
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

    var text = 'addToRoom---';
    text = text.concat(groupname);
    text = text.concat(':::');
    text = text.concat(UserRoom);
    stompClient.send("/app/client", {}, JSON.stringify({'from':username, 'text':text}));
}

function deleteFromRoom(activePage) {
    var UserRoom = document.getElementById("userToRoom").value;
    var username = sessionStorage.getItem("username");
    var groupname = activePage.replace('group/', '');

    var text = 'deleteFromRoom---';
    text = text.concat(groupname);
    text = text.concat(':::');
    text = text.concat(UserRoom);
    stompClient.send("/app/client", {}, JSON.stringify({'from':username, 'text':text}));
}

function deleteRoom(activePage) {
    var username = sessionStorage.getItem("username");
    var groupname = activePage.replace('group/', '');

    var text = 'deleteRoom---';
    text = text.concat(groupname);
    stompClient.send("/app/client", {}, JSON.stringify({'from':username, 'text':text}));
}

function getMessages(activePage){
    var groupname = activePage.replace('group/', '');
    var req = new XMLHttpRequest();
    let ref = window.location.href;
    let parts = ref.split("/group");
    var url = parts[0] + '/getMessages/';
    url = url.concat(groupname);
    req.open('GET', url, false);
    req.send();
    if (req.status == 200) {
        // console.log(req.responseText);
        let obj = JSON.parse(req.responseText);
        obj.forEach(interpret);
        function interpret(value, index, array) {
            var response = document.getElementById('response');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(value.username + ": " + value.content));
            response.appendChild(p);
        }
    }
}

function sendMessage() {
    var from = sessionStorage.getItem("username");
    var text = 'chat---';
    text = text.concat(document.getElementById('text').value);
    text = text.concat(':::');
    text = text.concat(document.getElementById('destination').value);
    stompClient.send("/app/client", {}, JSON.stringify({'from':from, 'text':text}));
}

function sendToRoom(activePage) {

    var req = new XMLHttpRequest();
    var username = sessionStorage.getItem("username");

    var from = username
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
    let ref = window.location.href;
    let parts = ref.split("/group");
    req.open('POST', parts[0] + '/Mensajes', false);
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
    if(parts[0] === "chat"){
        let ttcheck = parts[1].split("+++");
        if(ttcheck.length === 1){
            var response = document.getElementById('response');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(messageOutput.from + ": " + parts[1] + " (" + messageOutput.time + ")"));
            response.appendChild(p);
        } else {
            let ref = window.location.href;
            let aP = ref.split("/");
            if(aP[aP.length-1] === "trendingtopics"){
                let json = ttcheck[0].split(",");
                let text = "<table>"
                json.forEach(myFunction2)
                text += "</table>"
                document.getElementById("response").innerHTML = text;
                function myFunction2(value, index, array) {
                    value = value.replace('{', '');
                    value = value.replace('}', '');
                    value = value.replace('\"','');
                    value = value.replace('\"','');
                    let aux = value.split(":");
                    text += "<tr><td>" + aux[0] + "</td>" + "<td>" + aux[1] + "</td></a><tr/>";
                }
            }
        }
    } else if (parts[0] === "showGroups"){
        console.log(parts[1]);
        var json = parts[1].split(";");

        let text = "<table border='1'>"
        json.forEach(myFunction)
        text += "</table>"
        document.getElementById("response").innerHTML = text;

        function myFunction(value, index, array) {
            let ref = window.location.href;
            let parts = ref.split("/info");
            let url = parts[0] + "/group/" + value;
            text += "<tr><td>" + value + "</td>" + "<td><a href=\ " + url + ">" + url + "</td></a><tr/>";
        }
    } else if(parts[0] === "login"){
        sessionStorage.setItem("username", parts[1]);
        connectToMain();
    } else{
        // Movidas
    }
}
