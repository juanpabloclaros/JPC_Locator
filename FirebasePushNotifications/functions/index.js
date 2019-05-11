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

        //const usuarioEmisor = context.params.receiver_user_id;
        const nombreEmisor = datasnapshot.val();
        const nombreReceptor = datasnapshot.child('nombreReceptor').val();
        //const usuarioReceptor = context.params.notification_id;
        const tokenEmisor = datasnapshot.child('tokenEmisor').val();
        const tokenReceptor = datasnapshot.child('tokenReceptor').val();
        const nombreGrupo = datasnapshot.child('nombreGrupo').val();
        const recibido = datasnapshot.child('recibido').val();
        const unirse = datasnapshot.child('unirse').val();

        console.log("nombre emisor: ", nombreEmisor);
        console.log("nomobre receptor: ", nombreReceptor);
        console.log("token emisor: ", tokenEmisor);
        console.log("token receptor: ", tokenReceptor);
        console.log("nombre del grupo: ", nombreGrupo);
        console.log("recibido: ", recibido);
        console.log("Unirse: ", unirse);

        if (recibido) {
            return tokenReceptor.then(() => {

                if (unirse) {
                    const respuesta = "{nombreReceptor} ha aceptado la solicitud";
                } else {
                    const respuesta = "{nombreReceptor} ha rechazado la solicitud";
                }
                //Aqui necesitamos crear un payload taht será enviado hacia el dispositivo.
                // El payload necesita tener al menos un 'data' o 'notification'.
                //Este payload sera enviado como un Map<string,string>
                const payload = {
                    data: {
                        id: "0",
                        titulo: "Respuesta de petición",
                        body: respuesta
                    }
                };

                //Esto nos permite usar FCM para enviar notificacion/mensaje hacia el dispositivo.
                //Todo esto usando el token del dispositivo al que queremos mandar.
                return admin.messaging().sendToDevice(tokenEmisor, payload)
                    .then(function (response) {
                        console.log("El mensaje se ha enviado: ", response);
                    })
                    .catch(function (error) {
                        console.log("Error enviando el mensaje: ", error);
                    });
            });
        }

        return tokenEmisor.then(() => {

			//Aqui necesitamos crear un payload taht será enviado hacia el dispositivo.
			// El payload necesita tener al menos un 'data' o 'notification'.
			//Este payload sera enviado como un Map<string,string>
			const payload = {
                data: {
                    id: "1",
					titulo: "Invitación para unirte a grupo",
					body: "{nombreEmisor} quiere añadir al grupo {nombreGrupo}"
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
		});
	});
