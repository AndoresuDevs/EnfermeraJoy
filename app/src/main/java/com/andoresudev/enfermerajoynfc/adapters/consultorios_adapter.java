package com.andoresudev.enfermerajoynfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.andoresudev.enfermerajoynfc.R;
import com.andoresudev.enfermerajoynfc.models.consultorio;

import java.util.ArrayList;

public class consultorios_adapter extends BaseAdapter {

    Context cont;
    ArrayList<consultorio> c=new ArrayList<consultorio>();

    public consultorios_adapter(Context cont, ArrayList<consultorio> consList){
        this.cont=cont;
        this.c=consList;
    }

    @Override
    public int getCount() {
        return c.size();
    }

    @Override
    public Object getItem(int position) {

        consultorio cons = c.get(position);
        return cons;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View vista, ViewGroup parent) {
        consultorio cons = c.get(position);
        if(vista==null)
            vista = LayoutInflater.from(cont).inflate(R.layout.listview_consultorios, null);

        TextView esp= vista.findViewById(R.id.txtEspClinica);
        TextView nom= vista.findViewById(R.id.txtNomClinica);
        TextView dir= vista.findViewById(R.id.txtDirClinica);


        esp.setText(cons.getEspecialidad_cons());
        nom.setText(cons.getNombre_cons());
        dir.setText(cons.getDireccion_cons());


        return vista;
    }
}
