package com.project.juan_.jpc_locator;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyMessageService extends FirebaseMessagingService {
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
            notificationChannel.setDescription("Notificacions de JPC Locator");
            notificationChannel.setShowBadge(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void mostrarNotificacion(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, RequestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notificacion")
                .setSmallIcon(R.drawable.ic_people_black)
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
