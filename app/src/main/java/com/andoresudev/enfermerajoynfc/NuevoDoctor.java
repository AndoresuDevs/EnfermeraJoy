package com.andoresudev.enfermerajoynfc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andoresudev.enfermerajoynfc.models.doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.UUID;

public class NuevoDoctor extends AppCompatActivity {
    EditText nombre, apellido, especialidad, planta, cedula, telefono, correo;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_doctor);
        mAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        nombre= findViewById(R.id.txtNombreND);
        apellido= findViewById(R.id.txtApellidoND);
        especialidad= findViewById(R.id.txtEspecialidadND);
        planta= findViewById(R.id.txtPlantaND);
        cedula= findViewById(R.id.txtCedulaND);
        telefono= findViewById(R.id.txtTelefonoVD);
        correo= findViewById(R.id.txtCorreoVD);

        nombre.setText(getIntent().getStringExtra("nombre"));
        apellido.setText(getIntent().getStringExtra("apellido"));
        especialidad.setText(getIntent().getStringExtra("especialidad"));
        planta.setText(getIntent().getStringExtra("planta"));
        cedula.setText(getIntent().getStringExtra("cedula"));
        telefono.setText(getIntent().getStringExtra("telefono"));
        correo.setText(getIntent().getStringExtra("correo"));
    }

    public void confirmarSaveDoctor(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Guardar Doctor");
        builder.setMessage("¿Desea guardar un nuevo doctor?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveDoctor();
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


    private void SaveDoctor() {
        ArrayList<EditText> txts = new ArrayList<EditText>();
        String nom=nombre.getText().toString();
        String ape=apellido.getText().toString();
        String esp=especialidad.getText().toString();
        String pla=planta.getText().toString();
        String ced=cedula.getText().toString();
        String tel=telefono.getText().toString();
        String cor=correo.getText().toString();
        if (nom.isEmpty()){txts.add(nombre);}
        if (ape.isEmpty()){txts.add(apellido);}
        if(txts.isEmpty()) {
            //FILTROS SUPERADOS, COMIENZA LA SUBIDA A BD
            doctor doc = new doctor();
            doc.setId_doc(UUID.randomUUID().toString());
            doc.setIdUsu_doc(mAuth.getCurrentUser().getUid());
            doc.setNombre_doc(""+nom);
            doc.setApellido_doc(""+ape);
            doc.setEspecialidad_doc(""+esp);
            doc.setPlanta_doc(""+pla);
            doc.setCedula_doc(""+ced);
            doc.setTelefono_doc(""+tel);
            doc.setCorreo_doc(""+cor);
            try {
                databaseReference.child("Doctor").child(""+doc.getId_doc()).setValue(doc);
                Toast.makeText(this,"Agregado con Exito", Toast.LENGTH_SHORT).show();
                finish();
            }catch (Exception e){
                Toast.makeText(this,"Error al guardar la los datos del doctor", Toast.LENGTH_SHORT).show();
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
        builder.setMessage("No se guardaran al nuevo doctor ¿Desea continuar?");
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