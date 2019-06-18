package com.project.juan_.jpc_locator;

public class Mensajes {
    private String from, mensaje, hora, fecha, nombre, grupoID;

    public Mensajes(){}

    public Mensajes(String from, String mensaje, String hora, String fecha, String nombre, String grupoID) {
        this.from = from;
        this.mensaje = mensaje;
        this.hora = hora;
        this.fecha = fecha;
        this.nombre = nombre;
        this.grupoID = grupoID;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGrupoID() {
        return grupoID;
    }

    public void setGrupoID(String grupoID) {
        this.grupoID = grupoID;
    }
}
