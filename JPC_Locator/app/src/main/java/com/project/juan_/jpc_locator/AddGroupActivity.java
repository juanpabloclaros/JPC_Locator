package com.project.juan_.jpc_locator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.juan_.jpc_locator.Entidades.Usuario;

public class AddGroupActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private Button btnAddGroup;
    private EditText txtGroup;
    private ProgressDialog pd;
    private String grupo;
    private boolean creado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        btnAddGroup = (Button) findViewById(R.id.addGroup);
        txtGroup = (EditText) findViewById(R.id.nombreGrupo);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        addGroup();
    }

        private void addGroup() {
        final Usuario usuario = new Usuario();

        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd = ProgressDialog.show(AddGroupActivity.this,"Grupos","Creando grupo...");

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        // Nos aseguramos de quitar los espacios del texto que ha introducido el usuario.
                        grupo = txtGroup.getText().toString().replace(" ","");

                        // Si no existe el hijo Grupos, es porque no hay ninguno, por lo que la primera vez añade el grupo si o si. Si esta creado, realizamos el codigo que hay dentro
                        if (snapshot.hasChild("Grupos")) {

                            // Vamos a recorrer los hijos que hay en Grupos para obtener el valor, y comprobar si el grupo que estamos introduciendo ya está añadido.
                            // Si está añadido, saltará un Toast informando de lo que ocurre, sino, añadira el grupo.
                            mDatabase.child("Grupos").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    creado = false;

                                    for(DataSnapshot data: dataSnapshot.getChildren()){
                                        if (data.getValue().equals(grupo)) {
                                            creado = true;
                                        }
                                    }

                                    if(creado){
                                        pd.dismiss();
                                        Toast.makeText(AddGroupActivity.this, "El grupo ya está creado", Toast.LENGTH_SHORT).show();
                                    }else {
                                        pd.dismiss();
                                        Toast.makeText(AddGroupActivity.this, "Se ha creado correctamente.", Toast.LENGTH_SHORT).show();
                                        // Cuando creo el grupo, lo añado a los grupos que hay, al nodo Grupos que hay dentro del usuario que lo creo para indicar que pertenece a el,
                                        // y al nodo Usuarios_por_grupo para tenerlo ya en ese grupo a la hora de buscar a todos los usuarios que pertenezcan a él
                                        mDatabase.child("Grupos").push().setValue(grupo);
                                        mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos").push().setValue(grupo);
                                        mDatabase.child("Usuarios_por_grupo").child(grupo).push().setValue(usuario.getUsuario());
                                        nextActivity();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                            });
                        }else{
                            pd.dismiss();
                            Toast.makeText(AddGroupActivity.this, "Se ha creado correctamente.", Toast.LENGTH_SHORT).show();
                            mDatabase.child("Grupos").push().setValue(grupo);
                            mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos").push().setValue(grupo);
                            mDatabase.child("Usuarios_por_grupo").child(grupo).push().setValue(usuario.getUsuario());
                            nextActivity();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

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
