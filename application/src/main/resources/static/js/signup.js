function signup() {
    var username = document.getElementById('username').value;
    var psw = document.getElementById('psw').value;
    var pswrepeat = document.getElementById('psw-repeat').value;

    if (psw == pswrepeat) {
        var req = new XMLHttpRequest();
        var msg = username;
        msg = msg.concat("&");
        msg = msg.concat(psw);
        console.log(msg);
        req.open('POST', 'http://localhost:8080/Usuarios', false);
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