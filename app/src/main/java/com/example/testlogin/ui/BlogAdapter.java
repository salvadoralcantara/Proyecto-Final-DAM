package com.example.testlogin.ui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testlogin.R;
import com.example.testlogin.model.Blog;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {

    public interface OnBlogActionListener {
        void onEdit(Blog blog);
        void onDelete(Blog blog);
    }

    private final List<Blog> blogs;
    private final OnBlogActionListener listener;
    private final String usuarioActual;

    public BlogAdapter(List<Blog> blogs, String usuarioActual, OnBlogActionListener listener) {
        this.blogs = blogs;
        this.usuarioActual = usuarioActual;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blog_item, parent, false);
        return new BlogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder h, int position) {
        Blog b = blogs.get(position);

        h.title.setText(b.titulo);
        h.story.setText(b.historia);

        if (b.imagenUri != null) {
            h.image.setImageURI(Uri.parse(b.imagenUri));
        } else {
            h.image.setImageResource(R.drawable.ic_launcher_background);
        }

        // Mostrar botones solo si el post pertenece al usuario logueado
        if (b.usuario != null && b.usuario.equals(usuarioActual)) {
            h.buttonContainer.setVisibility(View.VISIBLE);
            h.btnEditar.setOnClickListener(v -> listener.onEdit(b));
            h.btnEliminar.setOnClickListener(v -> listener.onDelete(b));
        } else {
            h.buttonContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return blogs.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView title, story;
        ImageView image;
        LinearLayout buttonContainer;
        Button btnEditar, btnEliminar;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvItemTitle);
            story = itemView.findViewById(R.id.tvItemStory);
            image = itemView.findViewById(R.id.imgItem);
            buttonContainer = itemView.findViewById(R.id.buttonContainer);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
