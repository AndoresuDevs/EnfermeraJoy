package com.andoresudev.enfermerajoynfc;

import androidx.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andoresudev.enfermerajoynfc.models.tratamiento;
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
import java.util.ArrayList;
import java.util.Hashtable;

public class VerRecetas extends AppCompatActivity {
    String idTratSelected;
    EditText fecha, nombre, apellido, edad, sexo, estatura, peso, medico, centro, diagnostico, detalles;
    String fechaS, nombreS, apellidoS, edadS, sexoS, estaturaS, pesoS, medicoS, centroS, diagnosticoS, detallesS;
    ImageButton cancel, save, edit, delete;
    FloatingActionButton btnCompartir;
    LinearLayout layMain, layEdit;

    DatabaseReference databaseReference;
    FirebaseAuth mAuth;




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
        setContentView(R.layout.activity_ver_recetas);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        idTratSelected=getIntent().getStringExtra("IdTratSelected");

        fecha = findViewById(R.id.txtFechaVR);
        nombre = findViewById(R.id.txtNombreVR);
        apellido = findViewById(R.id.txtApellidosVR);
        edad = findViewById(R.id.txtEdadVR);
        sexo = findViewById(R.id.txtSexoVR);
        estatura = findViewById(R.id.txtEstaturaVR);
        peso = findViewById(R.id.txtPesoVR);
        medico = findViewById(R.id.txtMedicoVR);
        centro = findViewById(R.id.txtUnidadVR);
        diagnostico= findViewById(R.id.txtDiagnosticoVR);
        detalles=findViewById(R.id.txtDetallesVR);

        cancel=findViewById(R.id.btnCancelar);
        save=findViewById(R.id.btnActualizarReceta);
        edit=findViewById(R.id.btnEditarReceta);
        delete=findViewById(R.id.btnEliminarReceta);
        layEdit=findViewById(R.id.crudEditarReceta);
        layMain=findViewById(R.id.crudVerReceta);

