package com.example.testlogin.ui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testlogin.R;
import com.example.testlogin.model.Blog;

import java.io.File;
import java.util.List;

// Adaptador para el RecyclerView que muestra los posts del blog
public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {

    // Interfaz para manejar clics de edición y eliminación
    public interface OnItemClickListener {
        void onEditar(Blog blog);     // Llamado al presionar "Editar"
        void onEliminar(Blog blog);   // Llamado al presionar "Eliminar"
    }

    private final List<Blog> lista;                // Lista de publicaciones a mostrar
    private final String usuarioActual;            // Usuario logueado
    private final OnItemClickListener listener;    // Listener para eventos de botones

    // Constructor que recibe la lista, el usuario y el listener
    public BlogAdapter(List<Blog> lista, String usuarioActual, OnItemClickListener listener) {
        this.lista = lista;
        this.usuarioActual = usuarioActual;
        this.listener = listener;
    }

    // Crea y devuelve un nuevo ViewHolder inflando el layout blog_item
    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_item, parent, false);
        return new BlogViewHolder(v);
    }

    // Asocia los datos de un post con los elementos visuales del ViewHolder
    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder h, int position) {
        Blog b = lista.get(position);                     // Obtiene el post en esa posición
        h.tvTitle.setText(b.titulo);                      // Asigna título
        h.tvStory.setText(b.historia);                    // Asigna descripción

        // Manejo seguro de la imagen del post desde la URI
        if (b.imagenUri != null) {
            try {
                Uri uri = Uri.parse(b.imagenUri);         // Parsea la URI de texto a objeto Uri
                File file = new File(uri.getPath());       // Crea archivo desde la ruta
                if (file.exists()) {
                    h.img.setImageURI(uri);               // Muestra imagen si existe
                } else {
                    h.img.setImageResource(R.drawable.ic_launcher_background); // Imagen por defecto
                }
            } catch (Exception e) {
                h.img.setImageResource(R.drawable.ic_launcher_background); // Imagen por defecto en error
            }
        } else {
            h.img.setImageResource(R.drawable.ic_launcher_background);     // Si no hay imagen
        }

        // Mostrar botones solo si el post pertenece al usuario actual
        if (b.usuario != null && b.usuario.equals(usuarioActual)) {
            h.btnEditar.setVisibility(View.VISIBLE);
            h.btnEliminar.setVisibility(View.VISIBLE);
        } else {
            h.btnEditar.setVisibility(View.GONE);
            h.btnEliminar.setVisibility(View.GONE);
        }

        // Asignación de acciones a botones
        h.btnEditar.setOnClickListener(v -> listener.onEditar(b));
        h.btnEliminar.setOnClickListener(v -> listener.onEliminar(b));
    }

    // Devuelve el número total de posts en la lista
    @Override
    public int getItemCount() {
        return lista.size();
    }

    // Clase interna que representa un "item" visual del RecyclerView
    static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvStory;            // Título y contenido del post
        ImageView img;                        // Imagen asociada
        Button btnEditar, btnEliminar;        // Botones de acción

        // Constructor que asocia los elementos de la vista con variables
        BlogViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvStory = itemView.findViewById(R.id.tvItemStory);
            img = itemView.findViewById(R.id.imgItem);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
