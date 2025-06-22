package com.example.testlogin.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testlogin.R;
import com.example.testlogin.data.BlogRepository;
import com.example.testlogin.model.Blog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class Ingreso extends AppCompatActivity {

    // Variables principales para el manejo de datos y estado
    private BlogRepository repo;                  // Repositorio que maneja acceso a la base de datos
    private BlogAdapter adapter;                  // Adaptador para el RecyclerView
    private String usuarioActual;                 // Usuario que inició sesión
    private Blog blogEditando = null;             // Variable para saber si se está editando un post

    // Referencias a elementos de la interfaz
    private RecyclerView rv;
    private ScrollView formulario;
    private EditText etTitulo, etDescripcion;
    private ImageView imgPreview;
    private Uri imagenUri = null;                 // URI de la imagen seleccionada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplica el tema según las preferencias antes de cargar la vista
        ThemeUtils.applyThemeFromPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);

        // Recupera el usuario actualmente logueado
        SharedPreferences prefs = getSharedPreferences("UsuariosPrefs", MODE_PRIVATE);
        usuarioActual = prefs.getString("usuario_logueado", null);

        // Si no hay usuario logueado, regresa a la pantalla de login
        if (usuarioActual == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Inicializa repositorio y vistas de la interfaz
        repo = new BlogRepository(this);
        rv = findViewById(R.id.rvBlogs);
        formulario = findViewById(R.id.formularioLayout);
        etTitulo = findViewById(R.id.etTitle);
        etDescripcion = findViewById(R.id.etStory);
        imgPreview = findViewById(R.id.imgPreview);

        // Configura la lista con diseño vertical
        rv.setLayoutManager(new LinearLayoutManager(this));
        cargarLista();

        // Acciones de botones
        findViewById(R.id.btnNuevoPost).setOnClickListener(v -> mostrarFormulario(null));
        findViewById(R.id.btnCancelar).setOnClickListener(v -> ocultarFormulario());
        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> cerrarSesion());
        findViewById(R.id.btnModoOscuro).setOnClickListener(v -> {
            boolean dark = !ThemeUtils.getDarkModePreference(this);
            ThemeUtils.toggleTheme(this, dark);
            recreate(); // Recarga la actividad con el nuevo tema
        });
        findViewById(R.id.btnSave).setOnClickListener(v -> guardarPost());
        findViewById(R.id.btnSelectImage).setOnClickListener(v -> seleccionarImagen());
    }

    // Cierra la sesión y vuelve al login
    private void cerrarSesion() {
        SharedPreferences prefs = getSharedPreferences("UsuariosPrefs", MODE_PRIVATE);
        prefs.edit().remove("usuario_logueado").apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // Abre la galería para seleccionar una imagen
    private void seleccionarImagen() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, 1000);
    }

    // Recibe el resultado de la selección de imagen
    @Override
    protected void onActivityResult(int code, int result, Intent data) {
        super.onActivityResult(code, result, data);
        if (code == 1000 && result == RESULT_OK && data != null) {
            imagenUri = data.getData();                 // Guarda la URI de la imagen seleccionada
            imgPreview.setImageURI(imagenUri);          // Muestra la imagen en la vista previa
        }
    }

    // Guarda o actualiza un post en la base de datos
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
            // Crear nuevo post
            Blog b = new Blog(titulo, descripcion, savedImagePath, usuarioActual);
            repo.insert(b);
            Toast.makeText(this, "Post creado", Toast.LENGTH_SHORT).show();
        } else {
            // Editar post existente
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

    // Guarda la imagen seleccionada en almacenamiento interno
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

    // Carga todos los posts desde el repositorio y los muestra
    private void cargarLista() {
        List<Blog> blogs = repo.getAll();
        adapter = new BlogAdapter(blogs, usuarioActual, new BlogAdapter.OnItemClickListener() {
            @Override
            public void onEditar(Blog b) {
                mostrarFormulario(b);   // Editar post
            }

            @Override
            public void onEliminar(Blog b) {
                repo.delete(b.id);      // Eliminar post
                cargarLista();          // Recargar la lista
            }
        });
        rv.setAdapter(adapter);
    }

    // Muestra el formulario, ya sea vacío o con datos de un post
    private void mostrarFormulario(Blog b) {
        formulario.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);
        if (b != null) {
            // Modo edición
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
            // Modo nuevo post
            blogEditando = null;
            etTitulo.setText("");
            etDescripcion.setText("");
            imgPreview.setImageResource(R.drawable.ic_launcher_background);
            imagenUri = null;
        }
    }

    // Oculta el formulario y muestra la lista nuevamente
    private void ocultarFormulario() {
        formulario.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
        blogEditando = null;
    }

    // Clase interna MainActivity (pantalla de login)
    public static class MainActivity extends AppCompatActivity {

        private SharedPreferences prefs;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            ThemeUtils.applyThemeFromPreferences(this); // Aplica tema oscuro o claro
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            prefs = getSharedPreferences("UsuariosPrefs", MODE_PRIVATE);

            // Referencias a elementos del formulario de login
            EditText inputUsuario = findViewById(R.id.editTextUsuario);
            EditText inputPassword = findViewById(R.id.editTextPassword);
            Button botonIniciar = findViewById(R.id.button);
            Button botonRegistrarse = findViewById(R.id.button2);
            Button botonSalir = findViewById(R.id.button3);
            SwitchCompat sw = findViewById(R.id.switch_dark);

            // Configuración del interruptor de modo oscuro
            SharedPreferences themePrefs = getSharedPreferences("theme_preferences", MODE_PRIVATE);
            sw.setChecked(themePrefs.getBoolean("is_dark_mode_enabled", false));
            sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ThemeUtils.toggleTheme(MainActivity.this, isChecked);
                recreate();
            });

            // Navega a la pantalla de registro
            botonRegistrarse.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, RegistrarseActivity.class));
            });

            // Valida login
            botonIniciar.setOnClickListener(v -> {
                String userInput = inputUsuario.getText().toString().trim();
                String passInput = inputPassword.getText().toString().trim();

                if (userInput.isEmpty() || passInput.isEmpty()) {
                    Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                String savedPassword = prefs.getString("user_" + userInput, null);

                if (savedPassword != null && savedPassword.equals(passInput)) {
                    prefs.edit().putString("usuario_logueado", userInput).apply();
                    Toast.makeText(this, "Ingreso exitoso", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, Ingreso.class));
                    finish();
                } else {
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            });

            // Cierra la app
            botonSalir.setOnClickListener(v -> finishAffinity());
        }

        // Al volver a esta pantalla, actualiza el estado del switch
        @Override
        protected void onResume() {
            super.onResume();
            SwitchCompat sw = findViewById(R.id.switch_dark);
            SharedPreferences themePrefs = getSharedPreferences("theme_preferences", MODE_PRIVATE);
            sw.setChecked(themePrefs.getBoolean("is_dark_mode_enabled", false));
        }
    }
}
