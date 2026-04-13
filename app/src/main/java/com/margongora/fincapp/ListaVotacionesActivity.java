package com.margongora.fincapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador encargado de la visualización y sincronización de procesos votaciones activos.
 * * Esta Activity utiliza consultas filtradas sobre subcolecciones anidadas para
 * *segmentar las votaciones por comunidad.
 * * @author Maria del Mar Góngora Sarabia
 * @version 1.5
 */
public class ListaVotacionesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VotacionAdapter adapter;
    private final List<Votacion> listaVotaciones = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idComunidad;

    // Namespace base para la arquitectura de datos jerárquica
    private static final String PATH_BASE = "artifacts/fincapp/public/data/comunidades";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_votaciones);

        // Extracción de metadatos del Intent para definir el contexto de la consulta
        idComunidad = getIntent().getStringExtra("COMUNIDAD_ID");

        if (idComunidad == null || idComunidad.isEmpty()) {
            manejarErrorContexto();
            return;
        }

        initInterface();
        suscribirCambiosVotaciones();
    }

    /**
     * Inicializa los componentes de la interfaz de usuario y el adaptador del {@link RecyclerView}.
     */
    private void initInterface() {
        recyclerView = findViewById(R.id.rvVotaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inyección del ID de comunidad en el adaptador para permitir transacciones de voto posteriores
        adapter = new VotacionAdapter(listaVotaciones, this, idComunidad);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Establece un SnapshotListener asíncrono con Cloud Firestore.
     * * Implementa un filtrado por predicado ("estado" == "Activa") en el lado del servidor,
     */
    private void suscribirCambiosVotaciones() {
        db.collection(PATH_BASE)
                .document(idComunidad)
                .collection("votaciones")
                .whereEqualTo("estado", "Activa")
                .addSnapshotListener((QuerySnapshot value, FirebaseFirestoreException error) -> {
                    if (error != null) {
                        Log.e("FIRESTORE_SYNC", "Fallo en la suscripción de datos: " + error.getMessage());
                        return;
                    }

                    if (value != null) {
                        actualizarDataset(value);
                    }
                });
    }

    /**
     * Realiza la deserialización automática de los documentos a objetos {@link Votacion}
     * y notifica al adaptador la invalidación del dataset anterior.
     */
    private void actualizarDataset(QuerySnapshot snapshot) {
        listaVotaciones.clear();

        listaVotaciones.addAll(snapshot.toObjects(Votacion.class));

        if (listaVotaciones.isEmpty()) {
            Log.d("UI_STATE", "Documento vacío para la comunidad: " + idComunidad);
        }

        adapter.notifyDataSetChanged();
    }

    private void manejarErrorContexto() {
        Toast.makeText(this, "Error de localización: ID de comunidad no encontrado", Toast.LENGTH_SHORT).show();
        finish();
    }
}