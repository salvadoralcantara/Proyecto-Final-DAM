package com.example.testlogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.testlogin.data.BlogRepository;
import com.example.testlogin.data.BlogRepository.Blog;
import com.example.testlogin.ThemeUtils;
import java.util.List;

public class Ingreso extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 101;

    EditText etTitle, etStory;
    ImageView imgPreview;
    Button btnSelectImage, btnSave, btnCancelar;
    Button btnModoOscuro, btnCerrarSesion, btnNuevoPost;
    ScrollView formularioLayout;
    RecyclerView rvBlogs;

    Uri selectedImageUri = null;

    BlogRepository repository;
    List<Blog> blogList;
    RecyclerView.Adapter blogAdapter;

    ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyThemeFromPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);

        repository = new BlogRepository(this);

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

        rvBlogs.setLayoutManager(new LinearLayoutManager(this));

        blogList = repository.getAll();

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
            }

            @Override
            public int getItemCount() {
                return blogList.size();
            }
        };

        rvBlogs.setAdapter(blogAdapter);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imgPreview.setImageURI(selectedImageUri);
                        Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show();
                    }
                });

        btnNuevoPost.setOnClickListener(v -> {
            formularioLayout.setVisibility(View.VISIBLE);
            btnNuevoPost.setVisibility(View.GONE);
            btnModoOscuro.setVisibility(View.GONE);
            btnCerrarSesion.setVisibility(View.GONE);
        });

        btnCancelar.setOnClickListener(v -> {
            formularioLayout.setVisibility(View.GONE);
            btnNuevoPost.setVisibility(View.VISIBLE);
            btnModoOscuro.setVisibility(View.VISIBLE);
            btnCerrarSesion.setVisibility(View.VISIBLE);
        });

        btnSelectImage.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                            REQUEST_PERMISSION_CODE);
                } else {
                    abrirSelectorImagen();
                }
            } else {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PERMISSION_CODE);
                } else {
                    abrirSelectorImagen();
                }
            }
        });

        btnSave.setOnClickListener(v -> {
            String titulo = etTitle.getText().toString();
            String historia = etStory.getText().toString();

            if (titulo.isEmpty() || historia.isEmpty()) {
                Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Blog blog = new Blog(titulo, historia, selectedImageUri != null ? selectedImageUri.toString() : null);
            repository.insert(blog);

            blogList.clear();
            blogList.addAll(repository.getAll());
            blogAdapter.notifyDataSetChanged();

            etTitle.setText("");
            etStory.setText("");
            imgPreview.setImageResource(R.drawable.ic_launcher_background);
            selectedImageUri = null;

            formularioLayout.setVisibility(View.GONE);
            btnNuevoPost.setVisibility(View.VISIBLE);
            btnModoOscuro.setVisibility(View.VISIBLE);
            btnCerrarSesion.setVisibility(View.VISIBLE);

            Toast.makeText(this, "Entrada guardada", Toast.LENGTH_SHORT).show();
        });

        btnModoOscuro.setOnClickListener(v -> {
            boolean isDark = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
            ThemeUtils.toggleTheme(Ingreso.this, !isDark);
            recreate();
        });

        btnCerrarSesion.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent intent = new Intent(Ingreso.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void abrirSelectorImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
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

// Cambios de creación de posts