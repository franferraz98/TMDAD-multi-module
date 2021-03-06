var stompClient = null;
var stompClient2 = null;
var stompClient3 = null;
var stompClient4 = null;
var stompClient5 = null;

var from = "";
var username = "";

function setConnected(connected) {

    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    // document.getElementById('subscribe').disabled = !connected;
    // document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
    // document.getElementById('createRoomDiv').style.visibility = connected ? 'visible' : 'hidden';
    document.getElementById('converRoomDiv').style.visibility = connected ? 'visible' : 'hidden';
    // document.getElementById('response').innerHTML = '';
}

function setConnectedAdmin(connected) {

    document.getElementById('connectAdmin').disabled = connected;
    document.getElementById('disconnectAdmin').disabled = !connected;
    document.getElementById('notificationsDiv').style.visibility = connected ? 'visible' : 'hidden';
}

function connect() {

    // Subscribe to general chat
    var socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {

        setConnected(true);
        console.log('Connected 1: ' + frame);
    });

    // Subscribe to self queue
    var socket2 = new SockJS('/route');
    stompClient2 = Stomp.over(socket2);
    username = document.getElementById('username').value;
    var topic = '/topic/';
    topic = topic.concat(username);

    stompClient2.connect({}, function(frame) {

      setConnected(true);
      console.log('Connected 2: ' + frame);
      stompClient2.subscribe(topic, function(messageOutput) {

          showMessageOutput(JSON.parse(messageOutput.body));
      });
    });

    // Subscribe to chat room creator
    var socket3 = new SockJS('/createRoom');
    stompClient3 = Stomp.over(socket3);

    stompClient3.connect({}, function(frame) {

        setConnected(true);
        console.log('Connected 3: ' + frame);
    });

    // Add user to chat room
    var socket4 = new SockJS('/addToRoom');
    stompClient4 = Stomp.over(socket4);

    stompClient4.connect({}, function(frame) {

        setConnected(true);
        console.log('Connected 4: ' + frame);
    });

    document.getElementById('subscribe').disabled = false;
}

function connectAdmin() {
    var socket5 = new SockJS('/notifications');
    stompClient5 = Stomp.over(socket5);

    var password = document.getElementById('password').value;
    var result = password.localeCompare('admin');
    if (password == 'admin') {

        stompClient5.connect({}, function(frame) {
                setConnectedAdmin(true);
                console.log('Connected 5: ' + frame);
        });
        document.getElementById('subscribeAdmin').disabled = false;
    }
}

function disconnect() {

    if(stompClient != null) {
        stompClient.disconnect();
    }

    setConnected(false);
    console.log("Disconnected");
}

function disconnectAdmin() {

    if(stompClient5 != null) {
        stompClient5.disconnect();
    }

    setConnectedAdmin(false);
    console.log("Admin Disconnected");
}

function subscribe() {

    username = document.getElementById('username').value;
    stompClient2.send("/app/route", {}, JSON.stringify({'from':username, 'text':username}));
    document.getElementById('subscribe').disabled = true;

}

function subscribeAdmin() {

    username = 'admin';
    stompClient5.send("/app/notifications", {}, JSON.stringify({'from':username, 'text':username}));
    document.getElementById('subscribeAdmin').disabled = true;

}

function createRoom() {
    from = document.getElementById('from').value;
    var text = document.getElementById('chatRoom').value;
    stompClient3.send("/app/createRoom", {}, JSON.stringify({'from':from, 'text':text}));
}

function addToRoom() {
    from = document.getElementById('from').value;
    var text = document.getElementById('room').value;
    var user = document.getElementById('userToRoom').value;
    text = text.concat(':::');
    text = text.concat(user);
    stompClient3.send("/app/addToRoom", {}, JSON.stringify({'from':from, 'text':text}));
}

function sendNotification() {

    from = 'notification';
    var text = document.getElementById('textNoti').value;
    stompClient5.send("/app/notify", {}, JSON.stringify({'from':from, 'text':text}));
}

function sendToRoom() {

    from = document.getElementById('from').value;
    var text = document.getElementById('textRoom').value;
    var dest = document.getElementById('destRoom').value
    from = from.concat('@');
    from = from.concat(dest);
    text = text.concat(':::');
    text = text.concat(dest);
    stompClient.send("/app/chatRoom", {}, JSON.stringify({'from':from, 'text':text}));
}

function sendMessage() {

    from = document.getElementById('from').value;
    var text = document.getElementById('text').value;
    text = text.concat(':::');
    text = text.concat(document.getElementById('destination').value);
    stompClient.send("/app/chat", {}, JSON.stringify({'from':from, 'text':text}));
}

function showMessageOutput(messageOutput) {

    var response = document.getElementById('response');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode(messageOutput.from + ": " + messageOutput.text + " (" + messageOutput.time + ")"));
    response.appendChild(p);
}

function switchURL() {
   window.location.href = 'https://www.w3schools.com';
}

function addURL() {
    var URLs = {
      url1: {
        name: 'https://www.w3schools.com'
      },
      url2: {
        name: 'https://www.w3schools.com'
      },
      url3: {
        name: 'https://www.w3schools.com'
      }
    }

    var list = function(URLs) {
      for (var prop in URLs) {
        console.log(prop.name)
        document.getElementById('URLs').innerHTML += '<li>' + prop + ' URL: ' + URLs[prop].name + '</li>';
      }
    }
    list(URLs);
}

function info() {
    document.querySelector('html').setAttribute("from", from)
    document.querySelector('html').setAttribute("username", username)
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open( "GET", "/info", false ); // false for synchronous request
    xmlHttp.send(null);
    document.querySelector('html').innerHTML = xmlHttp.responseText;
}

function replaceHTML() {

    var request = new XMLHttpRequest();
    request.open("GET", "", false);
    request.send(null);
    var returnValue = request.responseText;

    document.open();
    document.write(NC);
    document.close();
}
