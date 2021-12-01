package com.andoresudev.enfermerajoynfc.models;

public class doctor {
    String id_doc;
    String idUsu_doc;
    String nombre_doc;
    String apellido_doc;
    String especialidad_doc;
    String planta_doc;
    String cedula_doc;
    String telefono_doc;
    String correo_doc;

    public String getId_doc() {
        return id_doc;
    }

    public void setId_doc(String id_doc) {
        this.id_doc = id_doc;
    }

    public String getIdUsu_doc() {
        return idUsu_doc;
    }

    public void setIdUsu_doc(String idUsu_doc) {
        this.idUsu_doc = idUsu_doc;
    }

    public String getNombre_doc() {
        return nombre_doc;
    }

    public void setNombre_doc(String nombre_doc) {
        this.nombre_doc = nombre_doc;
    }

    public String getApellido_doc() {
        return apellido_doc;
    }

    public void setApellido_doc(String apellido_doc) {
        this.apellido_doc = apellido_doc;
    }

    public String getEspecialidad_doc() {
        return especialidad_doc;
    }

    public void setEspecialidad_doc(String especialidad_doc) {
        this.especialidad_doc = especialidad_doc;
    }

    public String getPlanta_doc() {
        return planta_doc;
    }

    public void setPlanta_doc(String planta_doc) {
        this.planta_doc = planta_doc;
    }

    public String getCedula_doc() {
        return cedula_doc;
    }

    public void setCedula_doc(String cedula_doc) {
        this.cedula_doc = cedula_doc;
    }

    public String getTelefono_doc() {
        return telefono_doc;
    }

    public void setTelefono_doc(String telefono_doc) {
        this.telefono_doc = telefono_doc;
    }

    public String getCorreo_doc() {
        return correo_doc;
    }

    public void setCorreo_doc(String correo_doc) {
        this.correo_doc = correo_doc;
    }

    @Override
    public String toString() {
        String doc =nombre_doc+" "+ apellido_doc;
        return doc;
    }
}
