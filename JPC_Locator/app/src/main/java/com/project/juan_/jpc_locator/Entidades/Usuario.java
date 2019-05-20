package com.project.juan_.jpc_locator.Entidades;


public class Usuario {
    private String nombre;
    private String email;
    private String token;
    private int numero;
    private boolean cerca;
    private static String usuario;

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

    public boolean isCerca() {
        return cerca;
    }

    public void setCerca(boolean cerca) {
        this.cerca = cerca;
    }
}
