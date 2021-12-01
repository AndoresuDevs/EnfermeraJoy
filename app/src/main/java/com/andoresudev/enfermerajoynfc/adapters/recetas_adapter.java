package com.andoresudev.enfermerajoynfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.andoresudev.enfermerajoynfc.R;
import com.andoresudev.enfermerajoynfc.models.tratamiento;

import java.util.ArrayList;

public class recetas_adapter extends BaseAdapter {
    Context cont;
    ArrayList<tratamiento>t= new ArrayList<tratamiento>();

    public recetas_adapter(Context cont, ArrayList<tratamiento> tratList){
        this.cont=cont;
        this.t=tratList;
    }

    @Override
    public int getCount() {
        return t.size();
    }

    @Override
    public Object getItem(int position) {
        tratamiento trat = t.get(position);
        return trat;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View vista, ViewGroup parent) {
        tratamiento trat = t.get(position);
        if (vista==null)
            vista= LayoutInflater.from(cont).inflate(R.layout.listview_recetas,null);

        TextView fecha= vista.findViewById(R.id.txtFechaTrat);
        TextView diag= vista.findViewById(R.id.txtDiagTrat);
        TextView doc= vista.findViewById(R.id.txtDocTrat);
        TextView clin= vista.findViewById(R.id.txtClinTrat);


        fecha.setText(trat.getFecha_trat());
        diag.setText(trat.getDiagnostico_trat());
        doc.setText(trat.getMedico_trat());
        clin.setText(trat.getCentro_trat());


        return vista;
    }
}
