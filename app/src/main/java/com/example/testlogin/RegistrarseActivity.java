package com.example.testlogin;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;


public class RegistrarseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrarse);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button botonVolver = findViewById(R.id.buttonVolver);
        botonVolver.setOnClickListener(v -> {
            finish();
        });

        EditText inputUsuario = findViewById(R.id.editTextText);
        EditText inputEmail = findViewById(R.id.editTextText2);
        EditText inputPassword = findViewById(R.id.editTextText4);
        EditText inputConfirmar = findViewById(R.id.editTextText5);

        Button botonGuardar = findViewById(R.id.button4);

        botonGuardar.setOnClickListener(v -> {
            String usuario = inputUsuario.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String confirmar = inputConfirmar.getText().toString().trim();

            if (usuario.length() < 3) {
                Toast.makeText(this, "Usuario debe tener al menos 3 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                Toast.makeText(this, "Email no valido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 5 || !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
                Toast.makeText(this, "Password debe tener minimo 5 caracteres y ser alfanumerico", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmar)) {
                Toast.makeText(this, "Las contrasenas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guarda los registros en SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UsuariosPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("usuario", usuario);
            editor.putString("email", email);
            editor.putString("password", password);
            editor.apply();

            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_LONG).show();

            // Limpia los campos
            inputUsuario.setText("");
            inputEmail.setText("");
            inputPassword.setText("");
            inputConfirmar.setText("");
        });



    }
}