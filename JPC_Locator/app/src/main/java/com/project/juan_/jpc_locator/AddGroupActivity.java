package com.project.juan_.jpc_locator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.juan_.jpc_locator.Entidades.Usuario;

import java.util.HashMap;
import java.util.Map;

public class AddGroupActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private Button btnAddGroup;
    private EditText txtGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        btnAddGroup = (Button) findViewById(R.id.addGroup);
        txtGroup = (EditText) findViewById(R.id.nombreGrupo);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final Usuario usuario = new Usuario();

        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Grupos").push().setValue(txtGroup.getText().toString());
                mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos").push().setValue(txtGroup.getText().toString());
                nextActivity();
            }
        });
    }

    // Nos manda a la otra acitividad
    private void nextActivity(){
        startActivity(new Intent(AddGroupActivity.this, MainActivity.class));

        // Cada vez que mandamos a otra actividad, la actividad de login la eliminamos para que no se quede en segundo plano
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
