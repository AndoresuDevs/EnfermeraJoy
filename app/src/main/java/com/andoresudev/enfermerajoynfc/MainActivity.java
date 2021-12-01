package com.andoresudev.enfermerajoynfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private TextView email, password;
    private Button iniciar;

    private FirebaseAuth mAuth;

    private String mail="", pass="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        email=findViewById(R.id.txtCorreo);
        password=findViewById(R.id.txtPassword);
        iniciar=findViewById(R.id.btnIniciarSesion);
        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail = email.getText().toString();
                pass = password.getText().toString();

                if (!mail.isEmpty() && !pass.isEmpty()) {
                    loginUser();
                } else {
                    Toast.makeText(MainActivity.this, R.string.toast_completeCampos, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginUser() {

        mAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this, Menu.class));
                    Toast.makeText(MainActivity.this, R.string.toast_bienvenido, Toast.LENGTH_SHORT).show();
                    finish();
                }   else{
                    Toast.makeText(MainActivity.this,R.string.toast_errorAlIniciar , Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this, Menu.class));
            finish();
        }
    }

    public void RegistroEnMenu(View view){
        startActivity(new Intent(MainActivity.this, Registro.class));

    }

    public void Recuperar(View view){
        startActivity(new Intent(MainActivity.this, RecuperarContra.class));
    }
}