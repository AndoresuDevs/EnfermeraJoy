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

import com.andoresudev.enfermerajoynfc.models.doctor;
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

public class VerDoctores extends AppCompatActivity {
    String idDocSelected;
    EditText nombre, apellido, especialidad, planta, cedula, telefono, correo;
    String nombreS, apellidoS, especialidadS, plantaS, cedulaS, telefonoS, correoS;
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
        setContentView(R.layout.activity_ver_doctores);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        idDocSelected = getIntent().getStringExtra("IdDocSelected");

        nombre = findViewById(R.id.txtNombreVD);
        apellido = findViewById(R.id.txtApellidoVD);
        especialidad = findViewById(R.id.txtEspecialidadVD);
        planta = findViewById(R.id.txtPlantaVD);
        cedula = findViewById(R.id.txtCedulaVD);
        telefono = findViewById(R.id.txtTelefonoVD);
        correo = findViewById(R.id.txtCorreoVD);

        cancel = findViewById(R.id.btnCancelar);
        save = findViewById(R.id.btnActualizarReceta);
        edit = findViewById(R.id.btnEditarReceta);
        delete = findViewById(R.id.btnEliminarReceta);
        layEdit = findViewById(R.id.crudEditarDoc);
        layMain = findViewById(R.id.crudVerDoc);

        btnCompartir=findViewById(R.id.compartirDoctor);
        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builderCompartir();
            }
        });
        editableOFF();
        infoDoc(idDocSelected);
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

    private void infoDoc(String id) {
        databaseReference.child("Doctor").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    nombreS = snapshot.child("nombre_doc").getValue().toString();
                    apellidoS = snapshot.child("apellido_doc").getValue().toString();
                    especialidadS = snapshot.child("especialidad_doc").getValue().toString();
                    plantaS = snapshot.child("planta_doc").getValue().toString();
                    cedulaS = snapshot.child("cedula_doc").getValue().toString();
                    telefonoS = snapshot.child("telefono_doc").getValue().toString();
                    correoS = snapshot.child("correo_doc").getValue().toString();
                    nombre.setText(nombreS);
                    apellido.setText(apellidoS);
                    especialidad.setText(especialidadS);
                    planta.setText(plantaS);
                    cedula.setText(cedulaS);
                    telefono.setText(telefonoS);
                    correo.setText(correoS);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void editarDoctor(View view) {
        editableON();
        layMain.setVisibility(View.INVISIBLE);
        layEdit.setVisibility(View.VISIBLE);
    }

    public void confirmarEliminarDoctor(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar");
        builder.setMessage("¿Desea eliminar este doctor? Esta acción no se puede deshacer");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarDoctor();
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

    private void eliminarDoctor() {
        try {
            databaseReference.child("Doctor").child(idDocSelected).removeValue();
            finish();
            Toast.makeText(this, "Doctor eliminada correctamente", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    public void validation(TextView text) {
        text.setError("Obligatorio");
    }

    private void editableOFF() {
        nombre.setEnabled(false);
        apellido.setEnabled(false);
        especialidad.setEnabled(false);
        planta.setEnabled(false);
        cedula.setEnabled(false);
        telefono.setEnabled(false);
        correo.setEnabled(false);
    }

    private void editableON() {
        nombre.setEnabled(true);
        apellido.setEnabled(true);
        especialidad.setEnabled(true);
        planta.setEnabled(true);
        cedula.setEnabled(true);
        telefono.setEnabled(true);
        correo.setEnabled(true);
    }

    public void cancelarDoctor(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancelar");
        builder.setMessage("No se guardaran los cambios al doctor ¿Desea continuar?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editableOFF();
                nombre.setText(nombreS);
                apellido.setText(apellidoS);
                especialidad.setText(especialidadS);
                planta.setText(plantaS);
                cedula.setText(cedulaS);
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

    public void confirmarSaveDoctor(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Guardar Cambios");
        builder.setMessage("¿Desea guardar los cambios de este doctor?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveCambiosDoctor();
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

    private void saveCambiosDoctor(){
        ArrayList<EditText> txts = new ArrayList<EditText>();
        String nom=nombre.getText().toString();
        String ape=apellido.getText().toString();
        String esp=especialidad.getText().toString();
        String pla=planta.getText().toString();
        String ced=cedula.getText().toString();
        String tel=telefono.getText().toString();
        String cor=correo.getText().toString();

        if (nom.isEmpty()){txts.add(nombre);}
        if (ape.isEmpty()){txts.add(apellido);}

        if(txts.isEmpty()) {
            //FILTROS SUPERADOS, COMIENZA LA SUBIDA A BD
            doctor doc = new doctor();
            doc.setId_doc(idDocSelected);
            doc.setIdUsu_doc(mAuth.getCurrentUser().getUid());
            doc.setNombre_doc(""+nom);
            doc.setApellido_doc(""+ape);
            doc.setEspecialidad_doc(""+esp);
            doc.setPlanta_doc(""+pla);
            doc.setCedula_doc(""+ced);
            doc.setTelefono_doc(""+tel);
            doc.setCorreo_doc(""+cor);

            try {
                databaseReference.child("Doctor").child(idDocSelected).setValue(doc);
                Toast.makeText(this,"Actualizado con Exito", Toast.LENGTH_SHORT).show();
                finish();
            }catch (Exception e){
                Toast.makeText(this,"Error al actualizar los datos del doctor", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(VerDoctores.this,R.string.nfc_no_supported,Toast.LENGTH_SHORT).show();
                }else{
                    if(nfcAdapter.isEnabled()){
                        try {
                            if(myTag==null){
                                Toast.makeText(VerDoctores.this,R.string.error_detected,Toast.LENGTH_SHORT).show();
                            }else{
                                String mensajeNFC=empaquetarMensaje();
                                write(mensajeNFC, myTag);
                                Toast.makeText(VerDoctores.this,R.string.write_succes,Toast.LENGTH_SHORT).show();
                            }
                        }catch (IOException | FormatException e){
                            Toast.makeText(VerDoctores.this,R.string.write_error,Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(VerDoctores.this,R.string.nfc_disabled,Toast.LENGTH_SHORT).show();
                    }
                }




            }
        });

        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VerDoctores.this).setCancelable(true);
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
        String text="doc|"; //identificador para saber k ventana abrir
        text=text+nombre.getText().toString()+"|";
        text=text+apellido.getText().toString()+"|";
        text=text+especialidad.getText().toString()+"|";
        text=text+planta.getText().toString()+"|";
        text=text+cedula.getText().toString()+"|";
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