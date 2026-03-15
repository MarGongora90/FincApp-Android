package com.margongora.fincapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad que gestiona la pantalla de selección de comunidades.
 * <p>
 * Se encarga de conectar con Firebase Firestore, recuperar la lista de comunidades
 * disponibles y presentarlas mediante un {@link RecyclerView}.
 * </p>
 * * @author Mar Góngora
 */
public class MenuPrincipalActivity extends AppCompatActivity implements ComunidadAdaptador.OnItemClickListener {

    /** Etiqueta para logs de depuración. */
    private static final String TAG = "MenuPrincipal";

    /** Componente para mostrar la lista de comunidades. */
    private RecyclerView recyclerView;

    /** Adaptador que vincula los datos de la lista con la interfaz visual. */
    private ComunidadAdaptador adapter;

    /** Lista local que almacena los objetos de tipo Comunidad recuperados. */
    private List<Comunidad> listaComunidades;

    /** Instancia de la base de datos de Firebase. */
    private FirebaseFirestore db;

    /** Barra de progreso para indicar carga de datos. */
    private ProgressBar progressBar;

    /** Botón para cerrar la sesión actual. */
    private MaterialButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        // Inicialización de Firebase Firestore
        db = FirebaseFirestore.getInstance();

        vincularComponentes();
        configurarBotones();
        cargarComunidadesDesdeFirestore();
    }

    /**
     * Inicializa las vistas y configura el RecyclerView con su LayoutManager y Adaptador.
     */
    private void vincularComponentes() {
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.rvComunidades);
        btnLogout = findViewById(R.id.btnLogout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaComunidades = new ArrayList<>();

        adapter = new ComunidadAdaptador(listaComunidades, this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Configura los listeners para los botones de la interfaz.
     */
    private void configurarBotones() {
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {

                Intent intent = new Intent(MenuPrincipalActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    /**
     * Realiza una petición asíncrona a Firestore para obtener los documentos de la colección.
     */
    private void cargarComunidadesDesdeFirestore() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        // Acceso a la colección siguiendo la estructura jerárqu. establecida
        db.collection("artifacts")
                .document("fincapp")
                .collection("public")
                .document("data")
                .collection("comunidades")
                .get()
                .addOnCompleteListener(task -> {

                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        listaComunidades.clear();

                        if (task.getResult().isEmpty()) {
                            Log.d(TAG, "No hay documentos en la ruta especificada.");
                            Toast.makeText(this, "No se encontraron comunidades", Toast.LENGTH_SHORT).show();
                        }

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Comunidad comunidad = document.toObject(Comunidad.class);

                                if (comunidad != null) {
                                    comunidad.setId(document.getId());
                                    listaComunidades.add(comunidad);
                                    Log.d(TAG, "Cargada: " + comunidad.getNombre());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error de mapeo en documento: " + document.getId(), e);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error en Firestore: ", task.getException());
                        Toast.makeText(MenuPrincipalActivity.this,
                                "Error de conexión: " + task.getException().getLocalizedMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Maneja el evento de clic en un elemento de la lista.
     * Redirige a la gestión detallada de la comunidad seleccionada.
     * * @param comunidad Objeto comunidad seleccionado por el usuario.
     */
    @Override
    public void onItemClick(Comunidad comunidad) {
        Intent intent = new Intent(this, GestionComunidadActivity.class);
        intent.putExtra("COMUNIDAD_ID", comunidad.getId());
        intent.putExtra("COMUNIDAD_NOMBRE", comunidad.getNombre());
        startActivity(intent);
    }

    /**
     * Método vinculado al botón de refresco del layout (si se añade uno en el XML).
     * @param view La vista que lanza el evento.
     */
    public void onBtnRefrescarClick(View view) {
        cargarComunidadesDesdeFirestore();
    }
}