'use-strict'

const functions = require('firebase-functions');

//En esta parte vamos a escribir todas las firebase functions y vamos a manejar toda la lógica de la notificacion

//Inicializamos la app
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

//Esto es una funcion que va a estar escuchando en el nodo 'Notifications'
//Esto es equivalente al addValueEventListener(...) en android
exports.sendNotification = functions.database.ref("/Notifications/Grupo/{emisor_id}/{receptor_id}")
	.onWrite((datasnapshot,context) => {

        const userData = datasnapshot.after.val();
        console.log("userData", datasnapshot.after.val());

        const usuarioEmisor = context.params.emisor_id;

        const nombreEmisor = userData.nombreEmisor;
        const nombreReceptor = userData.nombreReceptor;
        const usuarioReceptor = context.params.receptor_id;
        const tokenEmisor = userData.tokenEmisor;
        const tokenReceptor = userData.tokenReceptor;
        const nombreGrupo = userData.grupo;
        const grupoID = userData.grupoID;
        let recibido = userData.recibido;
        let unirse = userData.unirse;
        let respuesta = "Mensaje de ejemplo";

        console.log("Usuario emisor uid:", usuarioEmisor);
        console.log("Usuario receptor uid:", usuarioReceptor);
        console.log("nombre emisor: ", nombreEmisor);
        console.log("nombre receptor: ", nombreReceptor);
        console.log("token emisor: ", tokenEmisor);
        console.log("token receptor: ", tokenReceptor);
        console.log("nombre del grupo: ", nombreGrupo);
        console.log("recibido: ", recibido);
        console.log("Unirse: ", unirse);

        if (recibido == "true") {

            if (unirse == "true") {
                respuesta = `${nombreReceptor} ha aceptado la solicitud`;
            } else {
                respuesta = `${nombreReceptor} ha rechazado la solicitud`;
            }
            //Aqui necesitamos crear un payload taht será enviado hacia el dispositivo.
            // El payload necesita tener al menos un 'data' o 'notification'.
            //Este payload sera enviado como un Map<string,string>
            const payload = {
                notification: {
                    id: "0",
                    title: "Respuesta de peticion",
                    body: respuesta
                }
            };

            //Esto nos permite usar FCM para enviar notificacion/mensaje hacia el dispositivo.
            //Todo esto usando el token del dispositivo al que queremos mandar.
            return admin.messaging().sendToDevice(tokenEmisor, payload)
                .then(function (response) {
                    console.log("El mensaje se ha enviado: ", response);
                    // Aqui borramos la rama esa de la notificación
                })
                .catch(function (error) {
                    console.log("Error enviando el mensaje: ", error);
                });
        } else {

            //Aqui necesitamos crear un payload taht será enviado hacia el dispositivo.
            // El payload necesita tener al menos un 'data' o 'notification'.
            //Este payload sera enviado como un Map<string,string>
            const payload = {
                data: {
                    id: "1",
                    title: "Invitación para unirte a grupo",
                    body: `${nombreEmisor} quiere agregarte al grupo ${nombreGrupo}`,
                    nombre: nombreEmisor,
                    grupo: nombreGrupo,
                    grupoId: grupoID
                }
            };

            //Esto nos permite usar FCM para enviar notificacion/mensaje hacia el dispositivo.
            //Todo esto usando el token del dispositivo al que queremos mandar.
            return admin.messaging().sendToDevice(tokenReceptor, payload)
                .then(function (response) {
                    console.log("El mensaje se ha enviado: ", response);
                })
                .catch(function (error) {
                    console.log("Error enviando el mensaje: ", error);
                });

        }
	});
