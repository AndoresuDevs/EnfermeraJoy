package com.andoresudev.enfermerajoynfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContra extends AppCompatActivity {

    TextView email;
    private FirebaseAuth mAuth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contra);
        mAuth= FirebaseAuth.getInstance();
        email=findViewById(R.id.txtCorreoRecuperar);
        pd= new ProgressDialog(this);
    }

    public void Recuperar(View view){
        String mail=email.getText().toString();
        if (mail.isEmpty()){
            Toast.makeText(this, R.string.text_ingreseCorreo, Toast.LENGTH_SHORT).show();
        }else{
            //AQUI VA TODO PARA RECUPERAR LA CONTRA

            mAuth.setLanguageCode("es");
            mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RecuperarContra.this, R.string.text_contraEnviada, Toast.LENGTH_SHORT).show();
                        finish();
                    }   else{
                        Toast.makeText(RecuperarContra.this, R.string.text_contraNoEnviada, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


    public void volverAlMenu(View view){
        finish();
    }
}