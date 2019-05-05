'use-strict'

const functions = require('firebase-functions');

//En esta parte vamos a escribir todas las firebase functions y vamos a manejar toda la lógica de la notificacion

//Inicializamos la app
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

//Esto es una funcion que va a estar escuchando en el nodo 'Notifications'
//Esto es equivalente al addValueEventListener(...) en android
exports.sendNotification = functions.database.ref("Notifications")
    .onWrite(event => {
        var request = event.data.val();

        //Aqui necesitamos crear un payload taht será enviado hacia el dispositivo.
        // El payload necesita tener al menos un 'data' o 'notification'. En este caso solo usaré
        //'data'. Este payload sera enviado como un Map<string,string>
        var payload = {
            data: {
                username: "usuario inicial",
                email: "usuario@gmail.com"
            }
        };

        //Esto nos permite usar FCM para enviar notificacion/mensaje hacia el dispositivo.
        //Todo esto usando el token del dispositivo al que queremos mandar.
        admin.messaging().sendToDevice(request.token, payload)
            .then(function (response) {
                console.log("El mensaje se ha enviado: ", response);
            })
            .catch(function (error) {
                console.log("Error enviando el mensaje: ", error);
            })
    });
