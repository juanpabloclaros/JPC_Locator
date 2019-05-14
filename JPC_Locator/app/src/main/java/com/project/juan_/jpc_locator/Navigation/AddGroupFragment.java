package com.project.juan_.jpc_locator.Navigation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.juan_.jpc_locator.Entidades.Usuario;
import com.project.juan_.jpc_locator.R;
import com.project.juan_.jpc_locator.RequestActivity;


public class AddGroupFragment extends Fragment {

    private DatabaseReference mDatabase;
    private Button btnAddGroup;
    private EditText txtGroup;
    private ProgressDialog pd;
    private String grupo;
    private boolean creado = false;
    private Usuario usuario = new Usuario();

    public AddGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_group, container, false);

        btnAddGroup = (Button) v.findViewById(R.id.addGroup);
        txtGroup = (EditText) v.findViewById(R.id.nombreGrupo);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd = ProgressDialog.show(getContext(),"Grupos","Creando grupo...");

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        // Nos aseguramos de quitar los espacios del texto que ha introducido el usuario.
                        grupo = txtGroup.getText().toString().replace(" ","");

                        // Si no existe el hijo Grupos, es porque no hay ninguno, por lo que la primera vez añade el grupo si o si. Si esta creado, realizamos el codigo que hay dentro
                        if (snapshot.hasChild("Usuarios_por_grupo")) {

                            // Vamos a recorrer los hijos que hay en Grupos para obtener el valor, y comprobar si el grupo que estamos introduciendo ya está añadido.
                            // Si está añadido, saltará un Toast informando de lo que ocurre, sino, añadira el grupo.
                            mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos_creados").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    creado = false;

                                    for(DataSnapshot data: dataSnapshot.getChildren()){
                                        if (grupo.equals(data.getValue())) {
                                            creado = true;
                                        }
                                    }

                                    if(creado){
                                        pd.dismiss();
                                        Toast.makeText(getContext(), "El grupo ya está creado", Toast.LENGTH_SHORT).show();
                                    }else {
                                        pd.dismiss();
                                        Toast.makeText(getContext(), "Se ha creado correctamente.", Toast.LENGTH_SHORT).show();

                                        // Añadimos el grupo dentro del nodo del usuario y una vez se complete la tarea, lo añadimos a Usuarios_por_grupo con el usuario ya dentro
                                        mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos_creados").push().setValue(grupo)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos_creados").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot miSnapshot: dataSnapshot.getChildren()){
                                                            if(grupo.equals(miSnapshot.getValue())){
                                                                mDatabase.child("Usuarios_por_grupo").child(miSnapshot.getKey()).push().setValue(usuario.getUsuario());
                                                                mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos").child(miSnapshot.getKey()).setValue(grupo);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        });
                                        txtGroup.setText("");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                            });
                        }else{
                            pd.dismiss();
                            Toast.makeText(getContext(), "Se ha creado correctamente.", Toast.LENGTH_SHORT).show();

                            // Añadimos el grupo dentro del nodo del usuario y una vez se complete la tarea, lo añadimos a Usuarios_por_grupo con el usuario ya dentro
                            mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos_creados").push().setValue(grupo)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos_creados").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot miSnapshot: dataSnapshot.getChildren()){
                                                        if(grupo.equals(miSnapshot.getValue())){
                                                            mDatabase.child("Usuarios_por_grupo").child(miSnapshot.getKey()).push().setValue(usuario.getUsuario());
                                                            mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos").child(miSnapshot.getKey()).setValue(grupo);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    });
                            txtGroup.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
