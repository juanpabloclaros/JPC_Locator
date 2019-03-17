package com.project.juan_.jpc_locator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private Button btnSignOut, btnMaps, btnAddGroup, btnAddUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        btnSignOut = (Button) findViewById(R.id.signOut);
        btnMaps = (Button) findViewById(R.id.maps);
        btnAddGroup = (Button) findViewById(R.id.addGroupMain);
        btnAddUsuario = (Button) findViewById(R.id.addUsuarioMain);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

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

        btnAddUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Con este botón vamos a ir a AddUsuarioActivity
                startActivity(new Intent(MainActivity.this, AddUsuarioActivity.class));
            }
        });
    }
}
