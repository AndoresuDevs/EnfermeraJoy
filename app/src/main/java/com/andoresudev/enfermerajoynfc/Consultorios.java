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

import com.andoresudev.enfermerajoynfc.adapters.consultorios_adapter;
import com.andoresudev.enfermerajoynfc.models.consultorio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Consultorios extends AppCompatActivity {

    ListView tblConsultorios;
    ImageButton btnAdd;

    ArrayList<consultorio> listCons;
    ArrayAdapter<consultorio> adapter;
    consultorio conSelected = new consultorio();

    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultorios);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();

        tblConsultorios= findViewById(R.id.tblConsultorios);
        btnAdd=findViewById(R.id.btnAgregarConsultorio);
        listCons= new ArrayList<consultorio>();
        mostrarDocs();
        tblConsultorios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                conSelected= (consultorio) parent.getItemAtPosition(position);
                String idConsSel = conSelected.getId_cons().toString();
                abrirCons(idConsSel);
            }
        });
    }

    private void abrirCons(String id){

        Intent i =new Intent(this, VerConsultorios.class);
        i.putExtra("IdConSelected", id);
        startActivity(i);
    }

    private void mostrarDocs() {
        String id =mAuth.getCurrentUser().getUid();
        databaseReference.child("Consultorio").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCons.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    consultorio c = ds.getValue(consultorio.class);

                    if (c.getIdUsu_cons().equals(id)){
                        listCons.add(c);
                    }
                    consultorios_adapter adapter = new consultorios_adapter(Consultorios.this, listCons);
                    tblConsultorios.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void nuevoCons(View view){
        Intent i = new Intent(this, NuevoConsultorio.class);
        i.putExtra("nombre", "");
        i.putExtra("especialidad", "");
        i.putExtra("direccion", "");
        i.putExtra("telefono", "");
        i.putExtra("correo", "");
        startActivity(i);
        finish();
    }
}