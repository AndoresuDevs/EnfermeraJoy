package com.andoresudev.enfermerajoynfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.andoresudev.enfermerajoynfc.R;
import com.andoresudev.enfermerajoynfc.models.tip;

import java.util.ArrayList;

public class tips_adapter extends BaseAdapter {

    Context cont;
    ArrayList<tip> t=new ArrayList<tip>();

    public tips_adapter(Context cont, ArrayList<tip> tips){
        this.cont=cont;
        this.t=tips;
    }

    @Override
    public int getCount() {
        return t.size();
    }

    @Override
    public Object getItem(int position) {

        //return position;
        tip ti=t.get(position);
        return ti;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View vista, ViewGroup parent) {
        tip ti=t.get(position);
        if(vista==null)
            vista = LayoutInflater.from(cont).inflate(R.layout.listview_tips,null);

        TextView titulo= vista.findViewById(R.id.txtTituloTips);
        TextView desc= vista.findViewById(R.id.txtMiniDescTip);

        titulo.setText(ti.getTitulo_tip());
        desc.setText(ti.getDescripcion_tip());



        return vista;
    }
}
