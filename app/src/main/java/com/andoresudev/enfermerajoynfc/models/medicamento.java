package com.andoresudev.enfermerajoynfc.models;

public class medicamento {
    public int id_med;
    public int id_trat;
    public String nombre_med;
    public String cantidad_med;
    public String horario_med;

    public int getId_med() {
        return id_med;
    }

    public void setId_med(int id_med) {
        this.id_med = id_med;
    }

    public int getId_trat() {
        return id_trat;
    }

    public void setId_trat(int id_trat) {
        this.id_trat = id_trat;
    }

    public String getNombre_med() {
        return nombre_med;
    }

    public void setNombre_med(String nombre_med) {
        this.nombre_med = nombre_med;
    }

    public String getCantidad_med() {
        return cantidad_med;
    }

    public void setCantidad_med(String cantidad_med) {
        this.cantidad_med = cantidad_med;
    }

    public String getHorario_med() {
        return horario_med;
    }

    public void setHorario_med(String horario_med) {
        this.horario_med = horario_med;
    }
}
