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
 * Pantalla encargada de mostrar los resultados finales de una votación ya cerrada.
 * Recupera el recuento de votos a favor, en contra y la cuota de participación
 * desde la base de datos para informar a los vecinos del resultado.
 * * @author Maria del Mar Góngora Sarabia
 */
public class VotacionesFinalizadasActivity extends AppCompatActivity {

    private static final String TAG = "FINCAPP_LOG";


    private TextView tvTitulo, tvResFavor, tvResContra, tvResAbstencion, tvCuotaRes;
    private Button btnVolver;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votaciones_finalizadas);

        // Inicializamos los componentes del diseño
        tvTitulo = findViewById(R.id.tvTituloVotacionFinalizada);
        tvResFavor = findViewById(R.id.tvResFavor);
        tvResContra = findViewById(R.id.tvResContra);
        tvResAbstencion = findViewById(R.id.tvResAbstencion);
        tvCuotaRes = findViewById(R.id.tvCuotaParticipacionRes);
        btnVolver = findViewById(R.id.btnVolverVotaciones);

        db = FirebaseFirestore.getInstance();


        tvTitulo.setText("Consultando acta digital...");

        consultarResultados();

        // Botón para cerrar la pantalla y volver al menú
        btnVolver.setOnClickListener(v -> finish());
    }


    private void consultarResultados() {
        // Recuperamos la información enviada por el Intent
        String tempVotacionId = getIntent().getStringExtra("VOTACION_ID");
        String tempComunidadId = getIntent().getStringExtra("COMUNIDAD_ID");

        // Valores por defecto en caso de que no lleguen los datos
        //if (tempVotacionId == null) tempVotacionId = "Instalacion de Camaras";
       // if (tempComunidadId == null) tempComunidadId = "Gt05Vb5eJsJc...";

        final String idBuscado = tempVotacionId;
        final String comunidadPath = tempComunidadId;

        tvTitulo.setText("Cargando: " + idBuscado);

        // Accedemos a la colección de votos de la comunidad específica
        db.collection("artifacts/fincapp/public/data/comunidades")
                .document(comunidadPath)
                .collection("votos")
                .whereEqualTo("votacionId", idBuscado)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                        actualizarInterfaz(document);
                    } else {
                        tvTitulo.setText("No se han encontrado resultados");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al consultar resultados: " + e.getMessage());
                    tvTitulo.setText("Error al conectar con la base de datos");
                });
    }

    /**
     * Toma los datos numéricos de la base de datos y los escribe en la pantalla.
     */
    private void actualizarInterfaz(QueryDocumentSnapshot doc) {

        Double favor = doc.getDouble("favor");
        Double contra = doc.getDouble("contra");
        Double abstencion = doc.getDouble("abstencion");
        Double cuota = doc.getDouble("cuota_participacion");
        String titulo = doc.getString("votacionId");

        tvTitulo.setText(titulo != null ? titulo : "Resultado Final");


        tvResFavor.setText(String.format(Locale.getDefault(), "%.0f", favor != null ? favor : 0.0));
        tvResContra.setText(String.format(Locale.getDefault(), "%.0f", contra != null ? contra : 0.0));
        tvResAbstencion.setText(String.format(Locale.getDefault(), "%.0f", abstencion != null ? abstencion : 0.0));


        tvCuotaRes.setText(String.format(Locale.getDefault(), "%.2f%%", cuota != null ? cuota : 0.0));
    }
}