function createGroup() {
    var username = sessionStorage.getItem("username");
    var UserRoom = document.getElementById("userToRoom").value;
    var groupname = document.getElementById("room").value;

    var req = new XMLHttpRequest();
    var msg = groupname;
    msg = msg.concat("&");
    msg = msg.concat(UserRoom);
    console.log(msg);
    let ref = window.location.href;
    let parts = ref.split("/index");
    req.open('POST', parts[0] + '/Grupos/{name}/addToGroup', false);
    req.send(msg);
    if (req.status == 200) {
        console.log("Grupo CREADO");
        // window.location.replace("localhost:8080/login");
    } else {
        // TODO: Presentar excepcion
    }
}