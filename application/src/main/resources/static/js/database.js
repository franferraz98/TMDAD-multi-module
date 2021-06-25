function searchFile() {
     let fileid = document.getElementById("fileId").value;

     function reqListener () {
        console.log( "FileID: "  + fileid);
     }

    var req2 = new XMLHttpRequest();
    req2.open('GET', 'http://localhost:8080/files/' + fileid, false);
    req2.send(null);
    if (req2.status == 200)
      console.log(req2.responseURL);

     // and just like that you have control of the data

    function showMessageOutput(messageOutput) {
        var response = document.getElementById('File');
        var p2 = document.createElement('p2');
        p2.style.wordWrap = 'break-word';
        p2.appendChild(document.createTextNode(messageOutput));
        response.appendChild(p2);
    }
    showMessageOutput(req2.responseURL)
}