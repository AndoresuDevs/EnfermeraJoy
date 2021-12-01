package com.andoresudev.enfermerajoynfc.models;

public class tip {
    String id_tip;
    String titulo_tip;
    String descripcion_tip;

    public String getId_tip() {
        return id_tip;
    }

    public void setId_tip(String id_tip) {
        this.id_tip = id_tip;
    }

    public String getTitulo_tip() {
        return titulo_tip;
    }

    public void setTitulo_tip(String titulo_tip) {
        this.titulo_tip = titulo_tip;
    }

    public String getDescripcion_tip() {
        return descripcion_tip;
    }

    public void setDescripcion_tip(String descripcion_tip) {
        this.descripcion_tip = descripcion_tip;
    }

    @Override
    public String toString() {
        return titulo_tip;
    }
}
