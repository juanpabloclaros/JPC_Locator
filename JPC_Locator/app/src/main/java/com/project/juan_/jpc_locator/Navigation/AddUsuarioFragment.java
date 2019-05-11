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
import com.project.juan_.jpc_locator.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddUsuarioFragment extends Fragment {

    private DatabaseReference mDatabase;
    private Button btnAddUsuario;
    private EditText txtTelefono;
    private Spinner spGrupos;
    private ProgressDialog pd;
    private boolean encontrado = false;
    private String key,nombreEmisor,nombreReceptor,tokenEmisor,tokenReceptor,grupoID;
    final Usuario usuario = new Usuario();

    public AddUsuarioFragment() {
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
        View v = inflater.inflate(R.layout.fragment_add_usuario, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        btnAddUsuario = (Button) v.findViewById(R.id.addUsuario);
        txtTelefono = (EditText) v.findViewById(R.id.usuarioTelefono);
        spGrupos = (Spinner) v.findViewById(R.id.spinnerGrupos);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos_creados").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<String> grupos = new ArrayList<>();

                // Con este for recorremos los hijos del nodo
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    // Añadimos los grupos que tenemos creados
                    grupos.add(snapshot.getValue().toString());

                }

                valoresSpinner(grupos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }

        });
    }

    private void valoresSpinner(ArrayList<String> listaGrupos) {

        // Asignamos los valores al Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, listaGrupos);
        spGrupos.setAdapter(adapter);


        // Cuando pulsemos el botón, vamos a añadir al usuario al grupo
        btnAddUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                encontrado = false;
                pd = ProgressDialog.show(getContext(),"Añadir amigo","Añadiendo amigo al grupo...");

                mDatabase.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // Guardamos el nombre y token del usuario que va a mandar la peticion de invitacion a gupo
                        nombreEmisor = dataSnapshot.child(usuario.getUsuario()).getValue(Usuario.class).getNombre();
                        tokenEmisor = dataSnapshot.child(usuario.getUsuario()).getValue(Usuario.class).getToken();
                        // Con este for recorremos los hijos del nodo
                        for(DataSnapshot misnapshot: dataSnapshot.getChildren()){

                            // Buscamos el telefono que coincida con el introducido. Una vez lo encontremos, vamos a buscar dentro de sus grupos.
                            if(Integer.toString(misnapshot.getValue(Usuario.class).getNumero()).equals(txtTelefono.getText().toString())){

                                // Una vez encontramos al usuario con ese numero de telefono, guardamos sus datos y comprobamos si ya está en el grupo
                                // al que queremos añadirle
                                key = misnapshot.getKey();
                                nombreReceptor = misnapshot.getValue(Usuario.class).getNombre();
                                tokenReceptor = misnapshot.getValue(Usuario.class).getToken();
                                obtenerClave(spGrupos.getSelectedItem().toString());

                                // Iniciamos la comprobacion
                                for(DataSnapshot data: misnapshot.child("Grupos").getChildren()){

                                    // Buscamos si el grupo seleccionado esta dentro de los grupos a los que pertenece el usuario
                                    if(grupoID.equals(data.getKey())){
                                        encontrado = true;
                                    }
                                }
                            }
                        }

                        pd.dismiss();
                        if(encontrado){
                            Toast.makeText(getContext(), "El número ya está añadido al grupo.", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getContext(), "Se ha añadido correctamente.", Toast.LENGTH_SHORT).show();
                            mDatabase.child("Usuarios_por_grupo").child(usuario.getUsuario()).child(spGrupos.getSelectedItem().toString()).push().setValue(key);
//                                        mDatabase.child("Usuarios").child(key).child("Grupos").push().setValue(spGrupos.getSelectedItem().toString());

                            Map<String,Object> valores = new HashMap<>();
                            valores.put("nombreEmisor",nombreEmisor);
                            valores.put("nombreReceptor",nombreReceptor);
                            valores.put("tokenEmisor",tokenEmisor);
                            valores.put("tokenReceptor",tokenReceptor);
                            valores.put("recibido","false");
                            valores.put("unirse","false");
                            valores.put("grupo",spGrupos.getSelectedItem().toString());
                            mDatabase.child("Notifications").child("Grupo").child(usuario.getUsuario()).child(key).setValue(valores);
                            txtTelefono.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }

                });
            }
        });
    }

    private void obtenerClave(final String grupo) {
        mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos_creados").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren())
                    if (grupo.equals(snapshot.getValue()))
                        grupoID = snapshot.getKey();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
