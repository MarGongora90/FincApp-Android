package com.margongora.fincapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Actividad encargada de gestionar el proceso de votación  en tiempo real.
 * <p>
 * Esta clase permite a los vecinos participar en las decisiones de su comunidad.
 * Implementa un sistema de voto ponderado basado en la cuota de participación
 *  * </p>
 * * <b>Funcionalidades principales:</b>
 * <ul>
 * <li>Verificación de participación previa para evitar votos duplicados.</li>
 * <li>Carga dinámica de la cuota de participación del usuario autenticado.</li>
 * <li>Sincronización en tiempo real de los resultados parciales de la votación.</li>
 * <li>Registro seguro del voto mediante transacciones en subcolecciones de Firestore.</li>
 * </ul>
 * * @author Mar Góngora
 */
public class VotacionesActivasActivity extends AppCompatActivity {

    private TextView tvTituloVotacion, tvVotosFavor, tvVotosContra, tvVotosAbstencion, tvCuotaValor, tvYaVotado;
    private RadioGroup rgOpciones;
    private MaterialButton btnVolver;

    /** Instancia de acceso a  Firestore. */
    private FirebaseFirestore db;
    /** Instancia para la gestión de autenticación de usuarios. */
    private FirebaseAuth auth;
    /** Identificadores necesarios para la ruta de datos. */
    private String comunidadId, votacionId, userId;
    /** Valor del coeficiente de propiedad del usuario. */
    private double cuotaUsuario = 0.0;
    /** control de estado de la interfaz según si el usuario ya participó. */
    private boolean yaVotó = false;

