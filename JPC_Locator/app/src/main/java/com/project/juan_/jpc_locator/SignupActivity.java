package com.project.juan_.jpc_locator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.juan_.jpc_locator.Entidades.Usuario;

public class SignupActivity extends AppCompatActivity {

    // Declaramos las variables con las que vamos a enlazar a los campos que se han creado en signup.
    private EditText txtNombre, txtEmail, txtPassword, txtPasswordRepetida, txtTelefono;
    private Button btnRegistrar;

    // Declaramos la variable que nos da Firebase para llevar a cabo la autenticacion
    private FirebaseAuth mAuth;

    // Instanciamos una referencia para los usuarios e instanciamos la base de datos
    private DatabaseReference referenceUsuarios;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        txtNombre = (EditText) findViewById(R.id.registroNombre);
        txtTelefono = (EditText) findViewById(R.id.registroTelefono);
        txtEmail = (EditText) findViewById(R.id.registroEmail);
        txtPassword = (EditText) findViewById(R.id.registroPassword);
        txtPasswordRepetida = (EditText) findViewById(R.id.registroPasswordRepetida);
        btnRegistrar = (Button) findViewById(R.id.registroRegistrar);

        // Inicializamos la variable de Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        referenceUsuarios = database.getReference("Usuarios");

        // Habilitamos el evento de regitrarse
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = txtEmail.getText().toString();
                final String nombre = txtNombre.getText().toString();
                final int telefono = Integer.parseInt(txtTelefono.getText().toString());
                if(emailValido(email) && validarPassword() && validarNombre(nombre)) {
                    String password = txtPassword.getText().toString();
                    
                    // Pasamos la dirección de correo electrónico y contraseña del nuevo usuario a esta
                    // funcion para crear la nueva cuenta
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(SignupActivity.this, "Se registró correctamente", Toast.LENGTH_SHORT).show();
                                        Usuario usuario = new Usuario();
                                        usuario.setNombre(nombre);
                                        usuario.setEmail(email);
                                        usuario.setNumero(telefono);
                                        referenceUsuarios.child(String.valueOf(telefono)).setValue(usuario);

                                        // con finish se acaba la ctividad y volvera a la principal
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignupActivity.this, "Error al registrarse", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(SignupActivity.this, "Las contraseñas no coinciden o son muy pequeñas o el nombre esta vacío. Por favor, compruebe los datos insertados.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Funcion para comprobar que el correo es válido
    private boolean emailValido(CharSequence target){
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // Funcion para comprobar que la contraseña es valida
    public boolean validarPassword(){
        String password, passwordRepetida;
        password = txtPassword.getText().toString();
        passwordRepetida = txtPasswordRepetida.getText().toString();
        if(password.equals(passwordRepetida)){
            if(password.length()>=6 && password.length()<=16){
                return true;
            }else return false;
        }else return false;
    }

    public boolean validarNombre(String nombre){
        return !nombre.isEmpty();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
