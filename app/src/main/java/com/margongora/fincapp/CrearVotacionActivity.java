package com.margongora.fincapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador encargado de la gestión y persistencia de procesos de votacion.
 * * Esta Activity implementa una arquitectura de datos jerárquica, vinculando
 * las votaciones como subcolecciones de una comunidad específica. Gestiona
 * la integridad referencial mediante el mapeo de IDs de documentos en
 * componentes de selección {@link Spinner}.
 * * @author Maria del Mar Góngora Sarabia
 */
public class CrearVotacionActivity extends AppCompatActivity {
    private Spinner spinnerComunidades;
    private EditText etTitulo, etDescripcion;
    private Button btnGuardar;
    private FirebaseFirestore db;

    // Estructuras de datos para el mapeo posicional de IDs de documentos
    private List<String> listaNombres = new ArrayList<>();
    private List<String> listaIds = new ArrayList<>();

    // Espacio de nombres jerárquico para el acceso a la colección de comunidades
    private final String BASE_PATH = "artifacts/fincapp/public/data/comunidades";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_votaciones);

        db = FirebaseFirestore.getInstance();
        initViews();
        cargarComunidades();
    }

    private void initViews() {
        spinnerComunidades = findViewById(R.id.spinnerComunidadesVotacion);
        etTitulo = findViewById(R.id.etTituloVotacion);
        etDescripcion = findViewById(R.id.etDescripcionVotacion);
        btnGuardar = findViewById(R.id.btnGuardarVotacion);

        btnGuardar.setOnClickListener(v -> guardarVotacion());
    }

    /**
     * Realiza una consulta asíncrona a la colección de comunidades para
     * el adaptador del Spinner. Sincroniza dos listas paralelas para mantener
     * la correlación entre el nombre visualizado y el UID persistente.
     */
    private void cargarComunidades() {
        db.collection(BASE_PATH)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaNombres.clear();
                    listaIds.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String nombre = doc.getString("nombre");
                        if (nombre != null) {
                            listaNombres.add(nombre);
                            listaIds.add(doc.getId());
                        }
                    }
                    // Implementación de adaptador estándar de Android para componentes de selección
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaNombres);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerComunidades.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Log.e("FIREBASE_FETCH", "Fallo al recuperar datos de comunidades", e));
    }

    /**
     * Instancia un nuevo registro de votación en una subcolección de Firestore.
     * * Implementa la lógica de inicialización de contadores de votos a cero y
     * establece el estado inicial mediante una constante de cadena para asegurar
     * la compatibilidad con los filtros de consulta (Query Filters) en otras vistas.
     */
    private void guardarVotacion() {
        String titulo = etTitulo.getText().toString().trim();
        String desc = etDescripcion.getText().toString().trim();
        int pos = spinnerComunidades.getSelectedItemPosition();

        if (titulo.isEmpty() || desc.isEmpty() || pos == -1) {
            Toast.makeText(this, "Validación fallida", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardar.setEnabled(false);
        String idComu = listaIds.get(pos);

        // Generación de referencia de documento con ID autogenerado
        DocumentReference nuevaRef = db.collection(BASE_PATH)
                .document(idComu)
                .collection("votaciones")
                .document();

        // Mapeo manual de campos para garantizar la integridad del esquema NoSQL
        Map<String, Object> votacionMap = new HashMap<>();
        votacionMap.put("id", nuevaRef.getId());
        votacionMap.put("titulo", titulo);
        votacionMap.put("descripcion", desc);
        votacionMap.put("estado", "Activa");
        votacionMap.put("votosSi", 0);
        votacionMap.put("votosNo", 0);
        votacionMap.put("votosAbstencion", 0);
        votacionMap.put("fechaCreacion", System.currentTimeMillis());

        // Operación de escritura asíncrona mediante el método set()
        nuevaRef.set(votacionMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Transacción de creación completada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnGuardar.setEnabled(true);
                    Log.e("FIREBASE_WRITE", "Excepción de seguridad o permisos en la escritura", e);
                    Toast.makeText(this, "Error de persistencia: Verifique privilegios de administrador", Toast.LENGTH_LONG).show();
                });
    }
}