package com.project.juan_.jpc_locator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.project.juan_.jpc_locator.Entidades.Usuario;
import com.project.juan_.jpc_locator.Navigation.MainNavigationActivity;

public class LoginActivity extends AppCompatActivity {

    // Instanciamos los objetos
    private EditText txtEmail, txtPassword;
    private Button btnLogin, btnRegistro, btnOlvidar;
    private ProgressDialog pd;

    // Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Le damos el contenido de la vista
        setContentView(R.layout.activity_login);

        // Le damos los valores a las variables que hemos instanciado
        txtEmail = (EditText) findViewById(R.id.loginEmail);
        txtPassword = (EditText) findViewById(R.id.loginPassword);
        btnLogin = (Button) findViewById(R.id.loginLogin);
        btnRegistro = (Button) findViewById(R.id.loginRegistrar);
        btnOlvidar = (Button) findViewById(R.id.loginOlvidar);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = ProgressDialog.show(LoginActivity.this,"Iniciar sesión","Iniciando sesión, por favor espere...");
                final String email = txtEmail.getText().toString();
                if(emailValido(email) && validarPassword()) {
                    String password = txtPassword.getText().toString();
                    // Este metodo nos lo da Firebase para llevar a cabo la autenticación
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        pd.dismiss();
                                        if (mAuth.getCurrentUser().isEmailVerified()){
                                            // Sign in success, update UI with the signed-in user's information
                                            Toast.makeText(LoginActivity.this,"Ha iniciado sesión correctamente.", Toast.LENGTH_SHORT).show();
                                            Usuario usuario = new Usuario();
                                            usuario.setUsuario(mAuth.getUid());
                                            FirebaseInstanceId.getInstance().getInstanceId()
                                                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                            if (!task.isSuccessful()) {
                                                                Log.w("Aviso", "getInstanceId failed", task.getException());
                                                                return;
                                                            }

                                                            // Get new Instance ID token
                                                            String token = task.getResult().getToken();

                                                            FirebaseDatabase.getInstance().getReference().child("Usuarios").child(mAuth.getUid()).child("token").setValue(token);
                                                        }
                                                    });
                                            nextActivity();
                                        }else{
                                            Toast.makeText(LoginActivity.this, "Por favor, verifique su correo electrónico.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        pd.dismiss();
                                        Toast.makeText(LoginActivity.this,"Error, los datos no son correctos.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }else{
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this,"Error al iniciar sesión. Compruebe que los valores son correctos.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cada vez que se registra, se va a la pestaña de login
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnOlvidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // vamos a la activity de restablecer la contraseña
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    // Funcion para comprobar que el correo es válido
    private boolean emailValido(CharSequence target){
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // Funcion para comprobar que la contraseña es valida
    public boolean validarPassword(){
        String password;
        password = txtPassword.getText().toString();
        if(password.length()>=6 && password.length()<=16){
            return true;
        }else return false;
    }

    // Esta funcion va a hacer que no vuelva a iniciar sesion si ya lo ha hecho
    // Este metodo se va a llamar cada vez que entremos a la actividad
    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && currentUser.isEmailVerified()){
            Usuario usuario = new Usuario();
            usuario.setUsuario(currentUser.getUid());
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w("Aviso", "getInstanceId failed", task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            Log.w("Aviso", token);

                            FirebaseDatabase.getInstance().getReference().child("Usuarios").child(mAuth.getUid()).child("token").setValue(token);
                        }
                    });
            nextActivity();
        }
    }

    // Nos manda a la otra acitividad
    private void nextActivity(){
        startActivity(new Intent(LoginActivity.this, MainNavigationActivity.class));

        // Cada vez que mandamos a otra actividad, la actividad de login la eliminamos para que no se quede en segundo plano
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
