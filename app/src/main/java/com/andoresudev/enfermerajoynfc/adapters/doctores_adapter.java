package com.andoresudev.enfermerajoynfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.andoresudev.enfermerajoynfc.R;
import com.andoresudev.enfermerajoynfc.models.doctor;

import java.util.ArrayList;

public class doctores_adapter extends BaseAdapter {

    Context cont;
    ArrayList<doctor> d = new ArrayList<doctor>();

    public doctores_adapter(Context cont, ArrayList<doctor> doctorList){
        this.cont=cont;
        this.d=doctorList;
    }

    @Override
    public int getCount() {
        return d.size();
    }

    @Override
    public Object getItem(int position) {
        doctor doc = d.get(position);
        return doc;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View vista, ViewGroup parent) {
        doctor doc = d.get(position);
        if(vista==null)
            vista = LayoutInflater.from(cont).inflate(R.layout.listview_doctores,null);

        TextView esp = vista.findViewById(R.id.txtEspDoctor);
        TextView nom = vista.findViewById(R.id.txtNomDoctor);
        TextView tel = vista.findViewById(R.id.txtTelDoctor);

        esp.setText(doc.getEspecialidad_doc());
        String nombre = doc.getNombre_doc() +" "+doc.getApellido_doc();
        nom.setText(nombre);
        tel.setText(doc.getTelefono_doc());



        return vista;
    }
}
