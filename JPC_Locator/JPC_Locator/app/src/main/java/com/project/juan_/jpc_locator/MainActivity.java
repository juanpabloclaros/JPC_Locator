package com.project.juan_.jpc_locator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private Button btnSignOut, btnMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        btnSignOut = (Button) findViewById(R.id.signOut);
        btnMaps = (Button) findViewById(R.id.maps);

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
                // Cuando cierras sesion, te lleva otra vez al login por si quieres iniciar sesion con otra cuenta
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });
    }
}
