package com.project.juan_.jpc_locator;

public class MapsPojo {

    // Vamos a crear las variables que van a contener la latitud y longitud(tiene que llamarse igual a como lo tenemos en Firebase sino no funciona)
    private double Latitud;
    private double Longitud;

    // Constructor
    public MapsPojo(){}

    public double getLatitud() {
        return Latitud;
    }

    public void setLatitud(double latitud) {
        Latitud = latitud;
    }

    public double getLongitud() {
        return Longitud;
    }

    public void setLongitud(double longitud) {
        Longitud = longitud;
    }
}
