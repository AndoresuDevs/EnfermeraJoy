package com.andoresudev.enfermerajoynfc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andoresudev.enfermerajoynfc.models.tratamiento;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.UUID;

public class NuevaReceta extends AppCompatActivity {
    EditText fecha, nombre, apellido, edad, sexo, estatura, peso, medico, centro, diagnostico, detalles;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    tratamiento tratCompartido= new tratamiento();
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_receta);
        mAuth=FirebaseAuth.getInstance();



        fecha = findViewById(R.id.txtFechaNR);
        nombre = findViewById(R.id.txtNombreNR);
        apellido = findViewById(R.id.txtApellidosP);
        edad = findViewById(R.id.txtEdadNR);
        sexo = findViewById(R.id.txtSexoNR);
        estatura = findViewById(R.id.txtEstaturaNR);
        peso = findViewById(R.id.txtPesoNR);
        medico = findViewById(R.id.txtMedicoNR);
        centro = findViewById(R.id.txtCentroNR);
        detalles = findViewById(R.id.txtDetallesNR);
        diagnostico= findViewById(R.id.txtDiagnosticoNR);

        tratCompartido.setFecha_trat(getIntent().getStringExtra("fecha"));
        tratCompartido.setNombre_trat(getIntent().getStringExtra("nombre"));
        tratCompartido.setApellido_trat(getIntent().getStringExtra("apellido"));
        tratCompartido.setEdad_trat(getIntent().getStringExtra("edad"));
        tratCompartido.setSexo_trat(getIntent().getStringExtra("sexo"));
        tratCompartido.setEstatura_trat(getIntent().getStringExtra("estatura"));
        tratCompartido.setPeso_trat(getIntent().getStringExtra("peso"));
        tratCompartido.setMedico_trat(getIntent().getStringExtra("medico"));
        tratCompartido.setCentro_trat(getIntent().getStringExtra("centro"));
        tratCompartido.setDiagnostico_trat(getIntent().getStringExtra("diagnostico"));
        tratCompartido.setDetalles_trat(getIntent().getStringExtra("detalles"));

        iniciarFirebase();
        inicializarDatosCompartidos();
    }

    private void inicializarDatosCompartidos(){
        fecha.setText(tratCompartido.getFecha_trat());
        nombre.setText(tratCompartido.getNombre_trat());
        apellido.setText(tratCompartido.getApellido_trat());
        edad.setText(tratCompartido.getEdad_trat());
        sexo.setText(tratCompartido.getSexo_trat());
        estatura.setText(tratCompartido.getEstatura_trat());
        peso.setText(tratCompartido.getPeso_trat());
        medico.setText(tratCompartido.getMedico_trat());
        centro.setText(tratCompartido.getCentro_trat());
        diagnostico.setText(tratCompartido.getDiagnostico_trat());
        detalles.setText(tratCompartido.getDetalles_trat());


    }

    private void iniciarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference();
    }

    private void SaveReceta(){
        ArrayList<EditText> txts = new ArrayList<EditText>();

        String fech = fecha.getText().toString();
        String nom = nombre.getText().toString();
        String ape = apellido.getText().toString();
        String eda = edad.getText().toString();
        String sex = sexo.getText().toString();
        String est = estatura.getText().toString();
        String pes = peso.getText().toString();
        String med = medico.getText().toString();
        String cent = centro.getText().toString();
        String diag = diagnostico.getText().toString();
        String det = detalles.getText().toString();


        if (fech.isEmpty()){txts.add(fecha); }
        if (nom.isEmpty()){txts.add(nombre); }
        if (ape.isEmpty()){txts.add(apellido); }
        if (eda.isEmpty()){txts.add(edad); }
        if (sex.isEmpty()){txts.add(sexo); }
        if (med.isEmpty()){txts.add(medico); }
        if (cent.isEmpty()){txts.add(centro); }
        if (diag.isEmpty()){txts.add(diagnostico); }
        if (det.isEmpty()){txts.add(detalles); }

        if(txts.isEmpty()){
            tratamiento trat = new tratamiento();
            trat.setId_trat(UUID.randomUUID().toString());
            trat.setId_usuTrat(mAuth.getCurrentUser().getUid());
            trat.setFecha_trat(fech);
            trat.setNombre_trat(nom);
            trat.setApellido_trat(ape);
            trat.setEdad_trat(eda);
            trat.setSexo_trat(sex);
            trat.setEstatura_trat(est);
            trat.setPeso_trat(pes);
            trat.setMedico_trat(med);
            trat.setCentro_trat(cent);
            trat.setDiagnostico_trat(diag);
            trat.setDetalles_trat(det);

            try {
                databaseReference.child("Receta").child(""+trat.getId_trat()).setValue(trat);
                Toast.makeText(this,"Agregado con Exito", Toast.LENGTH_SHORT).show();
                finish();
            }catch (Exception e){
                Toast.makeText(this,"Error al guardar la Receta", Toast.LENGTH_SHORT).show();
            }


        }else{
            for (TextView tx: txts){
                validation(tx);
            }
        }

    }

    public void confirmarGuardarReceta(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Guardar Nueva Receta");
        builder.setMessage("¿Desea guardar la nueva receta?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveReceta();
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

    public void cancelar(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancelar");
        builder.setMessage("No se guardara la nueva receta ¿desea continuar?");
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
}