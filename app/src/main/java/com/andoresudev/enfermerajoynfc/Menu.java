package com.andoresudev.enfermerajoynfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;

public class Menu extends AppCompatActivity {

    TextView nombre, apellido, edad, estatura, peso, telefono;



    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    //NFC
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter[] writingTagFilters;
    Tag myTag;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        nombre = findViewById(R.id.txtNombreMenu);
        apellido= findViewById(R.id.txtApellidosMenu);
        edad = findViewById(R.id.txtEdadMenu);
        estatura = findViewById(R.id.txtEstaturaMenu);
        peso = findViewById(R.id.txtPesoMenu);
        telefono = findViewById(R.id.txtTelefonoMenu);
        nfcAdapter= NfcAdapter.getDefaultAdapter(this);

        infoUser();

        //PARTE DEL NFC
        context=this;
        nfcAdapter= NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter==null){
            //Toast.makeText(this, R.string.nfc_no_supported, Toast.LENGTH_SHORT).show();
        }else{
            try {
                readfromIntent(getIntent());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            pendingIntent=PendingIntent.getActivity(this,0,new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);
            IntentFilter tagDetected= new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
            writingTagFilters = new IntentFilter[]{tagDetected};
        }
    }

    public void Recetas(View view){
        Intent i = new Intent(this, Recetas.class);
        startActivity(i);
    }

    public void Doctores(View view){
        Intent i = new Intent(this, Doctores.class);
        startActivity(i);
    }

    public void Consultorios(View view){
        Intent i = new Intent(this, Consultorios.class);
        startActivity(i);
    }

    public void Tips(View view){
        Intent i = new Intent(this, Tips.class);
        startActivity(i);
    }

    public void Perfil(View view){
        Intent i = new Intent(this, Perfil.class);
        startActivity(i);
    }

    public void Escaner(View view){
        Intent i = new Intent(this, Escaner.class);
        startActivity(i);
    }

    public void cerrarSesion(){
        Toast.makeText(this, R.string.al_SesionTerminada, Toast.LENGTH_SHORT).show();
        mAuth.signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    public void confirmarCerrarSesion(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.text_CerrarSesion);
        builder.setMessage(R.string.al_confCerrarSesion);
        builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cerrarSesion();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void infoUser(){
        String id = mAuth.getCurrentUser().getUid();
        databaseReference.child("Usuario").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nombreString = snapshot.child("nombre_user").getValue().toString();
                    String apellidoString = snapshot.child("apellidos_user").getValue().toString();
                    String edadString= snapshot.child("edad_user").getValue().toString();
                    String estaturaString= snapshot.child("estatura_user").getValue().toString();
                    String pesoString= snapshot.child("peso_user").getValue().toString();
                    String telefonoString= snapshot.child("telefono_user").getValue().toString();

                    nombre.setText(" "+nombreString);
                    apellido.setText(" "+apellidoString);
                    edad.setText(" "+edadString+" años");
                    estatura.setText(" "+estaturaString+"mts");
                    peso.setText(" "+pesoString+"Kg");
                    telefono.setText(" "+telefonoString);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void readfromIntent(Intent intent) throws UnsupportedEncodingException {
        String action = intent.getAction();
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) ||NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) ||NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[]msgs =null;
            if(rawMsgs !=null){
                msgs = new NdefMessage[rawMsgs.length];
                for(int i=0; i<rawMsgs.length;i++){
                    msgs[i]=(NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }


    private void buildTagViews(NdefMessage[]msgs) throws UnsupportedEncodingException {
        if(msgs==null||msgs.length==0) return;
        String text="";
        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[]payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding=((payload[0])&128)==0 ?"UTF-8" : "UTF-16";
        int languageCodeLength=payload[0] & 0063;

        text=new String(payload, languageCodeLength+1,payload.length-languageCodeLength-1,textEncoding);
        //DETECTOR DE TIPO DE REGISTRO CON NFC
        String[]contenido=text.split("\\|");
        switch (contenido[0]) {
            case "trat": {
                Intent i = new Intent(this, NuevaReceta.class);
                i.putExtra("fecha", contenido[1]);
                i.putExtra("nombre", contenido[2]);
                i.putExtra("apellido", contenido[3]);
                i.putExtra("edad", contenido[4]);
                i.putExtra("sexo", contenido[5]);
                i.putExtra("estatura", contenido[6]);
                i.putExtra("peso", contenido[7]);
                i.putExtra("medico", contenido[8]);
                i.putExtra("centro", contenido[9]);
                i.putExtra("diagnostico", contenido[10]);
                i.putExtra("detalles", contenido[11]);
                startActivity(i);
                finish();
                break;
            }
            case "doc": {
                Intent i = new Intent(this, NuevoDoctor.class);
                i.putExtra("nombre", contenido[1]);
                i.putExtra("apellido", contenido[2]);
                i.putExtra("especialidad", contenido[3]);
                i.putExtra("planta", contenido[4]);
                i.putExtra("cedula", contenido[5]);
                i.putExtra("telefono", contenido[6]);
                i.putExtra("correo", contenido[7]);
                startActivity(i);
                finish();
                break;
            }
            case "cons": {
                Intent i = new Intent(this, NuevoConsultorio.class);
                i.putExtra("nombre", contenido[1]);
                i.putExtra("especialidad", contenido[2]);
                i.putExtra("direccion", contenido[3]);
                i.putExtra("telefono", contenido[4]);
                i.putExtra("correo", contenido[5]);
                startActivity(i);
                finish();
                break;
            }
            case "perf": {
                Intent i = new Intent(this, NuevaReceta.class);
                i.putExtra("fecha", "");
                i.putExtra("nombre", contenido[1]);
                i.putExtra("apellido", contenido[2]);
                i.putExtra("edad", contenido[3]);
                i.putExtra("sexo", contenido[4]);
                i.putExtra("estatura", contenido[5]);
                i.putExtra("peso", contenido[6]);
                i.putExtra("medico", "");
                i.putExtra("centro", "");
                i.putExtra("diagnostico", "");
                i.putExtra("detalles", "");
                startActivity(i);
                finish();
                break;
            }
        }


    }




    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        try {
            readfromIntent(intent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(nfcAdapter!=null) nfcAdapter.disableForegroundDispatch(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfcAdapter!=null) nfcAdapter.enableForegroundDispatch(this,pendingIntent,writingTagFilters,null);
    }



}