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

import com.andoresudev.enfermerajoynfc.models.consultorio;
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


public class VerConsultorios extends AppCompatActivity {
    String idConsSelected;
    EditText nombre, especialidad, direccion, telefono, correo;
    String nombreS, especialidadS, direccionS, telefonoS, correoS;
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
        setContentView(R.layout.activity_ver_consultorios);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        idConsSelected=getIntent().getStringExtra("IdConSelected");

        nombre=findViewById(R.id.txtNombreVC);
        especialidad=findViewById(R.id.txtEspecialidadVC);
        direccion=findViewById(R.id.txtDireccionVC);
        telefono=findViewById(R.id.txtTelefonoVC);
        correo=findViewById(R.id.txtCorreoVC);

        cancel=findViewById(R.id.btnCancelar);
        save=findViewById(R.id.btnActualizarReceta);
        edit=findViewById(R.id.btnEditarReceta);
        delete=findViewById(R.id.btnEliminarReceta);
        layEdit=findViewById(R.id.crudEditarCons);
        layMain=findViewById(R.id.crudVerCons);
        btnCompartir=findViewById(R.id.compartirConsultorio);
        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builderCompartir();
            }
        });
        editableOFF();
        infoCons(idConsSelected);

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
            pendingIntent= PendingIntent.getActivity(this,0,new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);
            IntentFilter tagDetected= new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
            writingTagFilters = new IntentFilter[]{tagDetected};
        }
    }

    private void infoCons(String id) {
        databaseReference.child("Consultorio").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    nombreS = snapshot.child("nombre_cons").getValue().toString();
                    especialidadS = snapshot.child("especialidad_cons").getValue().toString();
                    direccionS = snapshot.child("direccion_cons").getValue().toString();
                    telefonoS = snapshot.child("telefono_cons").getValue().toString();
                    correoS = snapshot.child("correo_cons").getValue().toString();

                    nombre.setText(nombreS);
                    especialidad.setText(especialidadS);
                    direccion.setText(direccionS);
                    telefono.setText(telefonoS);
                    correo.setText(correoS);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void editarConsultorio(View view){
        editableON();
        layMain.setVisibility(View.INVISIBLE);
        layEdit.setVisibility(View.VISIBLE);
    }

    public void confirmarEliminar(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar");
        builder.setMessage("¿Desea eliminar el consultorio? Esta acción no se pude deshacer");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarConsultorio();
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

    private void eliminarConsultorio(){
        try {
            databaseReference.child("Consultorio").child(idConsSelected).removeValue();
            finish();
            Toast.makeText(this, "Consultorio eliminada correctamente", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
        }

    }

    public void validation(TextView text){
        text.setError("Obligatorio");
    }

    private void editableOFF(){
        nombre.setEnabled(false);
        especialidad.setEnabled(false);
        direccion.setEnabled(false);
        telefono.setEnabled(false);
        correo.setEnabled(false);
    }

    private void editableON(){
        nombre.setEnabled(true);
        especialidad.setEnabled(true);
        direccion.setEnabled(true);
        telefono.setEnabled(true);
        correo.setEnabled(true);
    }
    public void cancelarConsultorio(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancelar");
        builder.setMessage("No se guardaran los cambios ¿desea continuar?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editableOFF();
                nombre.setText(nombreS);
                especialidad.setText(especialidadS);
                direccion.setText(direccionS);
                telefono.setText(telefonoS);
                correo.setText(correoS);
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

    public void confirmarSaveConsultorio(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Guardar Cambios");
        builder.setMessage("¿Desea guardar los cambios? Esta acción no se pude deshacer");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveCambiosConsultorio();
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

    private void saveCambiosConsultorio(){
        ArrayList<EditText> txts = new ArrayList<EditText>();
        String nom=nombre.getText().toString();
        String esp=especialidad.getText().toString();
        String dir=direccion.getText().toString();
        String tel=telefono.getText().toString();
        String cor=correo.getText().toString();

        if (nom.isEmpty()){txts.add(nombre);}
        if (dir.isEmpty()){txts.add(direccion);}

        if(txts.isEmpty()) {
            //FILTROS SUPERADOS, COMIENZA LA SUBIDA A BD
            consultorio con = new consultorio();
            con.setId_cons(idConsSelected);
            con.setIdUsu_cons(mAuth.getCurrentUser().getUid());
            con.setNombre_cons(nom);
            con.setEspecialidad_cons(esp);
            con.setDireccion_cons(dir);
            con.setTelefono_cons(tel);
            con.setCorreo_cons(cor);


            try {
                databaseReference.child("Consultorio").child(idConsSelected).setValue(con);
                Toast.makeText(this, "Actualizado con Exito", Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Error al actualizar la los datos del consultorio", Toast.LENGTH_SHORT).show();
            }

        }else{
            for (TextView tx: txts){
                validation(tx);
            }
        }
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
                    Toast.makeText(VerConsultorios.this,R.string.nfc_no_supported,Toast.LENGTH_SHORT).show();
                }else{
                    if(nfcAdapter.isEnabled()){
                        //AQUI METE EL TRY CATCH
                        try {
                            if(myTag==null){
                                Toast.makeText(VerConsultorios.this,R.string.error_detected,Toast.LENGTH_SHORT).show();
                            }else{
                                String mensajeNFC=empaquetarMensaje();
                                write(mensajeNFC, myTag);
                                Toast.makeText(VerConsultorios.this,R.string.write_succes,Toast.LENGTH_SHORT).show();
                            }
                        }catch (IOException | FormatException e){
                            Toast.makeText(VerConsultorios.this,R.string.write_error,Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(VerConsultorios.this,R.string.nfc_disabled,Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VerConsultorios.this).setCancelable(true);
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
        String text="cons|"; //identificador para saber k ventana abrir
        text=text+nombre.getText().toString()+"|";
        text=text+especialidad.getText().toString()+"|";
        text=text+direccion.getText().toString()+"|";
        text=text+telefono.getText().toString()+"|";
        if(correo.getText().toString().equals("")){
            text=text+" ";
        }else{
            text=text+correo.getText().toString()+"|";
        }

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