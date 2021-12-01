package com.andoresudev.enfermerajoynfc.models;

public class usuario {

    //VARIABLES
    private String id_user;
    private String contra_user;
    private String correo_user;
    private String nombre_user;
    private String apellidos_user;
    private String edad_user;
    private String sexo_user;
    private String estatura_user;
    private String peso_user;
    private String domicilio_user;
    private String telefono_user;
    private String alergias_user;
    private String sangre_user;
    private String umf_user;
    private String detalles_user;

    //SETTERS & GETTERS
    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getContra_user() {
        return contra_user;
    }

    public void setContra_user(String contra_user) {
        this.contra_user = contra_user;
    }

    public String getNombre_user() {
        return nombre_user;
    }

    public void setNombre_user(String nombre_user) {
        this.nombre_user = nombre_user;
    }

    public String getCorreo_user() {
        return correo_user;
    }

    public void setCorreo_user(String correo_user) {
        this.correo_user = correo_user;
    }

    public String getApellidos_user() {
        return apellidos_user;
    }

    public void setApellidos_user(String apellidos_user) {
        this.apellidos_user = apellidos_user;
    }

    public String getEdad_user() {
        return edad_user;
    }

    public void setEdad_user(String edad_user) {
        this.edad_user = edad_user;
    }

    public String getSexo_user() {
        return sexo_user;
    }

    public void setSexo_user(String sexo_user) {
        this.sexo_user = sexo_user;
    }

    public String getEstatura_user() {
        return estatura_user;
    }

    public void setEstatura_user(String estatura_user) {
        this.estatura_user = estatura_user;
    }

    public String getPeso_user() {
        return peso_user;
    }

    public void setPeso_user(String peso_user) {
        this.peso_user = peso_user;
    }

    public String getDomicilio_user() {
        return domicilio_user;
    }

    public void setDomicilio_user(String domicilio_user) {
        this.domicilio_user = domicilio_user;
    }

    public String getTelefono_user() {
        return telefono_user;
    }

    public void setTelefono_user(String telefono_user) {
        this.telefono_user = telefono_user;
    }

    public String getAlergias_user() {
        return alergias_user;
    }

    public void setAlergias_user(String alergias_user) {
        this.alergias_user = alergias_user;
    }

    public String getSangre_user() {
        return sangre_user;
    }

    public void setSangre_user(String sangre_user) {
        this.sangre_user = sangre_user;
    }

    public String getUmf_user() {
        return umf_user;
    }

    public void setUmf_user(String umf_user) {
        this.umf_user = umf_user;
    }

    public String getDetalles_user() {
        return detalles_user;
    }

    public void setDetalles_user(String detalles_user) {
        this.detalles_user = detalles_user;
    }

    //TO STRING


    @Override
    public String toString() {
        return nombre_user;
    }

}
