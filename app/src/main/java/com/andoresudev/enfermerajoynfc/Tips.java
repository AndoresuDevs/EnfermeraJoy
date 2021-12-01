package com.andoresudev.enfermerajoynfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.andoresudev.enfermerajoynfc.adapters.tips_adapter;
import com.andoresudev.enfermerajoynfc.models.tip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Tips extends AppCompatActivity {
    ListView tblTips;
    ArrayList<tip> listTips;
    //ArrayAdapter<tip> adapter;

    tip tipSelected = new tip();
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        tblTips=findViewById(R.id.tblTips);
        listTips= new ArrayList<tip>();
        mostrarTips();
        tblTips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tipSelected= (tip) parent.getItemAtPosition(position);
                String idTipSel = tipSelected.getId_tip();
                abrirTip(idTipSel);
            }
        });
    }

    private void abrirTip(String id){
        Intent i =new Intent(this, VerTips.class);
        i.putExtra("IdTipSelected", id);
        startActivity(i);
    }

    private void mostrarTips() {
        databaseReference.child("Tip").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTips.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    tip t = new tip();
                    String id =  ds.child("id_tip").getValue().toString();
                    String titulo = ds.child("titulo_tip").getValue().toString();
                    String descripcion= ds.child("descripcion_tip").getValue().toString();
                    t.setId_tip(id);
                    t.setTitulo_tip(titulo);
                    t.setDescripcion_tip(descripcion);
                    listTips.add(t);

                    //adapter = new ArrayAdapter<tip>(Tips.this, android.R.layout.simple_list_item_1,listTips);
                    tips_adapter adap = new tips_adapter(Tips.this, listTips);
                    tblTips.setAdapter(adap);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}