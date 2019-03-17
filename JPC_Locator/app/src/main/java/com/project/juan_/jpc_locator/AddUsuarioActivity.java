package com.project.juan_.jpc_locator;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddUsuarioActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private Button btnAddUsuario;
    private EditText txtTelefono;
    private Spinner spGrupos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_usuario);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        btnAddUsuario = (Button) findViewById(R.id.addUsuario);
        txtTelefono = (EditText) findViewById(R.id.usuarioTelefono);
        spGrupos = (Spinner) findViewById(R.id.spinnerGrupos);

        ArrayList<String> grupos = fetchGrupos();

        // Asignamos los valores al Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, grupos);
        spGrupos.setAdapter(adapter);

        // Cuando pulsemos el botón, vamos a añadir al usuario al grupo
        btnAddUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Usuarios").child(txtTelefono.getText().toString()).child("Grupos").push().setValue(spGrupos.getSelectedItem().toString());
                nextActivity();
            }
        });
    }

    private ArrayList<String> fetchGrupos() {

        final ArrayList<String> grupos = new ArrayList<String>();

        mDatabase.child("Grupos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Con este for recorremos los hijos del nodo
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    // Añadimos los grupos que tenemos creados
                    grupos.add(String.valueOf(snapshot.getValue()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return grupos;
    }

    // Nos manda a la otra acitividad
    private void nextActivity(){
        startActivity(new Intent(AddUsuarioActivity.this, MainActivity.class));

        // Cada vez que mandamos a otra actividad, la actividad de login la eliminamos para que no se quede en segundo plano
        finish();
    }
}