    /**
     * Inicializa la actividad, configura las instancias de Firebase y extrae los parámetros
     * enviados a través del Intent (ID de comunidad y ID de votación).
     * * @param savedInstanceState Estado de la instancia si se reanuda la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votaciones_activas);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        // Recuperar IDs de navegación
        comunidadId = getIntent().getStringExtra("EXTRA_COMUNIDAD_ID");
        votacionId = getIntent().getStringExtra("EXTRA_VOTACION_ID");

        // Enlazar componentes de la UI
        tvTituloVotacion = findViewById(R.id.tvTituloVotacion);
        tvVotosFavor = findViewById(R.id.tvVotosFavor);
        tvVotosContra = findViewById(R.id.tvVotosContra);
        tvVotosAbstencion = findViewById(R.id.tvVotosAbstencion);
        tvCuotaValor = findViewById(R.id.tvCuotaValor);
        rgOpciones = findViewById(R.id.rgOpciones);
        btnVolver = findViewById(R.id.btnVolverVotaciones);

        // Validación de datos antes de iniciar procesos
        if (comunidadId != null && votacionId != null && userId != null) {
            verificarSiYaVoto();
            cargarDatosUsuario();
            escucharVotacion();
            configurarClickVoto();
        } else {
            Toast.makeText(this, "Error de sesión o datos", Toast.LENGTH_LONG).show();
            finish();
        }

        btnVolver.setOnClickListener(v -> finish());
    }

    /**
     * Consulta en Firestore si existe un documento con el ID del usuario en la
     * subcolección 'votos_usuarios'. Si existe, bloquea la interfaz para impedir re-votar.
     */
    private void verificarSiYaVoto() {
        db.collection("artifacts").document("fincapp")
                .collection("public").document("data")
                .collection("comunidades").document(comunidadId)
                .collection("votaciones").document(votacionId)
                .collection("votos_usuarios").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        yaVotó = true;
                        deshabilitarVotacion("Ya has participado en esta votación");
                    }
                });
    }

    /**
     * Desactiva los controles de selección de voto y muestra un mensaje informativo en pantalla.
     * * @param mensaje Texto explicativo que se mostrará al usuario.
     */
    private void deshabilitarVotacion(String mensaje) {
        for (int i = 0; i < rgOpciones.getChildCount(); i++) {
            rgOpciones.getChildAt(i).setEnabled(false);
        }
        if (tvYaVotado != null) {
            tvYaVotado.setVisibility(View.VISIBLE);
            tvYaVotado.setText(mensaje);
        }
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    /**
     * Obtiene el valor de la cuota de participación del usuario desde su perfil en Firestore.
     */
    private void cargarDatosUsuario() {
        db.collection("artifacts").document("fincapp")
                .collection("public").document("data")
                .collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Double cuota = documentSnapshot.getDouble("cuota");
                        cuotaUsuario = (cuota != null) ? cuota : 0.0;
                        tvCuotaValor.setText(String.format(Locale.US, "%.2f%%", cuotaUsuario));
                    }
                });
    }

    /**
     * Establece un SnapshotListener para observar cambios en tiempo real en la votación.
     * Actualiza los marcadores de Favor, Contra y Abstención automáticamente cuando
     * otro vecino emite un voto.
     */
    private void escucharVotacion() {
        DocumentReference vRef = db.collection("artifacts").document("fincapp")
                .collection("public").document("data")
                .collection("comunidades").document(comunidadId)
                .collection("votaciones").document(votacionId);

        vRef.addSnapshotListener((snapshot, e) -> {
            if (e != null || snapshot == null || !snapshot.exists()) return;

            tvTituloVotacion.setText(snapshot.getString("titulo"));
            double fav = snapshot.getDouble("favor") != null ? snapshot.getDouble("favor") : 0.0;
            double con = snapshot.getDouble("contra") != null ? snapshot.getDouble("contra") : 0.0;
            double abs = snapshot.getDouble("abstencion") != null ? snapshot.getDouble("abstencion") : 0.0;

            tvVotosFavor.setText(String.format(Locale.US, "%.2f", fav));
            tvVotosContra.setText(String.format(Locale.US, "%.2f", con));
            tvVotosAbstencion.setText(String.format(Locale.US, "%.2f", abs));
        });
    }

    /**
     * Configura el listener del RadioGroup para detectar la elección del usuario.
     * Solo procede si el usuario no ha votado antes y posee una cuota válida.
     */
    private void configurarClickVoto() {
        rgOpciones.setOnCheckedChangeListener((group, checkedId) -> {
            if (yaVotó) return;

            String campo = "";
            if (checkedId == R.id.rbFavor) campo = "favor";
            else if (checkedId == R.id.rbContra) campo = "contra";
            else if (checkedId == R.id.rbAbstencion) campo = "abstencion";

            if (!campo.isEmpty() && cuotaUsuario > 0) {
                registrarVotoOficial(campo);
            }
        });
    }

    /**
     * Ejecuta el proceso de registro del voto en dos pasos
     * * @param campo Nombre del campo en Firestore a incrementar ("favor", "contra" o "abstencion").
     */
    private void registrarVotoOficial(String campo) {
        yaVotó = true; // Bloqueo preventivo en la UI
        deshabilitarVotacion("Registrando voto...");

        DocumentReference votacionRef = db.collection("artifacts").document("fincapp")
                .collection("public").document("data")
                .collection("comunidades").document(comunidadId)
                .collection("votaciones").document(votacionId);

        // Mapa de datos para el registro individual
        Map<String, Object> registroVoto = new HashMap<>();
        registroVoto.put("timestamp", FieldValue.serverTimestamp());
        registroVoto.put("opcion", campo);

        //  Bloqueo de ID de usuario en la votación
        votacionRef.collection("votos_usuarios").document(userId)
                .set(registroVoto)
                .addOnSuccessListener(aVoid -> {
                    //  Actualización atómica de los totales de la votación
                    votacionRef.update(campo, FieldValue.increment(cuotaUsuario))
                            .addOnSuccessListener(v -> {
                                Toast.makeText(this, "Voto contabilizado con éxito", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Reversión local en caso de error de red o permisos
                    yaVotó = false;
                    for (int i = 0; i < rgOpciones.getChildCount(); i++) {
                        rgOpciones.getChildAt(i).setEnabled(true);
                    }
                    Toast.makeText(this, "Error al votar", Toast.LENGTH_SHORT).show();
                });
    }
}