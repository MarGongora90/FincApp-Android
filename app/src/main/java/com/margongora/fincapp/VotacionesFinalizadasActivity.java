package com.margongora.fincapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Locale;

/**
 * Actividad encargada de consultar y mostrar los resultados finales de una votación específica.
 * <p>
 * Implementa una lógica de consulta basada en el campo 'votacionId' dentro de la
 * jerarquía de colecciones del proyecto.
 * </p>
 */
public class VotacionesFinalizadasActivity extends AppCompatActivity {

    //PAra ver el Logcat
    private static final String TAG = "FINCAPP_LOG";

    // Componentes de la Interfaz de Usuario
    private TextView tvTitulo, tvResFavor, tvResContra, tvResAbstencion, tvCuotaRes;
    private Button btnVolver;

    /** Instancia de la base de datos Firestore. */
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votaciones_finalizadas);

        Log.d(TAG, "Activity Iniciada correctamente");

        // Vinculación de los componentes del layout XML
        tvTitulo = findViewById(R.id.tvTituloVotacionFinalizada);
        tvResFavor = findViewById(R.id.tvResFavor);
        tvResContra = findViewById(R.id.tvResContra);
        tvResAbstencion = findViewById(R.id.tvResAbstencion);
        tvCuotaRes = findViewById(R.id.tvCuotaParticipacionRes);
        btnVolver = findViewById(R.id.btnVolverVotaciones);

        db = FirebaseFirestore.getInstance();

        // Estado inicial de carga
        tvTitulo.setText("Buscando en base de datos...");

        // Identificador de la votación que deseamos consultar.
        String tituloIdBuscado = "Cambio administrador";

        consultarFirestore(tituloIdBuscado);

        // Finaliza la actividad para regresar al menú anterior
        btnVolver.setOnClickListener(v -> finish());
    }

    /**
     * Realiza la petición a Firestore para obtener los datos del documento de votación.
     * <p>
     * Utiliza una ruta de colección estricta: artifacts -> fincapp -> public -> data -> votos.
     * </p>
     * * @param id El identificador de la votación
     */
    private void consultarFirestore(String id) {
        Log.d(TAG, "Consultando colección: artifacts/fincapp/public/data/votos");

        db.collection("artifacts")
                .document("fincapp")
                .collection("public")
                .document("data")
                .collection("votos")
                .whereEqualTo("votacionId", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            Log.d(TAG, "Documento encontrado");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                actualizarCampos(document);
                            }
                        } else {
                            Log.e(TAG, "No existe el documento con votacionId: " + id);
                            tvTitulo.setText("ERROR: No se encontró '" + id + "'");
                        }
                    } else {
                        // Gestión de errores o permisos
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                        Log.e(TAG, "Fallo de conexión: " + errorMsg);
                        tvTitulo.setText("ERROR DB: " + errorMsg);
                    }
                });
    }

    /**
     * Extrae la información del documento de Firestore y actualiza la UI.
     * * @param doc El documento de Firestore que contiene los resultados.
     */
    private void actualizarCampos(QueryDocumentSnapshot doc) {
        tvTitulo.setText(doc.getString("votacionId"));

        // Extracción segura de valores numéricos
        Double favor = doc.getDouble("favor");
        Double contra = doc.getDouble("contra");
        Double abstencion = doc.getDouble("abstencion");
        Double cuota = doc.getDouble("cuota_participacion");

        // Formateo de los datos en los TextViews correspondientes
        tvResFavor.setText(String.format(Locale.getDefault(), "%.1f", favor != null ? favor : 0.0));
        tvResContra.setText(String.format(Locale.getDefault(), "%.1f", contra != null ? contra : 0.0));
        tvResAbstencion.setText(String.format(Locale.getDefault(), "%.1f", abstencion != null ? abstencion : 0.0));
        tvCuotaRes.setText(String.format(Locale.getDefault(), "%.1f%%", cuota != null ? cuota : 0.0));

        Log.d(TAG, "UI Actualizada con éxito");
    }
}