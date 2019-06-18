package com.project.juan_.jpc_locator;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.project.juan_.jpc_locator.Entidades.Usuario;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class MyMessageService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingServic";
    private Usuario usuario = new Usuario();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

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

    @TargetApi(Build.VERSION_CODES.O)
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
            intent.putExtra("clave_emisor",remoteMessage.getData().get("claveEmisor"));
            icono = R.drawable.ic_people_black;
        } else if (remoteMessage.getData().get("id").equals("0")){

            final String id_grupo = remoteMessage.getData().get("grupoId");

            try {
                ECDH ecdh = new ECDH();
                Log.d("clavePublica", remoteMessage.getData().get("claveReceptor"));
                byte publicKeyData[] = Base64.getDecoder().decode(remoteMessage.getData().get("claveReceptor").getBytes("UTF-8"));
                X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyData);
                KeyFactory kf = KeyFactory.getInstance("ECDH", "SC");
                PublicKey publicKey = kf.generatePublic(spec);

                SharedPreferences preferences = getSharedPreferences(id_grupo, MODE_PRIVATE);
                String clavePrivada = preferences.getString("clavePrivada","FEDCBA98765432100123456789ABCDEF");
                byte privateKeyData[] = Base64.getDecoder().decode(clavePrivada.getBytes("UTF-8"));
                PrivateKey privKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyData));

                byte[] claveCompartida = ecdh.generateSharedKey(publicKey, privKey);
                SharedPreferences.Editor editor = preferences.edit();
                String sharedKey = Base64.getEncoder().encodeToString(claveCompartida);
                editor.putString("claveCompartida", sharedKey);

            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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
