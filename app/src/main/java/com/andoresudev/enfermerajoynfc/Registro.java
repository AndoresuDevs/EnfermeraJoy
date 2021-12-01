package com.andoresudev.enfermerajoynfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andoresudev.enfermerajoynfc.models.usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Registro extends AppCompatActivity {

    EditText nombre, apellido, correo, contra, contra2;
    Button btnReg;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    //VARIABLES STRING
    String nom;
    String ape;
    String email;
    String pass;
    String pass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(); //REFERENCIA A LA RAIZ DE DATABASE TIME

        nombre = findViewById(R.id.txtNombreR);
        apellido = findViewById(R.id.txtApellidosR);
        correo = findViewById(R.id.txtCorreoR);
        contra = findViewById(R.id.txtContraR);
        contra2 = findViewById(R.id.txtContra2R);
        btnReg = findViewById(R.id.btnGuardarRegistroR);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<EditText> txts = new ArrayList<EditText>();

                nom = nombre.getText().toString();
                ape = apellido.getText().toString();
                email = correo.getText().toString();
                pass = contra.getText().toString();
                pass2 = contra2.getText().toString();

                if (nom.isEmpty()){txts.add(nombre); }
                if (ape.isEmpty()){txts.add(apellido); }
                if (email.isEmpty()){txts.add(correo); }
                if (pass.isEmpty()){txts.add(contra); }
                if (pass2.isEmpty()){txts.add(contra2); }

                if(txts.isEmpty()){
                    if (pass.length() >= 6) {
                        if (pass.equals(pass2)){
                            registrarUsuario();
                        }else{
                            Toast.makeText(Registro.this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(Registro.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show();
                    }
                }else{
                    for (TextView tx: txts){
                        validation(tx);
                    }
                }



                if (!email.isEmpty() && !pass.isEmpty()) {
                    if (pass.length() >= 6) {
                        Toast.makeText(Registro.this, "DATOS CORRECTOS", Toast.LENGTH_LONG).show();
                        registrarUsuario();
                    } else {
                        Toast.makeText(Registro.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show();
                    }//ELSE
                } else {

                }//ELSE

            }
        });
    }

    private void registrarUsuario() {
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    usuario user = new usuario();
                    user.setId_user(mAuth.getCurrentUser().getUid());
                    user.setContra_user(pass);
                    user.setCorreo_user(email);
                    user.setNombre_user(nom);
                    user.setApellidos_user(ape);
                    user.setEdad_user("");
                    user.setSexo_user("");
                    user.setEstatura_user("");
                    user.setPeso_user("");
                    user.setDomicilio_user("");
                    user.setTelefono_user("");
                    user.setAlergias_user("");
                    user.setSangre_user("");
                    user.setUmf_user("");
                    user.setDetalles_user("");

                    databaseReference.child("Usuario").child(user.getId_user()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()){
                                Toast.makeText(Registro.this, "Registrado con éxito", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });

                }else{
                    Toast.makeText(Registro.this, "No se pudo registrar al usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void validation(TextView text){
        text.setError("Obligatorio");
    }

    public void volverAlMenu(View view){
        finish();
    }
}