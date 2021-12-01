package com.andoresudev.enfermerajoynfc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andoresudev.enfermerajoynfc.models.consultorio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.UUID;

public class NuevoConsultorio extends AppCompatActivity {
    EditText nombre, especialidad, direccion, telefono, correo;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    consultorio consComaprtido = new consultorio();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_consultorio);
        mAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        nombre= findViewById(R.id.txtNombreNC);
        especialidad= findViewById(R.id.txtEspecialidadNC);
        direccion= findViewById(R.id.txtDireccionNC);
        telefono= findViewById(R.id.txtTelefonoNC);
        correo= findViewById(R.id.txtCorreoNC);

        nombre.setText(getIntent().getStringExtra("nombre"));
        especialidad.setText(getIntent().getStringExtra("especialidad"));
        direccion.setText(getIntent().getStringExtra("direccion"));
        telefono.setText(getIntent().getStringExtra("telefono"));
        correo.setText(getIntent().getStringExtra("correo"));
    }

    public void confirmarSaveConsultorio(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Guardar");
        builder.setMessage("¿Desea guardar los datos de un nuevo consultorio?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveConsultorio();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void saveConsultorio(){
        ArrayList<EditText> txts = new ArrayList<EditText>();
        String nom=nombre.getText().toString();
        String esp=especialidad.getText().toString();
        String dir=direccion.getText().toString();
        String tel=telefono.getText().toString();
        String cor=correo.getText().toString();

        if (nom.isEmpty()){txts.add(nombre);}
        if (dir.isEmpty()){txts.add(direccion);}

        if(txts.isEmpty()) {
            //FILTROS SUPERADOS, COMIENZA LA SUBIDA A BD
            consultorio con = new consultorio();
            con.setId_cons(UUID.randomUUID().toString());
            con.setIdUsu_cons(mAuth.getCurrentUser().getUid());
            con.setNombre_cons(nom);
            con.setEspecialidad_cons(esp);
            con.setDireccion_cons(dir);
            con.setTelefono_cons(tel);
            con.setCorreo_cons(cor);


            try {
                databaseReference.child("Consultorio").child("" + con.getId_cons()).setValue(con);
                Toast.makeText(this, "Agregado con Exito", Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Error al guardar la los datos del consultorio", Toast.LENGTH_SHORT).show();
            }

        }else{
            for (TextView tx: txts){
                validation(tx);
            }
        }
    }

    public void cancelar(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancelar");
        builder.setMessage("No se guardaran los cambios ¿desea continuar?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void validation(TextView text){
        text.setError("Obligatorio");
    }
}