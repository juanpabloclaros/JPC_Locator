package com.project.juan_.jpc_locator.Entidades;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Usuario {
    private String nombre;
    private String email;
    private int numero;
    private DatabaseReference mDatabase;
    private static String usuario;

    public Usuario() {
    }

    public static String getUsuario() {
        return usuario;
    }

    public void setUsuario(final String usuario) {

        if(usuario.equals("nada")){
            Usuario.usuario = null;
        }

        else{
            // Instanciamos la base de datos
            mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Con este for recorremos los hijos del nodo
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                        // Guardamos los valores en una clase
                        valoresPojo valor = snapshot.getValue(valoresPojo.class);
                        Log.e("Key",snapshot.getKey());

                        // Obtenemos el telefono que tiene ese email
                        if(valor.getEmail().equals(usuario)){
                            Usuario.usuario = snapshot.getKey();
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
