package com.project.juan_.jpc_locator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.juan_.jpc_locator.Entidades.Usuario;

import java.util.ArrayList;

public class DeleteGroupActivity extends AppCompatActivity {

    private Button btnBorrarGrupo;
    private Spinner spGrupos;

    // Creamos una referencia a la base de datos
    private DatabaseReference mDatabase;

    private ProgressDialog pd;
    private boolean encontrado = false;
    private String key;
    final Usuario usuario = new Usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_group);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        btnBorrarGrupo = (Button) findViewById(R.id.deleteGroup);
        spGrupos = (Spinner) findViewById(R.id.spinnerGrupos);

        fetchGrupos();
    }

    private void valoresSpinner(ArrayList<String> listaGrupos) {

        // Asignamos los valores al Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaGrupos);
        spGrupos.setAdapter(adapter);

        // Cuando pulsemos el botón, vamos a borrar el grupo de las distintas ramas donde aparece
        btnBorrarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                encontrado = false;
                pd = ProgressDialog.show(DeleteGroupActivity.this,"Borrar grupo","Borrando grupo...");

                mDatabase.child("Grupos").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            // Buscamos si el grupo seleccionado esta dentro de los grupos a los que pertenece el usuario
                            if(spGrupos.getSelectedItem().toString().equals(snapshot.getValue())){
                                snapshot.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            // Buscamos si el grupo seleccionado esta dentro de los grupos a los que pertenece el usuario
                            if(spGrupos.getSelectedItem().toString().equals(snapshot.getValue())){
                                snapshot.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mDatabase.child("Usuarios_por_grupo").child(spGrupos.getSelectedItem().toString()).removeValue();

                pd.dismiss();
                Toast.makeText(DeleteGroupActivity.this, "Se ha borrado correctamente.", Toast.LENGTH_SHORT).show();

                nextActivity();
            }
        });
    }

    private void fetchGrupos() {

        mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<String> grupos = new ArrayList<>();

                // Con este for recorremos los hijos del nodo
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    // Añadimos los grupos que tenemos creados
                    grupos.add(String.valueOf(snapshot.getValue()));

                }

                valoresSpinner(grupos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }

        });
    }

    // Nos manda a la otra acitividad
    private void nextActivity(){
        startActivity(new Intent(DeleteGroupActivity.this, MainActivity.class));

        // Cada vez que mandamos a otra actividad, la actividad de login la eliminamos para que no se quede en segundo plano
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
