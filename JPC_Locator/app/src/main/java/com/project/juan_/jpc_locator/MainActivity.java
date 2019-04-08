package com.project.juan_.jpc_locator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.project.juan_.jpc_locator.Entidades.Usuario;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btnSignOut, btnMaps, btnAddGroup, btnAddUsuario, btnDeleteGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        btnSignOut = (Button) findViewById(R.id.signOut);
        btnMaps = (Button) findViewById(R.id.maps);
        btnAddGroup = (Button) findViewById(R.id.addGroupMain);
        btnDeleteGroup = (Button) findViewById(R.id.deleteGroupMain);
        btnAddUsuario = (Button) findViewById(R.id.addUsuarioMain);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Usuario usuario = new Usuario();
                usuario.setUsuario("nada");

                // Cuando cierras sesion, te lleva otra vez al login por si quieres iniciar sesion con otra cuenta
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Con este botón vas al MapsActivity
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con este botón vamos a ir al AddGroupActivity
                startActivity(new Intent(MainActivity.this, AddGroupActivity.class));
            }
        });

        btnDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con este botón vamos a ir al AddGroupActivity
                startActivity(new Intent(MainActivity.this, DeleteGroupActivity.class));
            }
        });

        btnAddUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con este botón vamos a ir a AddUsuarioActivity
                startActivity(new Intent(MainActivity.this, AddUsuarioActivity.class));
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
