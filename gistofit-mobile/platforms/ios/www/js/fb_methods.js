            var login = function () {
                console.log('Firing login with Facebook Yo');
                if (!window.cordova) {
                    var appId = prompt("Enter FB Application ID", "");
                    facebookConnectPlugin.browserInit(appId);
                }
                facebookConnectPlugin.login( ["email"], 
                    function (response) { console.log(JSON.stringify(response)) },
                    function (response) { console.log(JSON.stringify(response)) });
            }
            
            var showDialog = function () { 
                facebookConnectPlugin.showDialog( { method: "feed" }, 
                    function (response) { console.log(JSON.stringify(response)) },
                    function (response) { console.log(JSON.stringify(response)) });
            }
            
            var apiTest = function () { 
                facebookConnectPlugin.api( "me/?fields=id,email", ["user_birthday"],
                    function (response) { console.log(JSON.stringify(response)) },
                    function (response) { console.log(JSON.stringify(response)) }); 
            }

            var getAccessToken = function () { 
                facebookConnectPlugin.getAccessToken( 
                    function (response) { console.log(JSON.stringify(response)) },
                    function (response) { console.log(JSON.stringify(response)) });
            }
            
            var getStatus = function () { 
                facebookConnectPlugin.getLoginStatus( 
                    function (response) { console.log(JSON.stringify(response)) },
                    function (response) { console.log(JSON.stringify(response)) });
            }

            var logout = function () { 
                facebookConnectPlugin.logout( 
                    function (response) { console.log(JSON.stringify(response)) },
                    function (response) { console.log(JSON.stringify(response)) });
            }
