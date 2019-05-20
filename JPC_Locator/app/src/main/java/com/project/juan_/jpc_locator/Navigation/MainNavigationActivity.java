package com.project.juan_.jpc_locator.Navigation;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.juan_.jpc_locator.Entidades.Usuario;
import com.project.juan_.jpc_locator.LoginActivity;
import com.project.juan_.jpc_locator.R;

import java.util.Objects;

public class MainNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView titleTV, subtitleTV;
    private Usuario usuario = new Usuario();
    private DatabaseReference usuarioRef;
    private Boolean gruposCreados = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        titleTV = (TextView) headerView.findViewById(R.id.textViewTitle);
        subtitleTV = (TextView) headerView.findViewById(R.id.textViewSubtitle);
        usuarioRef = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(usuario.getUsuario());

        FirebaseDatabase.getInstance().getReference()
                .child("Usuarios").child(usuario.getUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    titleTV.setText(Objects.requireNonNull(dataSnapshot.getValue(Usuario.class)).getNombre());
                    subtitleTV.setText(Objects.requireNonNull(dataSnapshot.getValue(Usuario.class)).getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        getSupportActionBar().setTitle("Inicio");
        Fragment fragment = new MapsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut();

            Usuario usuario = new Usuario();
            usuario.setUsuario("nada");

            // Cuando cierras sesion, te lleva otra vez al login por si quieres iniciar sesion con otra cuenta
            startActivity(new Intent(MainNavigationActivity.this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Grupos_creados"))
                    gruposCreados = true;
                else
                    gruposCreados = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment miFragment = null;
        boolean fragmentSeleccionado = false;

        if (id == R.id.nav_home) {
            miFragment = new MapsFragment();
            getSupportActionBar().setTitle("Inicio");
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_chat) {
            miFragment = new GroupsFragment();
            getSupportActionBar().setTitle("Grupos");
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_add_group) {
            miFragment = new AddGroupFragment();
            getSupportActionBar().setTitle("Crear Grupo");
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_add_usuario) {
            if (gruposCreados){
                miFragment = new AddUsuarioFragment();
                getSupportActionBar().setTitle("Añadir Usuario");
                fragmentSeleccionado = true;
            } else{
                Toast.makeText(this, "No tienes grupos creados. Crea un grupo antes.", Toast.LENGTH_SHORT).show();
                miFragment = new AddGroupFragment();
                getSupportActionBar().setTitle("Crear Grupo");
                fragmentSeleccionado = true;
            }

        } else if (id == R.id.nav_leave_group) {

            if (gruposCreados){
                miFragment = new LeaveGroupFragment();
                getSupportActionBar().setTitle("Dejar Grupo");
                fragmentSeleccionado = true;
            } else{
                Toast.makeText(this, "No perteneces a ningún grupo. ¿Quieres crear uno?", Toast.LENGTH_SHORT).show();
                miFragment = new AddGroupFragment();
                getSupportActionBar().setTitle("Crear Grupo");
                fragmentSeleccionado = true;
            }
        }

        if(fragmentSeleccionado){
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,miFragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
