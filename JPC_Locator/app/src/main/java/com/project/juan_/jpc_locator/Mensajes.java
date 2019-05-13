package com.project.juan_.jpc_locator;

public class Mensajes {
    private String from, mensaje, hora;

    public Mensajes(){}

    public Mensajes(String from, String mensaje, String hora) {
        this.from = from;
        this.mensaje = mensaje;
        this.hora = hora;
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

    public void setTipo(String hora) {
        this.hora = hora;
    }
}
