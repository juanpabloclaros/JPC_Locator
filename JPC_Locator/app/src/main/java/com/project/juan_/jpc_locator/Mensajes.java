package com.project.juan_.jpc_locator;

public class Mensajes {
    private String from, mensaje, hora, fecha;

    public Mensajes(){}

    public Mensajes(String from, String mensaje, String hora, String fecha) {
        this.from = from;
        this.mensaje = mensaje;
        this.hora = hora;
        this.fecha = fecha;
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

    public void setfecha(String fecha) {
        this.fecha = fecha;
    }
}
