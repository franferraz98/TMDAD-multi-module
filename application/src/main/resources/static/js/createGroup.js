function createGroup() {
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
        // window.location.replace("localhost:8080/login");
    } else {
        // TODO: Presentar excepcion
    }
}