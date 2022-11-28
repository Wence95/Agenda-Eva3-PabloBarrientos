package com.example.agenda.models;

import java.text.SimpleDateFormat;

public class Evento {
    private String uid;
    private String nombre;
    private long fecha;
    private String asunto;

    public Evento() {
    }

    public String getUid() {
        return uid;
    }

    public String getNombre() {
        return nombre;
    }

    public long getFecha() {
        return fecha;
    }

    public String getFechaString() {
        String fechaString = new SimpleDateFormat("dd/MM/yyyy").format(fecha);
        return fechaString;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    @Override
    public String toString() {
        return nombre + " " + getFechaString();
    }
}
