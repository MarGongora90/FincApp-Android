package com.margongora.fincapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity: Gestiona el acceso de usuarios a FincApp.
 * Implementa validaciones visuales (setError) y Guía Interactiva (SharedPreferences).
 */
public class MainActivity extends AppCompatActivity {

    private EditText etUsuario, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Enlace de los componentes del diseño XML
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // 2. REQUISITO: Mostrar guía interactiva solo la primera vez que se usa la app
        comprobarGuiaInicial();

        // 3. Configurar el evento de clic del botón de login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Primero validamos que los datos introducidos sean correctos
                if (validarEntrada()) {
                    ejecutarLogin();
                }
            }
        });
    }

    /**
     * REQUISITO DEL PROFESOR: Validar la correcta inserción de datos.
     * Muestra un error visual directo en el campo (setError) si hay fallos.
     */
    private boolean validarEntrada() {
        String dni = etUsuario.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        // Validación de campo vacío con aviso visual directo
        if (dni.isEmpty()) {
            etUsuario.setError("El DNI es obligatorio");
            etUsuario.requestFocus(); // El cursor salta aquí para que el usuario escriba
            return false;
        }

        // Validación de longitud (Requisito de seguridad)
        if (dni.length() < 9) {
            etUsuario.setError("Formato de DNI incorrecto (mínimo 9 caracteres)");
            etUsuario.requestFocus();
            return false;
        }

        // Validación de contraseña
        if (pass.isEmpty()) {
            etPassword.setError("La contraseña no puede estar vacía");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Consulta la base de datos local para verificar el usuario.
     */
    private void ejecutarLogin() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase db = admin.getReadableDatabase();

        String user = etUsuario.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        // Consulta SQL para buscar al usuario
        Cursor fila = db.rawQuery(
                "select nombre from usuarios where dni=? and password=?",
                new String[]{user, pass});

        if (fila.moveToFirst()) {
            // Usuario encontrado: pasamos al menú principal
            String nombreCompleto = fila.getString(0);
            Intent intent = new Intent(MainActivity.this, MenuPrincipalActivity.class);
            intent.putExtra("USUARIO_NOMBRE", nombreCompleto);
            startActivity(intent);
            finish();
        } else {
            // Si el usuario existe pero la clave no, avisamos con Toast
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_LONG).show();
            etPassword.setText("");
        }
        fila.close();
        db.close();
    }

    /**
     * REQUISITO DEL PROFESOR: Guía interactiva (Solo primera ejecución).
     */
    private void comprobarGuiaInicial() {
        SharedPreferences sharedPref = getSharedPreferences("FincAppPrefs", Context.MODE_PRIVATE);
        boolean esPrimeraVez = sharedPref.getBoolean("primera_vez", true);

        if (esPrimeraVez) {
            new AlertDialog.Builder(this)
                    .setTitle("Guía de Inicio")
                    .setMessage("¡Bienvenido a FincApp!\n\nUse su DNI y clave para acceder. Esta guía solo se mostrará una vez.")
                    .setPositiveButton("Empezar", null)
                    .setCancelable(false)
                    .show();

            // Guardar estado para que no se repita
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("primera_vez", false);
            editor.apply();
        }
    }
}