        btnCompartir=findViewById(R.id.compartirReceta);
        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builderCompartir();
            }
        });
        editableOFF();
        infoTrat(idTratSelected);



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

    private void infoTrat(String id) {
        databaseReference.child("Receta").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    fechaS = snapshot.child("fecha_trat").getValue().toString();
                    nombreS = snapshot.child("nombre_trat").getValue().toString();
                    apellidoS = snapshot.child("apellido_trat").getValue().toString();
                    edadS = snapshot.child("edad_trat").getValue().toString();
                    sexoS = snapshot.child("sexo_trat").getValue().toString();
                    estaturaS = snapshot.child("estatura_trat").getValue().toString();
                    pesoS = snapshot.child("peso_trat").getValue().toString();
                    medicoS = snapshot.child("medico_trat").getValue().toString();
                    centroS = snapshot.child("centro_trat").getValue().toString();
                    diagnosticoS = snapshot.child("diagnostico_trat").getValue().toString();
                    detallesS = snapshot.child("detalles_trat").getValue().toString();

                    fecha.setText(fechaS);
                    nombre.setText(nombreS);
                    apellido.setText(apellidoS);
                    edad.setText(edadS);
                    sexo.setText(sexoS);
                    estatura.setText(estaturaS);
                    peso.setText(pesoS);
                    medico.setText(medicoS);
                    centro.setText(centroS);
                    diagnostico.setText(diagnosticoS);
                    detalles.setText(detallesS);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void editar(View view){
        editableON();
        layMain.setVisibility(View.INVISIBLE);
        layEdit.setVisibility(View.VISIBLE);
    }

    private void saveCambiosReceta(){
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

        if(txts.isEmpty()) {
            tratamiento trat = new tratamiento();
            trat.setId_trat(idTratSelected);
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
                databaseReference.child("Receta").child(idTratSelected).setValue(trat);
                Toast.makeText(this,R.string.receta_actualizada, Toast.LENGTH_SHORT).show();
                finish();
            }catch (Exception e){
                Toast.makeText(this,R.string.error_actualizar, Toast.LENGTH_SHORT).show();
            }
            editableOFF();
        }else{
            for(TextView tx: txts){
                validation(tx);
            }
        }
    }

    public void confirmarActualizarReceta(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Guardar Cambios");
        builder.setMessage("¿Desea guardar los cambios?");
        builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveCambiosReceta();
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

    private void eliminarReceta(){
        try {
            databaseReference.child("Receta").child(idTratSelected).removeValue();
            finish();
            Toast.makeText(this, R.string.receta_eliminada, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, R.string.error_eliminar, Toast.LENGTH_SHORT).show();
        }



    }

    public void confirmarEliminarReceta(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Receta");
        builder.setMessage("¿Desea eliminar esta receta? Esta acción no se puede deshacer");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarReceta();
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

    public void validation(TextView text){
        text.setError("Obligatorio");
    }

    private void editableOFF(){
        fecha.setEnabled(false);
        nombre.setEnabled(false);
        apellido.setEnabled(false);
        edad.setEnabled(false);
        sexo.setEnabled(false);
        estatura.setEnabled(false);
        peso.setEnabled(false);
        medico.setEnabled(false);
        centro.setEnabled(false);
        diagnostico.setEnabled(false);
        detalles.setEnabled(false);

    }

    private void editableON(){
        fecha.setEnabled(true);
        nombre.setEnabled(true);
        apellido.setEnabled(true);
        edad.setEnabled(true);
        sexo.setEnabled(true);
        estatura.setEnabled(true);
        peso.setEnabled(true);
        medico.setEnabled(true);
        centro.setEnabled(true);
        diagnostico.setEnabled(true);
        detalles.setEnabled(true);
    }
    public void cancelar(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Actualizar Perfil");
        builder.setMessage("¿Desea guardar los cambios? Esta acción no se puede deshacer");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editableOFF();
                fecha.setText(fechaS);
                nombre.setText(nombreS);
                apellido.setText(apellidoS);
                edad.setText(edadS);
                sexo.setText(sexoS);
                estatura.setText(estaturaS);
                peso.setText(pesoS);
                medico.setText(medicoS);
                centro.setText(centroS);
                diagnostico.setText(diagnosticoS);
                detalles.setText(detallesS);
                layMain.setVisibility(View.VISIBLE);
                layEdit.setVisibility(View.INVISIBLE);
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
                    Toast.makeText(VerRecetas.this,R.string.nfc_no_supported,Toast.LENGTH_SHORT).show();
                }else{
                    if(nfcAdapter.isEnabled()){
                        try {
                            if(myTag==null){
                                Toast.makeText(VerRecetas.this,R.string.error_detected,Toast.LENGTH_SHORT).show();
                            }else{
                                String mensajeNFC=empaquetarMensaje();
                                write(mensajeNFC, myTag);
                                Toast.makeText(VerRecetas.this,R.string.write_succes,Toast.LENGTH_SHORT).show();
                            }
                        }catch (IOException | FormatException e){
                            Toast.makeText(VerRecetas.this,R.string.write_error,Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(VerRecetas.this,R.string.nfc_disabled,Toast.LENGTH_SHORT).show();
                    }
                }



            }

        });


        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VerRecetas.this).setCancelable(true);
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
        String text="trat|"; //identificador para saber k ventana abrir
        text=text+fecha.getText().toString()+"|";
        text=text+nombre.getText().toString()+"|";
        text=text+apellido.getText().toString()+"|";
        text=text+edad.getText().toString()+"|";
        text=text+sexo.getText().toString()+"|";
        text=text+estatura.getText().toString()+"|";
        text=text+peso.getText().toString()+"|";
        text=text+medico.getText().toString()+"|";
        text=text+centro.getText().toString()+"|";
        text=text+diagnostico.getText().toString()+"|";
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
        NdefRecord [] records ={createRecord(text)};
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
        byte[] langBytes=lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength= textBytes.length;
        byte[]payload = new byte[1+langLength+textLength];

        //set status byte (see NDEF specfor actual bits)
        payload[0]=(byte) langLength;

        //copy langbytes and textbytes into payload
        System.arraycopy(langBytes,0,payload,1, langLength);
        System.arraycopy(textBytes,0,payload,1+langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_TEXT, new byte[0],payload);
        return recordNFC;


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