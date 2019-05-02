package com.project.juan_.jpc_locator.Navigation;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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


public class DeleteGroupFragment extends Fragment {

    private Button btnBorrarGrupo;
    private Spinner spGrupos;

    // Creamos una referencia a la base de datos
    private DatabaseReference mDatabase;

    private ProgressDialog pd;
    final Usuario usuario = new Usuario();

    public DeleteGroupFragment() {
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
        View v = inflater.inflate(R.layout.fragment_delete_group, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        btnBorrarGrupo = (Button) v.findViewById(R.id.deleteGroup);
        spGrupos = (Spinner) v.findViewById(R.id.spinnerGrupos);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fetchGrupos();
    }

    private void fetchGrupos() {
        mDatabase.child("Usuarios_por_grupo").child(usuario.getUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<String> grupos = new ArrayList<>();

                // Con este for recorremos los hijos del nodo
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    // Añadimos los grupos que tenemos creados
                    grupos.add(snapshot.getKey());

                }

                valoresSpinner(grupos);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }

        });
    }

    private void valoresSpinner(final ArrayList<String> listaGrupos) {

        // Asignamos los valores al Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, listaGrupos);
        spGrupos.setAdapter(adapter);

        // Cuando pulsemos el botón, vamos a borrar el grupo de las distintas ramas donde aparece
        btnBorrarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd = ProgressDialog.show(getContext(),"Borrar grupo","Borrando grupo...");

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

                mDatabase.child("Usuarios_por_grupo").child(usuario.getUsuario()).child(spGrupos.getSelectedItem().toString()).removeValue();

                pd.dismiss();
                Toast.makeText(getContext(), "Se ha borrado correctamente.", Toast.LENGTH_SHORT).show();

                recargar();

            }
        });


    }

    private void recargar() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                //que hacer despues de 0.5 segundos
                assert getFragmentManager() != null;
                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.content_main);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(currentFragment).attach(currentFragment).commit();
            }
        }, 500);

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
