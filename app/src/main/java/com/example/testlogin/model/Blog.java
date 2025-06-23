package com.example.testlogin.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "blogs")
public class Blog {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String titulo;
    public String historia;
    public String imagenUri;
    public String usuario;

    public Blog() {
    }

    public Blog(long id, String titulo, String historia, String imagenUri, String usuario) {
        this.id = id;
        this.titulo = titulo;
        this.historia = historia;
        this.imagenUri = imagenUri;
        this.usuario = usuario;
    }

    @Ignore
    public Blog(String titulo, String historia, String imagenUri, String usuario) {
        this(0, titulo, historia, imagenUri, usuario);
    }
}
