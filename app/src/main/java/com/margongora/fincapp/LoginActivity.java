package com.margongora.fincapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * Controlador principal encargado de la gestión de sesiones y control de acceso.
 * * Implementa la lógica de autenticación mediante Firebase Auth y realiza una
 * verificación de integridad del perfil del usuario en Cloud Firestore antes
 * de conceder acceso al MainActivity. Gestiona además la persistencia de
 * preferencias de usuario para el flujo de bienvenida.
 * * @author Maria del Mar Góngora Sarabia
 */
public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPass;
    private Button btnLogin;
    private TextView tvIrRegistro;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Constante para la ruta de la colección de usuarios
    private static final String PATH_USUARIOS = "artifacts/fincapp/public/data/usuarios";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        mostrarBienvenidaSiEsNuevo();
    }

    /**
     * Vincula los componentes del layout XML con las instancias de la clase
     * y define los listeners para la navegación y ejecución de lógica.
     */
    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvIrRegistro = findViewById(R.id.tvGoToRegister);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> loginUsuario());
        tvIrRegistro.setOnClickListener(v -> startActivity(new Intent(this, RegistroActivity.class)));
    }

    /**
     * Gestiona el flujo de Onboarding mediante {@link SharedPreferences} para determinar si
     * se debe mostrar el cuadro de diálogo de bienvenida inicial.
     */
    private void mostrarBienvenidaSiEsNuevo() {
        SharedPreferences prefs = getSharedPreferences("ConfigApp", MODE_PRIVATE);
        if (prefs.getBoolean("isFirstTimeApp", true)) {
            prefs.edit().putBoolean("isFirstTimeApp", false).apply();

            new AlertDialog.Builder(this)
                    .setTitle("¡Bienvenido a FincApp!")
                    .setMessage("Plataforma integral de gestión comunitaria.\n\n" +
                            "1. Registro de propietarios de cada comunidad.\n" +
                            "2. Proceso de verificación de identidad y de comunidad.\n" +
                            "3. Participación en votaciones en tiempo real.")
                    .setPositiveButton("Continuar", null)
                    .show();
        }
    }

    /**
     * Ejecuta el proceso de autenticación asíncrona.
     * Valida la entrada de datos en el cliente y delega la verificación de
     * credenciales en el proveedor de identidad de Firebase.
     */
    private void loginUsuario() {
        String email = etEmail.getText().toString().trim();
        String pass = etPass.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Validación fallida: Credenciales incompletas", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                comprobarVerificacion(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
            } else {
                setLoading(false);
                Toast.makeText(this, "Error de autenticación: Credenciales no válidas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Realiza una consulta al documento del usuario en Firestore para verificar
     * el estado del atributo 'verificado'. Este paso actúa como un middleware
     * de autorización antes de permitir la navegación al feed principal.
     */
    private void comprobarVerificacion(String uid) {
        db.collection(PATH_USUARIOS).document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Boolean verificado = doc.getBoolean("verificado");
                        if (verificado != null && verificado) {
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(this, VerificacionActivity.class));
                        }
                    }
                    setLoading(false);
                })
                .addOnFailureListener(e -> setLoading(false));
    }

    /**
     * Gestiona el estado visual de la interfaz durante operaciones asíncronas.
     * Alterna la visibilidad entre el componente de progreso y el botón de acción
     * para evitar colisiones de peticiones concurrentes.
     */
    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setVisibility(loading ? View.GONE : View.VISIBLE);
    }
}