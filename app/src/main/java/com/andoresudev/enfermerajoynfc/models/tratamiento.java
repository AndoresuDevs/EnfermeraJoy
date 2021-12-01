package com.andoresudev.enfermerajoynfc.models;

public class tratamiento {

    public tratamiento(){

    }

    private String id_trat;
    private String id_usuTrat;
    private String fecha_trat;
    private String nombre_trat;
    private String apellido_trat;
    private String edad_trat;
    private String sexo_trat;
    private String estatura_trat;
    private String peso_trat;
    private String medico_trat;
    private String diagnostico_trat;
    private String centro_trat;
    private String medicamentos_trat;
    private String detalles_trat;

    public String getDiagnostico_trat() {
        return diagnostico_trat;
    }

    public void setDiagnostico_trat(String diagnostico_trat) {
        this.diagnostico_trat = diagnostico_trat;
    }

    public String getDetalles_trat() {
        return detalles_trat;
    }

    public void setDetalles_trat(String detalles_trat) {
        this.detalles_trat = detalles_trat;
    }

    public String getId_trat() {
        return id_trat;
    }

    public void setId_trat(String id_trat) {
        this.id_trat = id_trat;
    }

    public String getId_usuTrat() {
        return id_usuTrat;
    }

    public void setId_usuTrat(String id_usuTrat) {
        this.id_usuTrat = id_usuTrat;
    }

    public String getFecha_trat() {
        return fecha_trat;
    }

    public void setFecha_trat(String fecha_trat) {
        this.fecha_trat = fecha_trat;
    }

    public String getNombre_trat() {
        return nombre_trat;
    }

    public void setNombre_trat(String nombre_trat) {
        this.nombre_trat = nombre_trat;
    }

    public String getApellido_trat() {
        return apellido_trat;
    }

    public void setApellido_trat(String apellido_trat) {
        this.apellido_trat = apellido_trat;
    }

    public String getEdad_trat() {
        return edad_trat;
    }

    public void setEdad_trat(String edad_trat) {
        this.edad_trat = edad_trat;
    }

    public String getSexo_trat() {
        return sexo_trat;
    }

    public void setSexo_trat(String sexo_trat) {
        this.sexo_trat = sexo_trat;
    }

    public String getEstatura_trat() {
        return estatura_trat;
    }

    public void setEstatura_trat(String estatura_trat) {
        this.estatura_trat = estatura_trat;
    }

    public String getPeso_trat() {
        return peso_trat;
    }

    public void setPeso_trat(String peso_trat) {
        this.peso_trat = peso_trat;
    }

    public String getMedico_trat() {
        return medico_trat;
    }

    public void setMedico_trat(String medico_trat) {
        this.medico_trat = medico_trat;
    }

    public String getCentro_trat() {
        return centro_trat;
    }

    public void setCentro_trat(String centro_trat) {
        this.centro_trat = centro_trat;
    }

    public String getMedicamentos_trat() {
        return medicamentos_trat;
    }

    public void setMedicamentos_trat(String medicamentos_trat) {
        this.medicamentos_trat = medicamentos_trat;
    }

    @Override
    public String toString() {
        String med=diagnostico_trat+" - "+fecha_trat;
        return med;
    }



}
