package com.project.juan_.jpc_locator.Entidades;


import java.security.PrivateKey;

public class Usuario {
    private String nombre;
    private String email;
    private String token;
    private int numero;
    private static String usuario;
    private static PrivateKey clavePrivada;
    private static byte[] claveCompartida;

    public Usuario() {
    }

    public static String getUsuario() {
        return usuario;
    }

    public void setUsuario(final String usuario) {

        if(usuario.equals("nada")){
            Usuario.usuario = null;
        }

        else{
            Usuario.usuario = usuario;
        }
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static PrivateKey getClavePrivada() {
        return clavePrivada;
    }

    public static void setClavePrivada(PrivateKey clavePública) {
        Usuario.clavePrivada = clavePública;
    }

    public static byte[] getClaveCompartida() {
        return claveCompartida;
    }

    public static void setClaveCompartida(byte[] claveCompartida) {
        Usuario.claveCompartida = claveCompartida;
    }
}
