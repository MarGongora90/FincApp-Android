package com.margongora.fincapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Controlador de entrada a la aplicación.
 * * Gestiona el ciclo de vida de la autenticación mediante Firebase Auth y actúa como
 * middleware de autorización, validando el estado de verificación del perfil y
 * redirigiendo al usuario hacia el dashboard correspondiente (Admin o Propietario)
 * según los privilegios definidos en Cloud Firestore.
 * * @author Maria del Mar Góngora Sarabia
 */
public class MainActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Rutas para la persistencia en el backend NoSQL
    private static final String BASE_PATH = "artifacts/fincapp/public/data/";
    private static final String RUTA_USUARIOS = BASE_PATH + "usuarios";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Flag de sistema para evitar el timeout de pantalla durante el proceso de login
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initInterface();
    }

    /**
     * Inicializa los componentes de la vista y vincula los listeners de eventos.
     */
    private void initInterface() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> ejecutarFlujoLogin());
        tvGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegistroActivity.class))
        );
    }

    /**
     * Dirige el proceso de login.
     * Realiza la validación sintáctica de los campos y lanza la petición asíncrona
     * al proveedor de identidad de Firebase.
     */
    private void ejecutarFlujoLogin() {
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Error de validación: Credenciales incompletas", Toast.LENGTH_SHORT).show();
            return;
        }

        toggleInteraccion(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) sincronizarPerfilUsuario(user.getUid());
                    } else {
                        toggleInteraccion(true);
                        Toast.makeText(this, "Error de autenticación: Credenciales no válidas", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Recupera el documento del usuario desde Firestore para aplicar la lógica
     * de control de acceso basada en el estado de verificación y roles.
     */
    private void sincronizarPerfilUsuario(String uid) {
        db.collection(RUTA_USUARIOS).document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        manejarSesionInvalida();
                        return;
                    }

                    // 1. Capa de Seguridad: Verificación de activación de cuenta
                    Boolean verificado = doc.getBoolean("verificado");
                    if (verificado == null || !verificado) {
                        toggleInteraccion(true);
                        lanzarActividad(VerificacionActivity.class, null);
                        return;
                    }

                    // 2. Capa de Negocio: Selección de flujo por Rol
                    String rol = doc.getString("rol");
                    List<String> comunidades = (List<String>) doc.get("idComunidad");

                    if ("administrador".equalsIgnoreCase(rol)) {
                        lanzarActividad(AdministradorPanelActivity.class, null);
                    } else if ("propietario".equalsIgnoreCase(rol)) {
                        gestionarNavegacionPropietario(comunidades);
                    }
                })
                .addOnFailureListener(e -> {
                    toggleInteraccion(true);
                    Log.e("FIRESTORE_ERROR", "Fallo al recuperar datos del perfil", e);
                });
    }

    private void gestionarNavegacionPropietario(List<String> comunidades) {
        if (comunidades == null || comunidades.isEmpty()) {
            toggleInteraccion(true);
            Toast.makeText(this, "Error de perfil: No se han vinculado comunidades", Toast.LENGTH_LONG).show();
        } else {
            // Se inyecta el ID de la comunidad principal para el contexto del menú
            lanzarActividad(MenuPrincipalActivity.class, comunidades.get(0));
        }
    }

    /**
     * Ejecuta la transición entre Activities limpiando el stack de navegación
     * para evitar retrocesos accidentales tras el login (FLAG_ACTIVITY_CLEAR_TASK).
     */
    private void lanzarActividad(Class<?> destino, String idComunidad) {
        Intent intent = new Intent(this, destino);
        if (idComunidad != null) intent.putExtra("idComunidad", idComunidad);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void manejarSesionInvalida() {
        toggleInteraccion(true);
        mAuth.signOut();
        Toast.makeText(this, "Error: El perfil de usuario no existe", Toast.LENGTH_SHORT).show();
    }

    private void toggleInteraccion(boolean habilitar) {
        btnLogin.setEnabled(habilitar);
    }
}