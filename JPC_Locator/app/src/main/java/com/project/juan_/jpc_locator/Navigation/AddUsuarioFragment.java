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

///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link AddUsuarioFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link AddUsuarioFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class AddUsuarioFragment extends Fragment {

    private DatabaseReference mDatabase;
    private Button btnAddUsuario;
    private EditText txtTelefono;
    private Spinner spGrupos;
    private ProgressDialog pd;
    private boolean encontrado = false;
    private String key;
    final Usuario usuario = new Usuario();

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    private OnFragmentInteractionListener mListener;

    public AddUsuarioFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment AddUsuarioFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static AddUsuarioFragment newInstance(String param1, String param2) {
//        AddUsuarioFragment fragment = new AddUsuarioFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
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
                                        Toast.makeText(getContext(), "El número ya está añadido al grupo.", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(getContext(), "Se ha añadido correctamente.", Toast.LENGTH_SHORT).show();
                                        mDatabase.child("Usuarios_por_grupo").child(spGrupos.getSelectedItem().toString()).push().setValue(key);
                                        mDatabase.child("Usuarios").child(key).child("Grupos").push().setValue(spGrupos.getSelectedItem().toString());
                                        txtTelefono.setText("");
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
                                            Toast.makeText(getContext(), "Se ha añadido correctamente.", Toast.LENGTH_SHORT).show();
                                            mDatabase.child("Usuarios_por_grupo").child(spGrupos.getSelectedItem().toString()).push().setValue(snapshot.getKey());
                                            txtTelefono.setText("");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }

                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
