package com.example.testlogin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testlogin.data.BlogRepository;
import com.example.testlogin.model.Blog;
import com.example.testlogin.ui.BlogAdapter;

import java.util.List;

public class Ingreso extends AppCompatActivity {

    private EditText etTitle, etStory;
    private TextView tvEmpty;
    private BlogRepository repo;
    private BlogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyThemeFromPrefs(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingreso);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, in) -> {
                    Insets bars = in.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(bars.left,bars.top,bars.right,bars.bottom);
                    return in;
                });

        repo   = new BlogRepository(this);
        etTitle= findViewById(R.id.etTitle);
        etStory= findViewById(R.id.etStory);
        tvEmpty= findViewById(R.id.tvEmpty);
        Button btnSave = findViewById(R.id.btnSave);

        RecyclerView rv = findViewById(R.id.rvBlogs);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BlogAdapter();
        rv.setAdapter(adapter);

        btnSave.setOnClickListener(v -> saveBlog());
        loadBlogs();
    }

    private void saveBlog(){
        String t = etTitle.getText().toString().trim();
        String s = etStory.getText().toString().trim();
        if(TextUtils.isEmpty(t) || TextUtils.isEmpty(s)){
            Toast.makeText(this,"Completa ambos campos",Toast.LENGTH_SHORT).show();
            return;
        }
        repo.insert(new Blog(t,s));
        etTitle.setText("");
        etStory.setText("");
        loadBlogs();
    }

    private void loadBlogs(){
        List<Blog> list = repo.getAll();
        adapter.setData(list);
        tvEmpty.setVisibility(list.isEmpty()? View.VISIBLE:View.GONE);
    }
}
