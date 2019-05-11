package com.project.juan_.jpc_locator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyMessageService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("Notificacion", remoteMessage.getData().get("titulo"));
        Log.e("Notificacion", remoteMessage.getData().get("body"));
        Log.e("Notificacion", remoteMessage.getData().get("id"));
    }
}
