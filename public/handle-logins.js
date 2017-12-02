function reqPermission(id) {
    enabled = (document.getElementById(id).getElementsByClassName("btn")[0].textContent == "Enable");
    if (enabled) {
        // post request
        document.getElementById(id).getElementsByClassName("btn")[0].textContent = "Disable";
    } else {
        document.getElementById(id).getElementsByClassName("btn")[0].textContent = "Enable";
    }
}

function getPermissions() {
    // return dumb json
}
