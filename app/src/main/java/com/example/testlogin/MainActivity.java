package com.example.testlogin;


import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyThemeFromPrefs(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // === 2. referencia al switch ===
        SwitchCompat sw = findViewById(R.id.switch_dark);

        // === 3. estado inicial ===
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        sw.setChecked(prefs.getBoolean("dark_mode", false));

        // === 4. listener para guardar y aplicar cambio ===
        sw.setOnCheckedChangeListener((buttonView, isChecked) ->
                ThemeUtils.toggleTheme(MainActivity.this, isChecked));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar SharedPreferences
        SharedPreferences ThemePrefs = getSharedPreferences("UsuariosPrefs", MODE_PRIVATE);

        // Obtener vistas
        EditText inputUsuario = findViewById(R.id.editTextUsuario);
        EditText inputPassword = findViewById(R.id.editTextPassword);
        Button botonIniciar = findViewById(R.id.button);
        Button botonRegistrarse = findViewById(R.id.button2);
        Button botonSalir = findViewById(R.id.button3);

        // Acciones
        botonRegistrarse.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegistrarseActivity.class));
        });

        botonIniciar.setOnClickListener(v -> {
            String userInput = inputUsuario.getText().toString().trim();
            String passInput = inputPassword.getText().toString().trim();

            String usuarioGuardado = prefs.getString("usuario", null);
            String passwordGuardado = prefs.getString("password", null);

            if (userInput.equals(usuarioGuardado) && passInput.equals(passwordGuardado)) {
                Toast.makeText(this, "Ingreso exitoso", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, Ingreso.class));
            } else {
                Toast.makeText(this, "Credenciales incorrectas o inexistentes", Toast.LENGTH_SHORT).show();
            }
        });

        botonSalir.setOnClickListener(v -> {
            finishAffinity();
        });
    }
}
