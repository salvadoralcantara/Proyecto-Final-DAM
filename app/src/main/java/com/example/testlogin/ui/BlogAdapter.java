package com.example.testlogin.ui;

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

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {

    public interface OnItemClickListener {
        void onEditar(Blog blog);
        void onEliminar(Blog blog);
    }

    private final List<Blog> lista;
    private final String usuarioActual;
    private final OnItemClickListener listener;

    public BlogAdapter(List<Blog> lista, String usuarioActual, OnItemClickListener listener) {
        this.lista = lista;
        this.usuarioActual = usuarioActual;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_item, parent, false);
        return new BlogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder h, int position) {
        Blog b = lista.get(position);
        h.tvTitle.setText(b.titulo);
        h.tvStory.setText(b.historia);

        if (b.imagenUri != null) {
            h.img.setImageURI(Uri.parse(b.imagenUri));
        } else {
            h.img.setImageResource(R.drawable.ic_launcher_background);
        }

        if (b.usuario != null && b.usuario.equals(usuarioActual)) {
            h.btnEditar.setVisibility(View.VISIBLE);
            h.btnEliminar.setVisibility(View.VISIBLE);
        } else {
            h.btnEditar.setVisibility(View.GONE);
            h.btnEliminar.setVisibility(View.GONE);
        }

        h.btnEditar.setOnClickListener(v -> listener.onEditar(b));
        h.btnEliminar.setOnClickListener(v -> listener.onEliminar(b));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvStory;
        ImageView img;
        Button btnEditar, btnEliminar;

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
