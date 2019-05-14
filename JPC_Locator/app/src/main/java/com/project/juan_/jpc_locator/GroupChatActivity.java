package com.project.juan_.jpc_locator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.juan_.jpc_locator.Entidades.Usuario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton enviarMensajebtn;
    private EditText entradaMensajeTxt;
    private Usuario usuario = new Usuario();
    private final List<Mensajes> listaMensajes = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MensajesAdapter mensajesAdapter;
    private RecyclerView listaMensajeUsuario;

    private String nombreGrupo,claveGrupo, nombreUsuario, fechaActual, tiempoActual, from;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        nombreGrupo = getIntent().getExtras().get("groupName").toString();
        claveGrupo = getIntent().getExtras().get("clave").toString();
        mToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(nombreGrupo);

        enviarMensajebtn = (ImageButton) findViewById(R.id.imageButtonId);
        entradaMensajeTxt = (EditText) findViewById(R.id.inputGroupMessage);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mensajesAdapter = new MensajesAdapter(listaMensajes);
        listaMensajeUsuario = (RecyclerView) findViewById(R.id.listaMensajesPrivadoUsuario);
        linearLayoutManager = new LinearLayoutManager(this);
        listaMensajeUsuario.setLayoutManager(linearLayoutManager);
        listaMensajeUsuario.setAdapter(mensajesAdapter);

        informacionUsuario();

        enviarMensajebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarMensaje();

                entradaMensajeTxt.setText("");

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabase.child("Chat").child(claveGrupo).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Mensajes mensajes = dataSnapshot.getValue(Mensajes.class);

                listaMensajes.add(mensajes);

                mensajesAdapter.notifyDataSetChanged();

                listaMensajeUsuario.smoothScrollToPosition(listaMensajeUsuario.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged( DataSnapshot dataSnapshot,  String s) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void informacionUsuario() {
        mDatabase.child("Usuarios").child(usuario.getUsuario()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    nombreUsuario = dataSnapshot.child("nombre").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void guardarMensaje() {
        String mensaje = entradaMensajeTxt.getText().toString();
        String mensajeKey = mDatabase.child("Chat").child(claveGrupo).push().getKey();
        from = usuario.getUsuario();

        if (TextUtils.isEmpty(mensaje)){
            Toast.makeText(this, "Por favor escriba un mensaje primero...", Toast.LENGTH_SHORT).show();
        } else{
            Calendar calendarDate = Calendar.getInstance();
            SimpleDateFormat formatoFecha = new SimpleDateFormat("MMM dd, yyyy");
            fechaActual = formatoFecha.format(calendarDate.getTime());

            Calendar calendarTime = Calendar.getInstance();
            SimpleDateFormat formatoTiempo = new SimpleDateFormat("hh:mm a");
            tiempoActual = formatoTiempo.format(calendarDate.getTime());

            HashMap<String,Object> mensajeGrupoKey = new HashMap<>();
            mDatabase.child("Chat").child(claveGrupo).updateChildren(mensajeGrupoKey);

            HashMap<String,Object> infoMensajeMap = new HashMap<>();
            infoMensajeMap.put("nombre",nombreUsuario);
            infoMensajeMap.put("mensaje",mensaje);
            infoMensajeMap.put("fecha",fechaActual);
            infoMensajeMap.put("hora",tiempoActual);
            infoMensajeMap.put("from",from);

            mDatabase.child("Chat").child(claveGrupo).child(mensajeKey).updateChildren(infoMensajeMap);
        }
    }
}
