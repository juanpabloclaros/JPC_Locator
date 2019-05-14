package com.project.juan_.jpc_locator;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyMessageService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        Log.e("Notificacion", remoteMessage.getData().get("titulo"));
//        Log.e("Notificacion", remoteMessage.getData().get("body"));
//        Log.e("Notificacion", remoteMessage.getData().get("id"));
        Log.e("Notificacion", remoteMessage.getData().toString());
        FirebaseDatabase.getInstance().getReference().child("Datos").push().setValue(remoteMessage.getData());
    }
}
