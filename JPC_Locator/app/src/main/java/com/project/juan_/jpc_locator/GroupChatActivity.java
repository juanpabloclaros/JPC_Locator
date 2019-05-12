package com.project.juan_.jpc_locator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton enviarMensajebtn;
    private EditText entradaMensajeTxt;
    private ScrollView mScrollView;
    private TextView mostrarMensajeView;

    private String nombreGrupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        nombreGrupo = getIntent().getExtras().get("groupName").toString();
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(nombreGrupo);

        enviarMensajebtn = (ImageButton) findViewById(R.id.imageButtonId);
        entradaMensajeTxt = (EditText) findViewById(R.id.inputGroupMessage);
        mostrarMensajeView = (TextView) findViewById(R.id.group_chat_textView);
        mScrollView = (ScrollView) findViewById(R.id.scrollViewId);
    }
}
