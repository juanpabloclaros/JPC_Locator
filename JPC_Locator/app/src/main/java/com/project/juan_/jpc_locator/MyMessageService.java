package com.project.juan_.jpc_locator;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyMessageService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingServic";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        crearNotificationChannel();
        mostrarNotificacion(remoteMessage);
    }

    private void crearNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel =
                    new NotificationChannel("notificacion", "Notificacion", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Notificaciones de JPC Locator");
            notificationChannel.setShowBadge(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void mostrarNotificacion(RemoteMessage remoteMessage) {

        Intent intent = null;
        int icono = 0;

        if (remoteMessage.getData().get("id").equals("1")){
            intent = new Intent(MyMessageService.this, RequestActivity.class);
            intent.putExtra("nombre",remoteMessage.getData().get("nombre"));
            intent.putExtra("grupo",remoteMessage.getData().get("grupo"));
            intent.putExtra("grupoID",remoteMessage.getData().get("grupoId"));
            intent.putExtra("usuarioEmisor",remoteMessage.getData().get("uidEmisor"));
            intent.putExtra("usuarioReceptor",remoteMessage.getData().get("uidReceptor"));
            icono = R.drawable.ic_people_black;
        } else if (remoteMessage.getData().get("id").equals("0")){
            intent = new Intent(this, LoginActivity.class);
            icono = R.drawable.ic_person_black;
        } else if (remoteMessage.getData().get("id").equals("2")){
            intent = new Intent(this, GroupChatActivity.class);
            intent.putExtra("clave",remoteMessage.getData().get("grupoId"));
            intent.putExtra("groupName",remoteMessage.getData().get("grupo"));
            icono = R.drawable.ic_chat_black;
        } else if (remoteMessage.getData().get("id").equals("3")){
            intent = new Intent(this, LoginActivity.class);
            icono = R.drawable.ic_person_near;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notificacion")
                .setSmallIcon(icono)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logo))
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setChannelId("notificacion")
                .setAutoCancel(true);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(Integer.parseInt(remoteMessage.getData().get("id")), builder.build());
    }
}
