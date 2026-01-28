package com.margongora.fincapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etDni, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias a los componentes del diseño XML
        etDni = findViewById(R.id.et_dni);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarLogin();
            }
        });
    }

    private void validarLogin() {
        String dni = etDni.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (dni.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
        } else if (dni.length() < 9) {
            Toast.makeText(this, "El DNI debe tener al menos 9 caracteres", Toast.LENGTH_SHORT).show();
        } else {
            // Mensaje de éxito temporal
            Toast.makeText(this, "Accediendo con DNI: " + dni, Toast.LENGTH_LONG).show();
        }
    }
}

