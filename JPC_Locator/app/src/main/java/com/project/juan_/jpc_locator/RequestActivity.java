package com.project.juan_.jpc_locator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.juan_.jpc_locator.Entidades.Usuario;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Base64;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class RequestActivity extends AppCompatActivity {

    private Button btnAceptar, btnRechazar;
    private String uidEmisor, uidReceptor, grupo, grupoID, nombre;
    private TextView mensajeTV;
    private DatabaseReference respuesta;
    private DatabaseReference mDatabase;
    private Usuario usuario = new Usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        btnAceptar = findViewById(R.id.buttonAceptar);
        btnRechazar = findViewById(R.id.buttonRechazar);
        mensajeTV = findViewById(R.id.textView);

        uidEmisor = getIntent().getStringExtra("usuarioEmisor");
        uidReceptor = getIntent().getStringExtra("usuarioReceptor");
        grupo = getIntent().getStringExtra("grupo");
        grupoID = getIntent().getStringExtra("grupoID");
        nombre = getIntent().getStringExtra("nombre");
        respuesta = FirebaseDatabase.getInstance().getReference().child("Notifications").child("Grupo").child(uidEmisor).child(uidReceptor);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mensajeTV.setText(nombre + " quiere añadirte al grupo " + grupo);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RequestActivity.this, "Añadiendo a grupo.", Toast.LENGTH_SHORT).show();

                mDatabase.child("Notifications").child("Grupo").child(uidEmisor).child(uidReceptor).child("clave_emisor").addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        try {
                            ECDH ecdh = new ECDH();
                            Log.d("clavePublica", dataSnapshot.getValue().toString());
                            byte publicKeyData[] = Base64.getDecoder().decode(dataSnapshot.getValue().toString().getBytes("UTF-8"));
                            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyData);
                            KeyFactory kf = KeyFactory.getInstance("ECDH", "SC");
                            PublicKey publicKey = kf.generatePublic(spec);

                            byte[] claveCompartida = ecdh.generateSharedKey(publicKey, ecdh.getPrivKey());
                            SharedPreferences.Editor editor = getSharedPreferences(grupoID, MODE_PRIVATE).edit();
                            String sharedKey = Base64.getEncoder().encodeToString(claveCompartida);
                            editor.putString("claveCompartida2", sharedKey);

                            byte[] encodedPublicKey = ecdh.getPubKey().getEncoded();
                            String b64PublicKey = java.util.Base64.getEncoder().encodeToString(encodedPublicKey);

                            respuesta.child("unirse").setValue(true);
                            respuesta.child("clave_receptor").setValue(b64PublicKey);
                            mDatabase.child("Usuarios_por_grupo").child(grupoID).push().setValue(uidReceptor);
                            mDatabase.child("Usuarios").child(uidReceptor).child("Grupos").child(grupoID).setValue(grupo);
                            startActivity(new Intent(RequestActivity.this, LoginActivity.class));
                            finish();
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });

        btnRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RequestActivity.this, "Invitación rechazada.", Toast.LENGTH_SHORT).show();
                respuesta.child("recibido").setValue(true);
                startActivity(new Intent(RequestActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
