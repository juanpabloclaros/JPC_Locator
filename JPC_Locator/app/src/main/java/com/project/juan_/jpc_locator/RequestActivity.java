package com.project.juan_.jpc_locator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RequestActivity extends AppCompatActivity {

    private Button btnAceptar, btnRechazar;
    private String uidEmisor, uidReceptor, grupo, grupoID, nombre;
    private TextView mensajeTV;
    private DatabaseReference respuesta;
    private DatabaseReference mDatabase;

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
                respuesta.child("unirse").setValue(true);
                mDatabase.child("Usuarios_por_grupo").child(grupoID).push().setValue(uidReceptor);
                mDatabase.child("Usuarios").child(uidReceptor).child("Grupos").child(grupoID).setValue(grupo);
                startActivity(new Intent(RequestActivity.this, LoginActivity.class));
                finish();
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
