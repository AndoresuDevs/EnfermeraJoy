package com.andoresudev.enfermerajoynfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.andoresudev.enfermerajoynfc.adapters.doctores_adapter;
import com.andoresudev.enfermerajoynfc.models.doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Doctores extends AppCompatActivity {
    ListView tblDoctores;
    ImageButton btnAdd;

    ArrayList<doctor> listDocs;
    doctor docSelected = new doctor();

    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctores);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();

        tblDoctores= findViewById(R.id.tblDoctores);
        btnAdd=findViewById(R.id.btnAgregarDoctor);
        listDocs= new ArrayList<doctor>();
        mostrarDocs();

        tblDoctores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                docSelected= (doctor) parent.getItemAtPosition(position);
                String idDocSel = docSelected.getId_doc().toString();
                abrirDoc(idDocSel);
            }
        });
    }
    private void abrirDoc(String id){

        Intent i =new Intent(this, VerDoctores.class);
        i.putExtra("IdDocSelected", id);
        startActivity(i);
    }

    private void mostrarDocs() {
        String id =mAuth.getCurrentUser().getUid();
        databaseReference.child("Doctor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listDocs.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    doctor d = ds.getValue(doctor.class);

                    if (d.getIdUsu_doc().equals(id)){
                        listDocs.add(d);
                    }
                    doctores_adapter adapter = new doctores_adapter(Doctores.this,  listDocs);
                    tblDoctores.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void nuevoDoc(View view){
        Intent i = new Intent(this, NuevoDoctor.class);
        i.putExtra("nombre", "");
        i.putExtra("apellido", "");
        i.putExtra("especialidad", "");
        i.putExtra("planta", "");
        i.putExtra("cedula", "");
        i.putExtra("telefono", "");
        i.putExtra("correo", "");
        startActivity(i);
        finish();
    }



}