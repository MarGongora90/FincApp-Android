package com.margongora.fincapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Dashboard principal del usuario. Gestiona la lógica de  datos
 * para perfiles multi-comunidad.
 * * @author Maria del Mar Góngora Sarabia
 * @version 1.3
 */
public class MenuPrincipalActivity extends AppCompatActivity implements ComunidadAdaptador.OnItemClickListener {

    private static final String TAG = "MenuPrincipal";
    private static final String PATH_USUARIOS = "artifacts/fincapp/public/data/usuarios";
    private static final String PATH_COMUNIDADES = "artifacts/fincapp/public/data/comunidades";

    private RecyclerView recyclerView;
    private ComunidadAdaptador adapter;
    private List<Comunidad> listaComunidades;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private ProgressBar progressBar;
    private MaterialButton btnLogout, btnAdminPanel;
    private TextView tvNombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initInterface();
        gestionarOnboardingLocal();
        sincronizarPerfilYComunidades();
    }

    private void initInterface() {
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.rvComunidades);
        btnLogout = findViewById(R.id.btnLogout);
        btnAdminPanel = findViewById(R.id.btnAdminPanel);
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaComunidades = new ArrayList<>();
        adapter = new ComunidadAdaptador(listaComunidades, this);
        recyclerView.setAdapter(adapter);

        btnLogout.setOnClickListener(v -> ejecutarSignOut());
    }

    /**
     * Implementa la lógica de bienvenida persistida en {@link SharedPreferences}.
     * Garantiza que el flujo explicativo se presente únicamente en la primera
     * instancia de acceso al panel principal.
     */
    private void gestionarOnboardingLocal() {
        SharedPreferences prefs = getSharedPreferences("ConfigApp", MODE_PRIVATE);
        if (prefs.getBoolean("isFirstTimeMenu", true)) {
            prefs.edit().putBoolean("isFirstTimeMenu", false).apply();

            new AlertDialog.Builder(this)
                    .setTitle("¡Bienvenido a tu Panel!")
                    .setMessage("Desde aqui tendrás acceso a tus propiedades:\n\n" +
                            "• Participación en procesos de voto.\n" +
                            "• Consulta de histórico de actas.\n" +
                            "• Gestión de perfil de usuario.")
                    .setPositiveButton("Entendido", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .show();
        }
    }

    /**
     * Recupera el perfil del usuario autenticado.
     * Gestiona la polimorfia del campo 'idComunidad' (soporta tipos String y List)
     * para asegurar la compatibilidad con diferentes esquemas de registro.
     */
    private void sincronizarPerfilYComunidades() {
        String uid = mAuth.getUid();
        if (uid == null) return;

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        db.collection(PATH_USUARIOS).document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        actualizarHeaderUI(doc);
                        procesarEntidadesVinculadas(doc.get("idComunidad"));
                    } else {
                        manejarErrorPersistencia();
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Excepción en sincronización: ", e));
    }

    private void actualizarHeaderUI(DocumentSnapshot doc) {
        String nombre = doc.getString("nombre");
        String rol = doc.getString("rol");
        if (nombre != null) tvNombreUsuario.setText("Hola, " + nombre);
        if ("administrador".equalsIgnoreCase(rol)) btnAdminPanel.setVisibility(View.VISIBLE);
    }

    /**
     *  Resolución de referencias cruzadas.
     * Convierte el ID (o lista de IDs) en una colección de Tareas asíncronas
     * para recuperar las entidades completas de la colección 'comunidades'.
     */
    private void procesarEntidadesVinculadas(Object campoComunidad) {
        List<String> ids = new ArrayList<>();

        if (campoComunidad instanceof List) {
            ids.addAll((List<String>) campoComunidad);
        } else if (campoComunidad instanceof String) {
            ids.add((String) campoComunidad);
        }

        if (!ids.isEmpty()) {
            ejecutarCargaParalela(ids);
        } else {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Implementa la ejecución concurrente de peticiones de red.
     * Utiliza  para sincronizar la
     * actualización del adaptador una vez todas las promesas han sido resueltas.
     */
    private void ejecutarCargaParalela(List<String> idsComunidad) {
        List<Task<DocumentSnapshot>> tareas = new ArrayList<>();

        for (String id : idsComunidad) {
            tareas.add(db.collection(PATH_COMUNIDADES).document(id).get());
        }

        Tasks.whenAllSuccess(tareas).addOnSuccessListener(resultados -> {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            listaComunidades.clear();

            for (Object res : resultados) {
                DocumentSnapshot doc = (DocumentSnapshot) res;
                if (doc.exists()) {
                    Comunidad c = doc.toObject(Comunidad.class);
                    if (c != null) {
                        c.setId(doc.getId());
                        listaComunidades.add(c);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onItemClick(Comunidad comunidad) {
        Intent intent = new Intent(this, GestionComunidadActivity.class);
        intent.putExtra("COMUNIDAD_ID", comunidad.getId());
        intent.putExtra("COMUNIDAD_NOMBRE", comunidad.getNombre());
        startActivity(intent);
    }

    private void ejecutarSignOut() {
        mAuth.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void manejarErrorPersistencia() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Error: Perfil de usuario", Toast.LENGTH_SHORT).show();
    }
}