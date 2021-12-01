package com.andoresudev.enfermerajoynfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.andoresudev.enfermerajoynfc.adapters.recetas_adapter;
import com.andoresudev.enfermerajoynfc.models.tratamiento;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Recetas extends AppCompatActivity {
    ListView tblRecetas;
    ImageButton btnAdd;

    ArrayList<tratamiento> listaTrats;
    ArrayAdapter<tratamiento> arrayAdapterTrats;
    tratamiento TratSelected = new tratamiento();

    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recetas);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();

        tblRecetas= findViewById(R.id.tblRecetas);
        btnAdd=findViewById(R.id.btnAgregarReceta);

        listaTrats= new  ArrayList<tratamiento>();
        mostrarTrats();

        tblRecetas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TratSelected= (tratamiento) parent.getItemAtPosition(position);
                String idTratSel = TratSelected.getId_trat().toString();
                abrirReceta(idTratSel);
            }
        });


    }

    private void abrirReceta(String id){
        Intent i =new Intent(this, VerRecetas.class);
        i.putExtra("IdTratSelected", id);
        startActivity(i);
    }

    private void mostrarTrats() {
        String id =mAuth.getCurrentUser().getUid();
        databaseReference.child("Receta").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaTrats.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    tratamiento t = ds.getValue(tratamiento.class);
                    if (t.getId_usuTrat().equals(id)){
                        listaTrats.add(t);
                    }


                    recetas_adapter adap = new recetas_adapter(Recetas.this, listaTrats);
                    tblRecetas.setAdapter(adap);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void addReceta(View view) {
        Intent i = new Intent(this, NuevaReceta.class);
        i.putExtra("fecha", "");
        i.putExtra("nombre", "");
        i.putExtra("apellido", "");
        i.putExtra("edad", "");
        i.putExtra("sexo", "");
        i.putExtra("estatura", "");
        i.putExtra("peso", "");
        i.putExtra("medico", "");
        i.putExtra("centro", "");
        i.putExtra("diagnostico", "");
        i.putExtra("detalles", "");
        startActivity(i);

    }


}