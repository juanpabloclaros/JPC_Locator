package com.project.juan_.jpc_locator;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText txtEmail;
    private Button btnEnviar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        txtEmail = (EditText) findViewById(R.id.olvidarPassword);
        btnEnviar = (Button) findViewById(R.id.olvidarEnviar);
        mAuth = FirebaseAuth.getInstance();

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(txtEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ForgotPasswordActivity.this, "Por favor, compruebe su correo para restablecer su contraseña.", Toast.LENGTH_SHORT).show();
                            nextActivity();
                        }else{
                            Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    // Nos manda a la otra acitividad
    private void nextActivity(){
        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));

        // Cada vez que mandamos a otra actividad, la actividad la eliminamos para que no se quede en segundo plano
        finish();
    }
}
