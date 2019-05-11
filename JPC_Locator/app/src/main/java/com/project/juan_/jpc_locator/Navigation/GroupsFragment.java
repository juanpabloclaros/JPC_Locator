package com.project.juan_.jpc_locator.Navigation;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.juan_.jpc_locator.Entidades.Usuario;
import com.project.juan_.jpc_locator.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GroupsFragment extends Fragment {

    private View groupFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listGroups = new ArrayList<>();
    private Usuario usuario = new Usuario();

    private DatabaseReference mDatabase;

    public GroupsFragment() {
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
        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Vamos a inicializar todas las variables y asignarle al listView los diferentes grupos que tenemos
        inicializacion();

        mostrarGrupos();

        return groupFragmentView;
    }


    private void inicializacion() {
        list_view = (ListView) groupFragmentView.findViewById(R.id.list_view);

        //Vamos a darle el contexto de nuestro groupsfragment
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1,listGroups);
        list_view.setAdapter(arrayAdapter);
    }

    private void mostrarGrupos() {
        mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos_creados").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();

                // Vamos a ir iterando entre los diferentes nodos
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getValue().toString());
                }

                listGroups.clear();
                listGroups.addAll(set);

                // Para ver los cambios que ocurra en el listView, usamos esta función
                arrayAdapter.notifyDataSetChanged();
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
