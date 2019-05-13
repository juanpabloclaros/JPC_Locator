package com.project.juan_.jpc_locator.Navigation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.juan_.jpc_locator.Entidades.Usuario;
import com.project.juan_.jpc_locator.GroupChatActivity;
import com.project.juan_.jpc_locator.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GroupsFragment extends Fragment {

    private View groupFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listGroups = new ArrayList<>();
    private ArrayList<String> claves = new ArrayList<>();
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

        // Con esto vamos a ir al chat de cada grupo
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nombreGrupo = parent.getItemAtPosition(position).toString();

                Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
                groupChatIntent.putExtra("groupName",nombreGrupo);
                groupChatIntent.putExtra("clave",claves.get(position));
                startActivity(groupChatIntent);
            }
        });

        return groupFragmentView;
    }


    private void inicializacion() {
        list_view = (ListView) groupFragmentView.findViewById(R.id.list_view);

        //Vamos a darle el contexto de nuestro groupsfragment
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1,listGroups);
        list_view.setAdapter(arrayAdapter);
    }

    private void mostrarGrupos() {
        mDatabase.child("Usuarios").child(usuario.getUsuario()).child("Grupos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<String> lista = new ArrayList<>();

                for (DataSnapshot data: dataSnapshot.getChildren()){
                    lista.add(data.getValue().toString());
                    claves.add(data.getKey());
                }

                listGroups.clear();
                listGroups.addAll(lista);

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
