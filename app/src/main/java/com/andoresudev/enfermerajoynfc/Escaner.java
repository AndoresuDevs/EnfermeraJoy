package com.andoresudev.enfermerajoynfc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Escaner extends AppCompatActivity {

    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String token = "";
    private String tokenanterior = "";


    //NFC
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter[] writingTagFilters;
    Tag myTag;
    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escaner);
        cameraView =findViewById(R.id.camera_view);






        initQR();

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

    public void initQR() {

        // creo el detector qr
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        // creo la camara
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        // listener de ciclo de vida de la camara
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                // verifico si el usuario dio los permisos para la camara
                if (ActivityCompat.checkSelfPermission(Escaner.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // verificamos la version de ANnroid que sea al menos la M para mostrar
                        // el dialog de la solicitud de la camara
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)) ;
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                    return;
                } else {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        // preparo el detector de QR
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }


            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {

                    // obtenemos el token
                    token = barcodes.valueAt(0).displayValue.toString();

                    // verificamos que el token anterior no se igual al actual
                    // esto es util para evitar multiples llamadas empleando el mismo token
                    if (!token.equals(tokenanterior)) {

                        // guardamos el ultimo token proceado
                        tokenanterior = token;
                        Log.i("token", token);//lo que contiene el codigo qr

                        ////////////////
                        String[]contenido=token.split("\\|");
                        switch (contenido[0]) {
                            case "trat": {
                                Intent i = new Intent(Escaner.this, NuevaReceta.class);
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
                                Intent i = new Intent(Escaner.this, NuevoDoctor.class);
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
                                Intent i = new Intent(Escaner.this, NuevoConsultorio.class);
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
                                Intent i = new Intent(Escaner.this, NuevaReceta.class);
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
                        ///////////////
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    synchronized (this) {
                                        wait(5000);
                                        // limpiamos el token
                                        tokenanterior = "";
                                    }
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    Log.e("Error", "Waiting didnt work!!");
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }
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