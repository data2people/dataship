function reqPermission(id) {
    enabled = (document.getElementById(id).getElementsByClassName("btn")[0].textContent == "Enable");
    if (enabled) {
        // post request
        document.getElementById(id).getElementsByClassName("btn")[0].textContent = "Disable";
    } else {
        document.getElementById(id).getElementsByClassName("btn")[0].textContent = "Enable";
    }
}

function updateProviderStates(providerState, targetPage) {
    var prov = providerState["providers"];
    var provsLength = prov.length;
    for (var i = 0; i < provsLength; i++) {
        if (prov[i] != null) {
            if (targetPage = "project" && document.getElementById(prov[i]["name"]).getElementsByClassName("btn")[0] != null) {

                if (prov[i]["enabled"]) {
                    document.getElementById(prov[i]["name"]).getElementsByClassName("btn")[0].textContent = "Enable";
                } else {
                    document.getElementById(prov[i]["name"]).getElementsByClassName("btn")[0].textContent = "Disable";
                }

            } else if (targetPage = "submitreq") {
                var prevValue = 
                    document.getElementById(prov[i]["name"]).value;
                if (!prov[i]["enabled"] && document.getElementById(prov[i]["name"])) {
                    document.getElementById(prov[i]["name"]).value += " (need to login)";
                }
            }
        }
    }
}

function getPermissions() {
    // return dumb json
    dumb = 
    {
        "providers": [
        {
                        "name": "facebook",
                        "enabled": false
                                                
        },
        {
                        "name": "google-plus",
                        "enabled": true
                                                
        },
        {
                        "name": "google-fit",
                        "enabled": false
                                                
        }
            
        ]

    };
    return dumb;
}
