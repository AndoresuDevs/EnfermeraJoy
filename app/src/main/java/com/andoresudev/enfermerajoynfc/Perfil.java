package com.andoresudev.enfermerajoynfc;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andoresudev.enfermerajoynfc.models.usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;

public class Perfil extends AppCompatActivity {

    EditText nombre, apellidos, edad, sexo, estatura, peso, domicilio, telefono, alergias, sangre, umf, detalles;
    ImageButton btnCancelar, btnEditar, btnGuardar;

    String nombreString, apellidosString, edadString, sexoString, estaturaString, pesoString, domicilioString, telefonoString,
            alergiasString, sangreString, umfString, detallesString;
    FloatingActionButton btnCompartir;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    //TARJETAS NFC
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter[] writingTagFilters;
    boolean writeMode;
    Tag myTag;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        mAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        btnCancelar= findViewById(R.id.btnCancelarPerfil);
        btnEditar= findViewById(R.id.btnEditarPerfil);
        btnGuardar= findViewById(R.id.btnActualizarPerfil);

        nombre = findViewById(R.id.txtNombreP);
        apellidos= findViewById(R.id.txtApellidosP);
        edad= findViewById(R.id.txtEdadP);
        sexo= findViewById(R.id.txtSexoP);
        estatura= findViewById(R.id.txtEstaturaP);
        peso= findViewById(R.id.txtPesoP);
        domicilio= findViewById(R.id.txtDomicilioP);
        telefono= findViewById(R.id.txtTelefonoP);
        alergias= findViewById(R.id.txtAlergiasP);
        sangre= findViewById(R.id.txtSangreP);
        umf= findViewById(R.id.txtUmfP);
        detalles= findViewById(R.id.txtDetallesP);

