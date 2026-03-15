package com.margongora.fincapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * CLASE PRINCIPAL: MainActivity
 * * Esta clase gestiona la entrada a la aplicación FincApp.
 * Se encarga de la autenticación de usuarios mediante Firebase Auth
 * * @author Mar Góngora
  */
public class MainActivity extends AppCompatActivity {

    /** Campo de texto para el correo electrónico del usuario */
    private EditText etEmail;
    /** Campo de texto para la contraseña del usuario */
    private EditText etPassword;
    /** Botón para ejecutar el inicio de sesión */
    private Button btnLogin;
    /** Instancia de Firebase Auth para gestionar la sesión */
    private FirebaseAuth mAuth;
    /** Instancia de Firestore para acceder a los datos de la comunidad */
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de servicios de Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Vinculación de componentes de la UI
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // bienvenida inicial
        comprobarPrimerAcceso();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarLogin();
            }
        });
    }

    /**
     * Valida los campos de entrada antes de la autenticación.
     * Verifica que el email sea válido y la contraseña cumpla con la longitud.
     */
    private void intentarLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Introduce un email válido");
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("Se necesitan 6 caracteres");
            return;
        }

        realizarAuthEnFirebase(email, password);
    }

    /**
     * Realiza la llamada a Firebase Auth.
     * * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     */
    private void realizarAuthEnFirebase(String email, String password) {
        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        obtenerNombreDeUsuarioYContinuar();
                    } else {
                        btnLogin.setEnabled(true);
                        mostrarError("Acceso Erróneo", "Correo o contraseña incorrecta.");
                    }
                });
    }

    /**
     * Consulta la colección 'usuarios' en Firestore para recuperar el nombre.
     * Da una bienvenida personalizada en lugar del email.
     */
    private void obtenerNombreDeUsuarioYContinuar() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("usuarios").document(user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        String nombreReal = "";
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                nombreReal = document.getString("nombre");
                            }
                        }

                        // Si no hay nombre, coge parte del mail
                        if (nombreReal == null || nombreReal.isEmpty()) {
                            nombreReal = user.getEmail().split("@")[0];
                        }

                        Toast.makeText(MainActivity.this, "¡Bienvenido, " + nombreReal + "!", Toast.LENGTH_SHORT).show();
                        irAlMenuPrincipal(nombreReal);
                    });
        }
    }

    /**
     * Diálogo de alerta para errores del sistema.
     */
    private void mostrarError(String titulo, String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    /**
     * Transición a la actividad principal del menú.
     * @param nombreUsuario El nombre recuperado para ser usado en el resto de pantallas.
     */
    private void irAlMenuPrincipal(String nombreUsuario) {
        Intent intent = new Intent(MainActivity.this, MenuPrincipalActivity.class);
        intent.putExtra("USUARIO_NOMBRE", nombreUsuario);
        startActivity(intent);
        finish();
    }

    /**
     * Implementa la guía de usuario utilizando SharedPreferences.
     * Se muestra únicamente la primera vez que se ejecuta la aplicación tras la instalación.
     */
    private void comprobarPrimerAcceso() {
        SharedPreferences sharedPref = getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        boolean esPrimerInicio = sharedPref.getBoolean("isFirstRun", true);

        if (esPrimerInicio) {
            new AlertDialog.Builder(this)
                    .setTitle("Bienvenido a FincApp")
                    .setMessage("Esta es una herramienta para la gestión de su comunidad.\n\nAcceda con sus credenciales proporcionadas por el administrador.")
                    .setPositiveButton("Comenzar", (dialog, which) -> {
                        sharedPref.edit().putBoolean("isFirstRun", false).apply();
                    })
                    .setCancelable(false)
                    .show();
        }
    }
}