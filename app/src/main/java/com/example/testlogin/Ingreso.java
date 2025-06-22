package com.example.testlogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testlogin.data.BlogRepository;
import com.example.testlogin.model.Blog;
import com.example.testlogin.ui.BlogAdapter;

import java.util.List;

public class Ingreso extends AppCompatActivity {

    private BlogRepository repository;
    private BlogAdapter adapter;

    private EditText etTitle, etStory;
    private ImageView imgPreview;
    private ScrollView formularioLayout;
    private RecyclerView rvBlogs;

    private Blog blogAEditar = null;
    private String uriImagen = null;
    private String usuarioActual;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    uriImagen = uri.toString();
                    imgPreview.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyThemeFromPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);

        // Obtener usuario logueado
        SharedPreferences prefs = getSharedPreferences("UsuariosPrefs", MODE_PRIVATE);
        usuarioActual = prefs.getString("usuario_logueado", "");

        // Inicializar componentes
        repository = new BlogRepository(this);
        formularioLayout = findViewById(R.id.formularioLayout);
        rvBlogs = findViewById(R.id.rvBlogs);
        etTitle = findViewById(R.id.etTitle);
        etStory = findViewById(R.id.etStory);
        imgPreview = findViewById(R.id.imgPreview);

        rvBlogs.setLayoutManager(new LinearLayoutManager(this));
        cargarDatos();

        // Botón: Cerrar sesión
        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> {
            prefs.edit().remove("usuario_logueado").apply();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // Botón: Modo oscuro
        findViewById(R.id.btnModoOscuro).setOnClickListener(v -> {
            boolean dark = !ThemeUtils.isDarkModeEnabled(this);
            ThemeUtils.toggleTheme(this, dark);
            recreate();
        });

        // Botón: Nuevo post
        findViewById(R.id.btnNuevoPost).setOnClickListener(v -> {
            blogAEditar = null;
            uriImagen = null;
            etTitle.setText("");
            etStory.setText("");
            imgPreview.setImageResource(R.drawable.ic_launcher_background);
            formularioLayout.setVisibility(View.VISIBLE);
        });

        // Botón: Seleccionar imagen
        findViewById(R.id.btnSelectImage).setOnClickListener(v -> {
            filePickerLauncher.launch("image/*");
        });

        // Botón: Guardar
        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String titulo = etTitle.getText().toString().trim();
            String historia = etStory.getText().toString().trim();

            if (titulo.isEmpty() || historia.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (blogAEditar == null) {
                Blog nuevo = new Blog(titulo, historia, uriImagen, usuarioActual);
                repository.insert(nuevo);
            } else {
                blogAEditar.titulo = titulo;
                blogAEditar.historia = historia;
                blogAEditar.imagenUri = uriImagen;
                repository.update(blogAEditar);
            }

            formularioLayout.setVisibility(View.GONE);
            cargarDatos();
        });

        // Botón: Cancelar
        findViewById(R.id.btnCancelar).setOnClickListener(v -> {
            formularioLayout.setVisibility(View.GONE);
        });
    }

    private void cargarDatos() {
        List<Blog> blogs = repository.getAll();
        adapter = new BlogAdapter(blogs, usuarioActual, new BlogAdapter.OnItemClickListener() {
            @Override
            public void onEditar(Blog blog) {
                blogAEditar = blog;
                etTitle.setText(blog.titulo);
                etStory.setText(blog.historia);
                uriImagen = blog.imagenUri;

                if (uriImagen != null) {
                    imgPreview.setImageURI(Uri.parse(uriImagen));
                } else {
                    imgPreview.setImageResource(R.drawable.ic_launcher_background);
                }

                formularioLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEliminar(Blog blog) {
                repository.delete(blog.id);
                cargarDatos();
            }
        });
        rvBlogs.setAdapter(adapter);
    }
}