        btnCompartir=findViewById(R.id.compartirPerfil);
        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builderCompartir();
            }
        });

        infoUser();                                                  ///////////////////////////////////////////
        editableOFF();
        colorGris();

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

    private void infoUser(){
        String id = mAuth.getCurrentUser().getUid();
        databaseReference.child("Usuario").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()){
                    nombreString = snapshot.child("nombre_user").getValue().toString();
                    apellidosString =snapshot.child("apellidos_user").getValue().toString();
                    edadString=snapshot.child("edad_user").getValue().toString();
                    sexoString=snapshot.child("sexo_user").getValue().toString();
                    estaturaString=snapshot.child("estatura_user").getValue().toString();
                    pesoString=snapshot.child("peso_user").getValue().toString();
                    domicilioString=snapshot.child("domicilio_user").getValue().toString();
                    telefonoString=snapshot.child("telefono_user").getValue().toString();
                    alergiasString=snapshot.child("alergias_user").getValue().toString();
                    sangreString=snapshot.child("sangre_user").getValue().toString();
                    umfString=snapshot.child("umf_user").getValue().toString();
                    detallesString=snapshot.child("detalles_user").getValue().toString();

                    nombre.setText(nombreString);
                    apellidos.setText(apellidosString);
                    edad.setText(edadString);
                    sexo.setText(sexoString);
                    estatura.setText(estaturaString);
                    peso.setText(pesoString);
                    domicilio.setText(domicilioString);
                    telefono.setText(telefonoString);
                    alergias.setText(alergiasString);
                    sangre.setText(sangreString);
                    umf.setText(umfString);
                    detalles.setText(detallesString);
                }
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });


    }

    public void EditarPerfil(View view){
        editableON();
        colorNegro();
        btnCancelar.setVisibility(View.VISIBLE);
        btnGuardar.setVisibility(View.VISIBLE);
        btnEditar.setVisibility(View.GONE);
    }

    public void CancelarPerfil(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancelar");
        builder.setMessage("No se guardaran los cambios ¿desea continuar?");
        builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editableOFF();
                colorGris();
                btnCancelar.setVisibility(View.GONE);
                btnGuardar.setVisibility(View.GONE);
                btnEditar.setVisibility(View.VISIBLE);
                infoUser();
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

    public void ActualizarPerfil(){
        ArrayList<EditText> txts = new ArrayList<EditText>();
        usuario user = new usuario();
        nombreString=nombre.getText().toString();
        apellidosString=apellidos.getText().toString();
        edadString=edad.getText().toString();
        sexoString=sexo.getText().toString();
        estaturaString=estatura.getText().toString();
        pesoString=peso.getText().toString();
        domicilioString=domicilio.getText().toString();
        telefonoString=telefono.getText().toString();
        alergiasString=alergias.getText().toString();
        sangreString=sangre.getText().toString();
        umfString=umf.getText().toString();
        detallesString=detalles.getText().toString();

        if(nombreString.isEmpty()){txts.add(nombre);}
        if(apellidosString.isEmpty()){txts.add(apellidos);}
        if(edadString.isEmpty()){txts.add(edad);}
        if(sexoString.isEmpty()){txts.add(sexo);}

        user.setId_user(mAuth.getCurrentUser().getUid());
        user.setNombre_user(nombreString);
        user.setApellidos_user(apellidosString);
        user.setEdad_user(edadString);
        user.setSexo_user(sexoString);
        user.setEstatura_user(estaturaString);
        user.setPeso_user(pesoString);
        user.setDomicilio_user(domicilioString);
        user.setTelefono_user(telefonoString);
        user.setAlergias_user(alergiasString);
        user.setSangre_user(sangreString);
        user.setUmf_user(umfString);
        user.setDetalles_user(detallesString);

        if(txts.isEmpty()){
            databaseReference.child("Usuario").child(user.getId_user()).setValue(user);
            Toast.makeText(this, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show();
            editableOFF();
            colorGris();
            btnCancelar.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.GONE);
            btnEditar.setVisibility(View.VISIBLE);
        }else{
            for(EditText tx: txts){
                validation(tx);
            }
        }
    }

    public void confirmarEditarPerfil(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Actualizar Perfil");
        builder.setMessage("¿Desea guardar los cambios? Esta acción no se puede deshacer");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActualizarPerfil();
                colorGris();
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

    private void editableOFF(){
        nombre.setEnabled(false);
        apellidos.setEnabled(false);
        edad.setEnabled(false);
        sexo.setEnabled(false);
        estatura.setEnabled(false);
        peso.setEnabled(false);
        domicilio.setEnabled(false);
        telefono.setEnabled(false);
        alergias.setEnabled(false);
        sangre.setEnabled(false);
        umf.setEnabled(false);
        detalles.setEnabled(false);

    }

    private void editableON(){
        nombre.setEnabled(true);
        apellidos.setEnabled(true);
        edad.setEnabled(true);
        sexo.setEnabled(true);
        estatura.setEnabled(true);
        peso.setEnabled(true);
        domicilio.setEnabled(true);
        telefono.setEnabled(true);
        alergias.setEnabled(true);
        sangre.setEnabled(true);
        umf.setEnabled(true);
        detalles.setEnabled(true);

    }

    private void colorGris(){
        nombre.setTextColor(getColor(R.color.grey));
        apellidos.setTextColor(getColor(R.color.grey));
        edad.setTextColor(getColor(R.color.grey));
        sexo.setTextColor(getColor(R.color.grey));
        estatura.setTextColor(getColor(R.color.grey));
        peso.setTextColor(getColor(R.color.grey));
        domicilio.setTextColor(getColor(R.color.grey));
        telefono.setTextColor(getColor(R.color.grey));
        alergias.setTextColor(getColor(R.color.grey));
        sangre.setTextColor(getColor(R.color.grey));
        umf.setTextColor(getColor(R.color.grey));
        detalles.setTextColor(getColor(R.color.grey));
    }

    private void colorNegro(){
        nombre.setTextColor(getColor(R.color.black));
        apellidos.setTextColor(getColor(R.color.black));
        edad.setTextColor(getColor(R.color.black));
        sexo.setTextColor(getColor(R.color.black));
        estatura.setTextColor(getColor(R.color.black));
        peso.setTextColor(getColor(R.color.black));
        domicilio.setTextColor(getColor(R.color.black));
        telefono.setTextColor(getColor(R.color.black));
        alergias.setTextColor(getColor(R.color.black));
        sangre.setTextColor(getColor(R.color.black));
        umf.setTextColor(getColor(R.color.black));
        detalles.setTextColor(getColor(R.color.black));
    }

    private void validation(TextView text){
        text.setError("Obligatorio");
    }

    private void builderCompartir(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setCancelable(false);
        LayoutInflater li = getLayoutInflater();
        View vis = li.inflate(R.layout.alert_dialog_compartir,null);
        builder.setView(vis);

        ImageView btnCancelar = vis.findViewById(R.id.btnCancelarCompartir);
        ImageView btnNFC = vis.findViewById(R.id.btnNFC);
        ImageView btnQR = vis.findViewById(R.id.btnQR);

        AlertDialog alertDialog = builder.create();

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        btnNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //BOTON PARA COMPARTIR CON NFC
                if(nfcAdapter==null){
                    Toast.makeText(Perfil.this,R.string.nfc_no_supported,Toast.LENGTH_SHORT).show();
                }else{
                    if(nfcAdapter.isEnabled()){
                        try {
                            if(myTag==null){
                                Toast.makeText(Perfil.this,R.string.error_detected,Toast.LENGTH_SHORT).show();
                            }else{
                                String mensajeNFC=empaquetarMensaje();
                                write(mensajeNFC, myTag);
                                Toast.makeText(Perfil.this,R.string.write_succes,Toast.LENGTH_SHORT).show();
                            }
                        }catch (IOException | FormatException e){
                            Toast.makeText(Perfil.this,R.string.write_error,Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(Perfil.this,R.string.nfc_disabled,Toast.LENGTH_SHORT).show();
                    }
                }



            }
        });

        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Perfil.this).setCancelable(true);
                LayoutInflater li = getLayoutInflater();
                View vis1 = li.inflate(R.layout.qr,null);
                builder.setView(vis1);
                ImageView areaQR = vis1.findViewById(R.id.generadorQR);
                AlertDialog alertDialog = builder.create();
                //AQUI METO PARA MSOTRAR EL QR
                String mensajeQR=empaquetarMensaje();

                try {
                    areaQR.setImageBitmap(generateQrCode(mensajeQR));
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                alertDialog.show();
            }
        });

        alertDialog.show();

    }

    //NFC
    private String empaquetarMensaje() {
        String text="perf|"; //identificador para saber k ventana abrir
        text=text+nombre.getText().toString()+"|";
        text=text+apellidos.getText().toString()+"|";
        text=text+edad.getText().toString()+"|";
        text=text+sexo.getText().toString()+"|";
        text=text+estatura.getText().toString()+"|";
        text=text+peso.getText().toString()+"|";
        text=text+domicilio.getText().toString()+"|";
        text=text+telefono.getText().toString()+"|";
        text=text+alergias.getText().toString()+"|";
        text=text+sangre.getText().toString()+"|";
        text=text+umf.getText().toString()+"|";
        text=text+detalles.getText().toString();

        return text;
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
        //DETECTOR DE TIPO DE REGISTRO
        //Toast.makeText(this,"TARJETA NFC LISTA PARA ESCRIBIR",Toast.LENGTH_LONG).show();
        String[]trat=text.split("\\|");
        Toast.makeText(this,"TARJETA NFC LISTA PARA ESCRIBIR",Toast.LENGTH_SHORT).show();

    }

    private void write (String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] records ={createRecord(text)};
        NdefMessage message = new NdefMessage(records);
        //get the instance of Ndef for the tag
        Ndef ndef = Ndef.get(tag);
        //Enable I/O
        ndef.connect();
        //write message
        ndef.writeNdefMessage(message);
        //Close the conection
        ndef.close();

    }


    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang ="en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes=lang.getBytes(StandardCharsets.US_ASCII);
        int langLength = langBytes.length;
        int textLength= textBytes.length;
        byte[]payload = new byte[1+langLength+textLength];

        //set status byte (see NDEF specfor actual bits)
        payload[0]=(byte) langLength;

        //copy langbytes and textbytes into payload
        System.arraycopy(langBytes,0,payload,1, langLength);
        System.arraycopy(textBytes,0,payload,1+langLength, textLength);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_TEXT, new byte[0],payload);


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
        WriteModeOff();

    }

    @Override
    protected void onResume() {
        super.onResume();
        WriteModeOn();
    }

    private void WriteModeOn(){
        writeMode=true;
        if(nfcAdapter!=null) nfcAdapter.enableForegroundDispatch(this,pendingIntent,writingTagFilters,null);
    }

    private void WriteModeOff(){
        writeMode=false;
        if(nfcAdapter!=null) nfcAdapter.disableForegroundDispatch(this);
    }

    //QR
    public static Bitmap generateQrCode(String myCodeText) throws WriterException {
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        int size = 256;

        BitMatrix bitMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
        int width =  bitMatrix.getWidth();
        System.out.println("BIT MATRIX 1: "+bitMatrix);
        Bitmap bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                bmp.setPixel(x,y, bitMatrix.get(x, y)==true ? Color.BLACK : Color.WHITE);
            }
        }
        System.out.println("Bmp MATRIX 1: "+bmp);
        return bmp;
    }
}