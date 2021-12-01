package com.andoresudev.enfermerajoynfc.models;

public class consultorio {
    String id_cons;
    String idUsu_cons;
    String nombre_cons;
    String especialidad_cons;
    String direccion_cons;
    String telefono_cons;
    String correo_cons;

    public String getId_cons() {
        return id_cons;
    }

    public void setId_cons(String id_cons) {
        this.id_cons = id_cons;
    }

    public String getIdUsu_cons() {
        return idUsu_cons;
    }

    public void setIdUsu_cons(String idUsu_cons) {
        this.idUsu_cons = idUsu_cons;
    }

    public String getNombre_cons() {
        return nombre_cons;
    }

    public void setNombre_cons(String nombre_cons) {
        this.nombre_cons = nombre_cons;
    }

    public String getEspecialidad_cons() {
        return especialidad_cons;
    }

    public void setEspecialidad_cons(String especialidad_cons) {
        this.especialidad_cons = especialidad_cons;
    }

    public String getDireccion_cons() {
        return direccion_cons;
    }

    public void setDireccion_cons(String direccion_cons) {
        this.direccion_cons = direccion_cons;
    }

    public String getTelefono_cons() {
        return telefono_cons;
    }

    public void setTelefono_cons(String telefono_cons) {
        this.telefono_cons = telefono_cons;
    }

    public String getCorreo_cons() {
        return correo_cons;
    }

    public void setCorreo_cons(String correo_cons) {
        this.correo_cons = correo_cons;
    }

    @Override
    public String toString() {
        return nombre_cons;
    }
}
