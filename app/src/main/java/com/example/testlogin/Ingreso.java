package com.example.testlogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testlogin.data.BlogRepository;
import com.example.testlogin.model.Blog;
import com.example.testlogin.ui.BlogAdapter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class Ingreso extends AppCompatActivity {

    private BlogRepository repo;
    private BlogAdapter adapter;
    private String usuarioActual;
    private Blog blogEditando = null;

    private RecyclerView rv;
    private ScrollView formulario;
    private EditText etTitulo, etDescripcion;
    private ImageView imgPreview;
    private Uri imagenUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyThemeFromPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);

        SharedPreferences prefs = getSharedPreferences("UsuariosPrefs", MODE_PRIVATE);
        usuarioActual = prefs.getString("usuario_logueado", null);

        if (usuarioActual == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        repo = new BlogRepository(this);
        rv = findViewById(R.id.rvBlogs);
        formulario = findViewById(R.id.formularioLayout);
        etTitulo = findViewById(R.id.etTitle);
        etDescripcion = findViewById(R.id.etStory);
        imgPreview = findViewById(R.id.imgPreview);

        rv.setLayoutManager(new LinearLayoutManager(this));
        cargarLista();

        findViewById(R.id.btnNuevoPost).setOnClickListener(v -> mostrarFormulario(null));
        findViewById(R.id.btnCancelar).setOnClickListener(v -> ocultarFormulario());
        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> cerrarSesion());
        findViewById(R.id.btnModoOscuro).setOnClickListener(v -> {
            boolean dark = !ThemeUtils.getDarkModePreference(this);
            ThemeUtils.toggleTheme(this, dark);
            recreate();
        });

        findViewById(R.id.btnSave).setOnClickListener(v -> guardarPost());
        findViewById(R.id.btnSelectImage).setOnClickListener(v -> seleccionarImagen());
    }

    private void cerrarSesion() {
        SharedPreferences prefs = getSharedPreferences("UsuariosPrefs", MODE_PRIVATE);
        prefs.edit().remove("usuario_logueado").apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void seleccionarImagen() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, 1000);
    }

    @Override
    protected void onActivityResult(int code, int result, Intent data) {
        super.onActivityResult(code, result, data);
        if (code == 1000 && result == RESULT_OK && data != null) {
            imagenUri = data.getData();
            imgPreview.setImageURI(imagenUri);
        }
    }

    private void guardarPost() {
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (titulo.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String savedImagePath = null;
        if (imagenUri != null) {
            savedImagePath = guardarImagenLocal(imagenUri);
        }

        if (blogEditando == null) {
            Blog b = new Blog(titulo, descripcion, savedImagePath, usuarioActual);
            repo.insert(b);
            Toast.makeText(this, "Post creado", Toast.LENGTH_SHORT).show();
        } else {
            blogEditando.titulo = titulo;
            blogEditando.historia = descripcion;
            blogEditando.imagenUri = savedImagePath;
            repo.update(blogEditando);
            Toast.makeText(this, "Post actualizado", Toast.LENGTH_SHORT).show();
            blogEditando = null;
        }

        ocultarFormulario();
        cargarLista();
    }

    private String guardarImagenLocal(Uri uri) {
        try {
            InputStream input = getContentResolver().openInputStream(uri);
            File imgFile = new File(getFilesDir(), "img_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream output = new FileOutputStream(imgFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > 0) {
                output.write(buffer, 0, len);
            }
            input.close();
            output.close();
            return imgFile.getAbsolutePath();
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar imagen", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void cargarLista() {
        List<Blog> blogs = repo.getAll();
        adapter = new BlogAdapter(blogs, usuarioActual, new BlogAdapter.OnItemClickListener() {
            @Override
            public void onEditar(Blog b) {
                mostrarFormulario(b);
            }

            @Override
            public void onEliminar(Blog b) {
                repo.delete(b.id);
                cargarLista();
            }
        });
        rv.setAdapter(adapter);
    }

    private void mostrarFormulario(Blog b) {
        formulario.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);
        if (b != null) {
            blogEditando = b;
            etTitulo.setText(b.titulo);
            etDescripcion.setText(b.historia);
            if (b.imagenUri != null) {
                File imgFile = new File(b.imagenUri);
                if (imgFile.exists()) {
                    imgPreview.setImageURI(Uri.fromFile(imgFile));
                } else {
                    imgPreview.setImageResource(R.drawable.ic_launcher_background);
                }
                imagenUri = Uri.fromFile(imgFile);
            }
        } else {
            blogEditando = null;
            etTitulo.setText("");
            etDescripcion.setText("");
            imgPreview.setImageResource(R.drawable.ic_launcher_background);
            imagenUri = null;
        }
    }

    private void ocultarFormulario() {
        formulario.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
        blogEditando = null;
    }
}
