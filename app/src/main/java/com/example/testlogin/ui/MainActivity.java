package com.example.testlogin.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.testlogin.R;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyThemeFromPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("UsuariosPrefs", MODE_PRIVATE);

        EditText inputUsuario = findViewById(R.id.editTextUsuario);
        EditText inputPassword = findViewById(R.id.editTextPassword);
        Button botonIniciar = findViewById(R.id.button);
        Button botonRegistrarse = findViewById(R.id.button2);
        Button botonSalir = findViewById(R.id.button3);
        SwitchCompat sw = findViewById(R.id.switch_dark);

        SharedPreferences themePrefs = getSharedPreferences("theme_preferences", MODE_PRIVATE);
        sw.setChecked(themePrefs.getBoolean("is_dark_mode_enabled", false));
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemeUtils.toggleTheme(MainActivity.this, isChecked);
            recreate();
        });

        botonRegistrarse.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegistrarseActivity.class));
        });

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

        botonSalir.setOnClickListener(v -> finishAffinity());
    }

    @Override
    protected void onResume() {
        super.onResume();
        SwitchCompat sw = findViewById(R.id.switch_dark);
        SharedPreferences themePrefs = getSharedPreferences("theme_preferences", MODE_PRIVATE);
        sw.setChecked(themePrefs.getBoolean("is_dark_mode_enabled", false));
    }
}