package com.project.juan_.jpc_locator.Navigation;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.juan_.jpc_locator.Entidades.Usuario;
import com.project.juan_.jpc_locator.R;

///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link AddGroupFragment} interface
// * to handle interaction events.
// * Use the {@link AddGroupFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class AddGroupFragment extends Fragment {

    private DatabaseReference mDatabase;
    private Button btnAddGroup;
    private EditText txtGroup;
    private ProgressDialog pd;
    private String grupo;
    private boolean creado = false;

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

        final Usuario usuario = new Usuario();

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
                            mDatabase.child("Usuarios_por_grupo").child(usuario.getUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    creado = false;

                                    for(DataSnapshot data: dataSnapshot.getChildren()){
                                        if (grupo.equals(data.getKey())) {
                                            creado = true;
                                        }
                                    }

                                    if(creado){
                                        pd.dismiss();
                                        Toast.makeText(getContext(), "El grupo ya está creado", Toast.LENGTH_SHORT).show();
                                    }else {
                                        pd.dismiss();
                                        Toast.makeText(getContext(), "Se ha creado correctamente.", Toast.LENGTH_SHORT).show();
                                        // Cuando creo el grupo, lo añado a los grupos que hay, al nodo Grupos que hay dentro del usuario que lo creo para indicar que pertenece a el,
                                        // y al nodo Usuarios_por_grupo para tenerlo ya en ese grupo a la hora de buscar a todos los usuarios que pertenezcan a él
                                        mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos").push().setValue(grupo);
                                        mDatabase.child("Usuarios_por_grupo").child(usuario.getUsuario()).child(grupo).push().setValue(usuario.getUsuario());
                                        txtGroup.setText("");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                            });
                        }else{
                            pd.dismiss();
                            Toast.makeText(getContext(), "Se ha creado correctamente.", Toast.LENGTH_SHORT).show();
                            mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos").push().setValue(grupo);
                            mDatabase.child("Usuarios_por_grupo").child(usuario.getUsuario()).child(grupo).push().setValue(usuario.getUsuario());
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
