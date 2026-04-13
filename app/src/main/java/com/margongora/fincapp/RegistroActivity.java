package com.margongora.fincapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Controlador encargado de dar e alta nuevos usuarios y lógica de verificación.
 * * Implementa un flujo de registro en tres fases:
 * 1. Autenticación gestionada por Firebase Auth
 * 2. Persistencia de perfiles extendidos y metadatos de seguridad en Cloud Firestore.
 * 3. Notificación transaccional asíncrona mediante protocolo SMTP (Mailtrap) para validación de cuenta.
 * * @author Maria del Mar Góngora Sarabia
 */
public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etEmail, etPass;
    private RadioGroup rgRoles;
    private Button btnRegistrar, btnSeleccionarComunidades;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Namespaces de persistencia NoSQL
    private static final String PATH_COMUNIDADES = "artifacts/fincapp/public/data/comunidades";
    private static final String PATH_USUARIOS = "artifacts/fincapp/public/data/usuarios";

    private final List<String> nombresComunidades = new ArrayList<>();
    private final List<String> idsComunidades = new ArrayList<>();
    private final List<String> comunidadesSeleccionadasIds = new ArrayList<>();
    private boolean[] checkedItems;

    private View layoutComunidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initInterface();
        cargarDatasetComunidades();
        setupEventHandlers();
    }

    private void initInterface() {
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        rgRoles = findViewById(R.id.rgRoles);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnSeleccionarComunidades = findViewById(R.id.btnSeleccionarComunidades);
        layoutComunidad = findViewById(R.id.layoutSeleccionComunidad);
    }

    private void setupEventHandlers() {
        // Gestión dinámica de la UI según el rol seleccionado
        rgRoles.setOnCheckedChangeListener((group, checkedId) -> {
            boolean esPropietario = (checkedId == R.id.rbPropietario);
            layoutComunidad.setVisibility(esPropietario ? View.VISIBLE : View.GONE);
            if (!esPropietario) comunidadesSeleccionadasIds.clear();
        });

        btnSeleccionarComunidades.setOnClickListener(v -> invocarSelectorMultiopcion());
        btnRegistrar.setOnClickListener(v -> validarYProcesarRegistro());
    }

    /**
     * Recupera el catálogo de comunidades mediante una consulta unaria a Firestore.
     * Sincroniza los vectores de nombres e IDs para garantizar la integridad referencial.
     */
    private void cargarDatasetComunidades() {
        db.collection(PATH_COMUNIDADES).get().addOnSuccessListener(queryDocumentSnapshots -> {
            nombresComunidades.clear();
            idsComunidades.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String nombre = doc.getString("nombre");
                if (nombre != null) {
                    nombresComunidades.add(nombre);
                    idsComunidades.add(doc.getId());
                }
            }
            checkedItems = new boolean[nombresComunidades.size()];
        }).addOnFailureListener(e -> Log.e("FIREBASE_FETCH", "Fallo al cargar comunidades", e));
    }

    /**
     * Implementa un diálogo modal de selección múltiple (Checkboxes) para la
     * vinculación de comunidades al perfil del usuario.
     */
    private void invocarSelectorMultiopcion() {
        if (nombresComunidades.isEmpty()) {
            cargarDatasetComunidades();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Vincular Comunidades")
                .setMultiChoiceItems(nombresComunidades.toArray(new String[0]), checkedItems, (dialog, which, isChecked) -> {
                    String id = idsComunidades.get(which);
                    if (isChecked) {
                        if (!comunidadesSeleccionadasIds.contains(id)) comunidadesSeleccionadasIds.add(id);
                    } else {
                        comunidadesSeleccionadasIds.remove(id);
                    }
                })
                .setPositiveButton("Confirmar", (dialog, which) ->
                        btnSeleccionarComunidades.setText("Asignadas: " + comunidadesSeleccionadasIds.size()))
                .show();
    }

    /**
     * Ejecuta el flujo de alta atómica.
     * Genera un OTP (One-Time Password) de 6 dígitos antes de delegar la creación
     * de la cuenta en el SDK de Firebase Auth.
     */
    private void validarYProcesarRegistro() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPass.getText().toString().trim();

        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Validación fallida:faltan datos obligatorios ", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegistrar.setEnabled(false);
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
                persistirMetadataUsuario(mAuth.getCurrentUser().getUid(), nombre, email, otp);
                despacharEmailTransaccional(email, otp, nombre);
            } else {
                btnRegistrar.setEnabled(true);
                Toast.makeText(this, "Error Auth: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Serializa los datos extendidos del usuario en el nodo 'usuarios' de Firestore.
     * Define el estado inicial 'verificado=false' como flag de control de acceso.
     */
    private void persistirMetadataUsuario(String uid, String nombre, String email, String otp) {
        int selectedId = rgRoles.getCheckedRadioButtonId();
        RadioButton rb = findViewById(selectedId);
        String rol = (rb != null) ? rb.getText().toString().toLowerCase() : "vecino";

        Map<String, Object> uMap = new HashMap<>();
        uMap.put("uid", uid);
        uMap.put("nombre", nombre);
        uMap.put("email", email);
        uMap.put("rol", rol);
        uMap.put("idComunidad", comunidadesSeleccionadasIds);
        uMap.put("verificado", false);
        uMap.put("codigoVerificacion", otp);

        db.collection(PATH_USUARIOS).document(uid).set(uMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registro completado. Verifique su email.", Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    /**
     * Implementa la lógica de notificación SMTP en un hilo de ejecución secundario (Background Thread).
     * Utiliza la librería JavaMail para la comunicación con el sandbox de Mailtrap,
     * evitando el bloqueo del hilo principal de la UI.
     */
    private void despacharEmailTransaccional(String destino, String otp, String nombre) {
        final String user = "cbd8d353b16bb1";
        final String pass = "5edddc7423648f";

        new Thread(() -> {
            try {
                Properties p = new Properties();
                p.put("mail.smtp.auth", "true");
                p.put("mail.smtp.starttls.enable", "true");
                p.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
                p.put("mail.smtp.port", "2525");

                Session s = Session.getInstance(p, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pass);
                    }
                });

                Message m = new MimeMessage(s);
                m.setFrom(new InternetAddress("no-reply@fincapp.com"));
                m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
                m.setSubject("Activación de Cuenta - FincApp");
                m.setText("Estimado/a " + nombre + ",\n\n" +
                        "Su código de seguridad es: " + otp + "\n\n" +
                        "Introdúzcalo en la App para finalizar el proceso.");

                Transport.send(m);
            } catch (Exception e) {
                Log.e("SMTP_DISPATCH", "Fallo en el servicio de mensajería", e);
            }
        }).start();
    }
}