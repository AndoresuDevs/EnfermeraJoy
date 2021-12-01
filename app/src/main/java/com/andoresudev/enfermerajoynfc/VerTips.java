package com.andoresudev.enfermerajoynfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VerTips extends AppCompatActivity {

    String idTipSelected;
    TextView nombre, descripcion;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_tips);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        idTipSelected=getIntent().getStringExtra("IdTipSelected");
        infoTip(idTipSelected);
        nombre = findViewById(R.id.txtTituloTip);
        descripcion=findViewById(R.id.txtContenidoTip);
    }

    private void infoTip(String id) {
        databaseReference.child("Tip").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nom = snapshot.child("titulo_tip").getValue().toString();
                    String desc = snapshot.child("descripcion_tip").getValue().toString();
                    nombre.setText(nom);
                    descripcion.setText(desc);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}