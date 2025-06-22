package com.example.testlogin.model;

public class Blog {
    public long id;
    public String titulo;
    public String historia;
    public String imagenUri;
    public String usuario;  // nuevo campo para saber quién lo creó

    // Constructor con ID (usado en lecturas desde la DB)
    public Blog(long id, String titulo, String historia, String imagenUri, String usuario) {
        this.id = id;
        this.titulo = titulo;
        this.historia = historia;
        this.imagenUri = imagenUri;
        this.usuario = usuario;
    }

    // Constructor sin ID (usado antes de insertar en la DB)
    public Blog(String titulo, String historia, String imagenUri, String usuario) {
        this(-1, titulo, historia, imagenUri, usuario);
    }
}
