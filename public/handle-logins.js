function reqPermission(id) {
    enabled = (document.getElementById(id).getElementsByClassName("btn")[0].textContent == "Enable");
    if (enabled) {
        // post request
        document.getElementById(id).getElementsByClassName("btn")[0].textContent = "Disable";
    } else {
        document.getElementById(id).getElementsByClassName("btn")[0].textContent = "Enable";
    }
}

function updateProviderStates(providerState, targetClass) {
    var prov = providerState["providers"];
    var provsLength = prov.length;
    for (var i = 0; i < provsLength; i++) {
        if (prov[i] != null) {
            if (prov[i]["enabled"]) {
                // post request
                document.getElementById(prov[i]["name"]).getElementsByClassName(targetClass)[0].textContent = "Enable";
            } else {
                document.getElementById(prov[i]["name"]).getElementsByClassName(targetClass)[0].textContent = "Disable";
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
