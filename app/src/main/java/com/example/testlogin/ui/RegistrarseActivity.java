package com.example.testlogin.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testlogin.R;

public class RegistrarseActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyThemeFromPreferences(this); // aplica tema antes de super
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        prefs = getSharedPreferences("UsuariosPrefs", MODE_PRIVATE);

        EditText inputUsuario = findViewById(R.id.editTextText);
        EditText inputEmail = findViewById(R.id.editTextText2);
        EditText inputPassword = findViewById(R.id.editTextText4);
        EditText inputConfirmar = findViewById(R.id.editTextText5);
        Button botonGuardar = findViewById(R.id.button4);
        Button botonVolver = findViewById(R.id.buttonVolver);

        botonGuardar.setOnClickListener(v -> {
            String usuario = inputUsuario.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String confirmar = inputConfirmar.getText().toString().trim();

            if (usuario.isEmpty() || email.isEmpty() || password.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (usuario.length() < 3) {
                Toast.makeText(this, "Usuario debe tener al menos 3 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email no válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 5) {
                Toast.makeText(this, "La contraseña debe tener al menos 5 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmar)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            if (prefs.contains("user_" + usuario)) {
                Toast.makeText(this, "Usuario ya registrado", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_" + usuario, password);
            editor.putString("email_" + usuario, email);
            editor.apply();

            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            finish();
        });

        botonVolver.setOnClickListener(v -> finish());
    }
}
