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

public class AddUsuarioActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private Button btnAddUsuario;
    private EditText txtTelefono;
    private Spinner spGrupos;
    private ProgressDialog pd;
    private boolean encontrado = false;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_usuario);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        btnAddUsuario = (Button) findViewById(R.id.addUsuario);
        txtTelefono = (EditText) findViewById(R.id.usuarioTelefono);
        spGrupos = (Spinner) findViewById(R.id.spinnerGrupos);
        final Usuario usuario = new Usuario();

        fetchGrupos();

    }

    private void valoresSpinner(ArrayList<String> listaGrupos) {

        // Asignamos los valores al Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaGrupos);
        spGrupos.setAdapter(adapter);

        final Usuario usuario = new Usuario();

        // Cuando pulsemos el botón, vamos a añadir al usuario al grupo
        btnAddUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                encontrado = false;
                pd = ProgressDialog.show(AddUsuarioActivity.this,"Añadir amigo","Añadiendo amigo al grupo...");

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        // Si no existe el hijo Grupos, es porque no hay ninguno, por lo que la primera vez añade el grupo si o si. Si esta creado, realizamos el codigo que hay dentro
                        if (snapshot.hasChild("Usuarios_por_grupo")) {

                            mDatabase.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    // Con este for recorremos los hijos del nodo
                                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                                        // Buscamos el telefono que coincida con el introducido. Una vez lo encontremos, vamos a buscar dentro de sus grupos.
                                        if(Integer.toString(snapshot.getValue(Usuario.class).getNumero()).equals(txtTelefono.getText().toString())){

                                            key = snapshot.getKey();
                                            for(DataSnapshot data: dataSnapshot.child(snapshot.getKey()).child("Grupos").getChildren()){

                                                // Buscamos si el grupo seleccionado esta dentro de los grupos a los que pertenece el usuario
                                                if(spGrupos.getSelectedItem().toString().equals(data.getValue())){
                                                    encontrado = true;
                                                }
                                            }
                                        }
                                    }

                                    pd.dismiss();
                                    if(encontrado){
                                        Toast.makeText(AddUsuarioActivity.this, "El número ya está añadido al grupo.", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(AddUsuarioActivity.this, "Se ha añadido correctamente.", Toast.LENGTH_SHORT).show();
                                        mDatabase.child("Usuarios_por_grupo").child(spGrupos.getSelectedItem().toString()).push().setValue(key);
                                        mDatabase.child("Usuarios").child(key).child("Grupos").push().setValue(spGrupos.getSelectedItem().toString());
                                        nextActivity();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }

                            });

                        }else{
                            mDatabase.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    // Con este for recorremos los hijos del nodo
                                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                                        // Buscamos el telefono que coincida con el introducido. Una vez lo encontremos, añadimos al grupo la key de ese usuario
                                        if(Integer.toString(snapshot.getValue(Usuario.class).getNumero()).equals(txtTelefono.getText().toString())) {
                                            pd.dismiss();
                                            Toast.makeText(AddUsuarioActivity.this, "Se ha añadido correctamente.", Toast.LENGTH_SHORT).show();
                                            mDatabase.child("Usuarios_por_grupo").child(spGrupos.getSelectedItem().toString()).push().setValue(snapshot.getKey());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }

                            });
                            nextActivity();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });
    }

    private void fetchGrupos() {

        mDatabase.child("Grupos").addListenerForSingleValueEvent(new ValueEventListener() {

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
        startActivity(new Intent(AddUsuarioActivity.this, MainActivity.class));

        // Cada vez que mandamos a otra actividad, la actividad de login la eliminamos para que no se quede en segundo plano
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
