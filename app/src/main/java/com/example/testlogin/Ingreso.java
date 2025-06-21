package com.example.testlogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testlogin.data.BlogRepository;
import com.example.testlogin.data.BlogRepository.Blog;

import java.util.List;

public class Ingreso extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 101;
    private EditText etTitle, etStory;
    private ImageView imgPreview;
    private Button btnSelectImage, btnSave, btnCancelar;
    private Button btnModoOscuro, btnCerrarSesion, btnNuevoPost;

    private ScrollView formularioLayout;
    private RecyclerView rvBlogs;
    private Uri selectedImageUri = null;
    private BlogRepository repository;
    private List<Blog> blogList;
    private RecyclerView.Adapter blogAdapter;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private String usuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyThemeFromPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);

        SharedPreferences prefs = getSharedPreferences("UsuariosPrefs", MODE_PRIVATE);
        usuarioActual = prefs.getString("usuario_logueado", "");  // Recuperamos el usuario

        repository = new BlogRepository(this);
        setupUI();
        setupRecyclerView();
        setupImagePicker();
        setupListeners();
    }

    private void setupUI() {
        etTitle = findViewById(R.id.etTitle);
        etStory = findViewById(R.id.etStory);
        imgPreview = findViewById(R.id.imgPreview);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSave = findViewById(R.id.btnSave);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnModoOscuro = findViewById(R.id.btnModoOscuro);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnNuevoPost = findViewById(R.id.btnNuevoPost);
        formularioLayout = findViewById(R.id.formularioLayout);
        rvBlogs = findViewById(R.id.rvBlogs);
    }

    private void setupRecyclerView() {
        rvBlogs.setLayoutManager(new LinearLayoutManager(this));
        blogList = repository.getAll(usuarioActual);
        blogAdapter = new RecyclerView.Adapter<BlogViewHolder>() {
            @Override
            public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_item, parent, false);
                return new BlogViewHolder(view);
            }

            @Override
            public void onBindViewHolder(BlogViewHolder holder, int position) {
                Blog blog = blogList.get(position);
                holder.tvTitle.setText(blog.titulo);
                holder.tvStory.setText(blog.historia);
                if (blog.imagenUri != null) {
                    holder.imgItem.setImageURI(Uri.parse(blog.imagenUri));
                } else {
                    holder.imgItem.setImageResource(R.drawable.ic_launcher_background);
                }

                // Mostrar opciones si es del usuario
                if (blog.autor.equals(usuarioActual)) {
                    holder.itemView.setOnLongClickListener(v -> {
                        PopupMenu menu = new PopupMenu(Ingreso.this, v);
                        menu.getMenu().add("Editar");
                        menu.getMenu().add("Eliminar");
                        menu.setOnMenuItemClickListener(item -> {
                            if (item.getTitle().equals("Eliminar")) {
                                repository.delete(blog.id);
                                actualizarLista();
                                Toast.makeText(Ingreso.this, "Eliminado", Toast.LENGTH_SHORT).show();
                            } else if (item.getTitle().equals("Editar")) {
                                etTitle.setText(blog.titulo);
                                etStory.setText(blog.historia);
                                if (blog.imagenUri != null)
                                    imgPreview.setImageURI(Uri.parse(blog.imagenUri));
                                selectedImageUri = blog.imagenUri != null ? Uri.parse(blog.imagenUri) : null;

                                btnSave.setOnClickListener(v2 -> {
                                    blog.titulo = etTitle.getText().toString();
                                    blog.historia = etStory.getText().toString();
                                    blog.imagenUri = selectedImageUri != null ? selectedImageUri.toString() : null;
                                    repository.update(blog);
                                    actualizarLista();
                                    limpiarFormulario();
                                    Toast.makeText(Ingreso.this, "Actualizado", Toast.LENGTH_SHORT).show();
                                });

                                formularioLayout.setVisibility(View.VISIBLE);
                                btnNuevoPost.setVisibility(View.GONE);
                                btnModoOscuro.setVisibility(View.GONE);
                                btnCerrarSesion.setVisibility(View.GONE);
                            }
                            return true;
                        });
                        menu.show();
                        return true;
                    });
                } else {
                    holder.itemView.setOnLongClickListener(null);
                }
            }

            @Override
            public int getItemCount() {
                return blogList.size();
            }
        };
        rvBlogs.setAdapter(blogAdapter);
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imgPreview.setImageURI(selectedImageUri);
                        Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupListeners() {
        btnNuevoPost.setOnClickListener(v -> {
            formularioLayout.setVisibility(View.VISIBLE);
            btnNuevoPost.setVisibility(View.GONE);
            btnModoOscuro.setVisibility(View.GONE);
            btnCerrarSesion.setVisibility(View.GONE);
        });

        btnCancelar.setOnClickListener(v -> limpiarFormulario());

        btnSelectImage.setOnClickListener(v -> abrirSelectorImagen());

        btnSave.setOnClickListener(v -> {
            String titulo = etTitle.getText().toString();
            String historia = etStory.getText().toString();

            if (titulo.isEmpty() || historia.isEmpty()) {
                Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Blog blog = new Blog(titulo, historia,
                    selectedImageUri != null ? selectedImageUri.toString() : null,
                    usuarioActual);
            repository.insert(blog);
            actualizarLista();
            limpiarFormulario();
            Toast.makeText(this, "Entrada guardada", Toast.LENGTH_SHORT).show();
        });

        btnModoOscuro.setOnClickListener(v -> {
            boolean isDark = (getResources().getConfiguration().uiMode &
                    Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
            ThemeUtils.toggleTheme(this, !isDark);
            recreate();
        });

        btnCerrarSesion.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("UsuariosPrefs", MODE_PRIVATE);
            prefs.edit().remove("usuario_logueado").apply();
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        });
    }

    private void actualizarLista() {
        blogList.clear();
        blogList.addAll(repository.getAll(usuarioActual));
        blogAdapter.notifyDataSetChanged();
    }

    private void abrirSelectorImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void limpiarFormulario() {
        etTitle.setText("");
        etStory.setText("");
        imgPreview.setImageResource(R.drawable.ic_launcher_background);
        selectedImageUri = null;
        formularioLayout.setVisibility(View.GONE);
        btnNuevoPost.setVisibility(View.VISIBLE);
        btnModoOscuro.setVisibility(View.VISIBLE);
        btnCerrarSesion.setVisibility(View.VISIBLE);
    }

    private static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvStory;
        ImageView imgItem;

        BlogViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvStory = itemView.findViewById(R.id.tvItemStory);
            imgItem = itemView.findViewById(R.id.imgItem);
        }
    }
}
