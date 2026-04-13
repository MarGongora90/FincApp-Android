package com.margongora.fincapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Dashboard principal para perfiles con rol de administrador.
 * * Esta Activity centraliza la gestión de la plataforma, permitiendo la navegación
 * hacia los módulos de CRUD de comunidades, gestión de usuarios y publicación
 * de contenido.
 * * @author Maria del Mar Góngora Sarabia
 */
public class AdministradorPanelActivity extends AppCompatActivity {

    private Button btnCrearComunidad, btnGestionarUsuarios, btnPublicarAnuncio, btnGestionarVotaciones, btnCerrarSesion;
    private TextView tvBienvenida, tvCountUsuarios, tvCountAnuncios, tvCountIncidencias;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //base para la estructura de documentos en la base de datos NoSQL
    private static final String BASE_PATH = "artifacts/fincapp/public/data/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_panel);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupListeners();
        cargarEstadisticas();
    }

    /**
     * Inyecta las referencias del layout XML y personaliza la UI con
     * el estado actual de la sesión del objeto FirebaseUser.
     */
    private void initViews() {
        tvBienvenida = findViewById(R.id.tvBienvenidaAdmin);
        tvCountUsuarios = findViewById(R.id.tvCountUsuarios);
        tvCountAnuncios = findViewById(R.id.tvCountAnuncios);
        tvCountIncidencias = findViewById(R.id.tvCountIncidencias);

        btnCrearComunidad = findViewById(R.id.btnIrCrearComunidad);
        btnGestionarUsuarios = findViewById(R.id.btnIrGestionarUsuarios);
        btnPublicarAnuncio = findViewById(R.id.btnIrPublicarAnuncio);
        btnGestionarVotaciones = findViewById(R.id.btnIrGestionarVotaciones);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionAdmin);

        if (mAuth.getCurrentUser() != null) {
            tvBienvenida.setText("Panel de Control\n" + mAuth.getCurrentUser().getEmail());
        }
    }

    /**
     * Ejecuta consultas de agregación (size) sobre las colecciones de Firestore.
     * Al ser operaciones asíncronas, se gestionan mediante OnSuccessListener
     * para actualizar la UI en el hilo principal una vez recibida la Snapshot.
     */
    private void cargarEstadisticas() {
        db.collection(BASE_PATH + "usuarios").get().addOnSuccessListener(queryDocumentSnapshots -> {
            tvCountUsuarios.setText(String.valueOf(queryDocumentSnapshots.size()));
        });

        db.collection(BASE_PATH + "anuncios").get().addOnSuccessListener(queryDocumentSnapshots -> {
            tvCountAnuncios.setText(String.valueOf(queryDocumentSnapshots.size()));
        });

        db.collection(BASE_PATH + "incidencias").get().addOnSuccessListener(queryDocumentSnapshots -> {
            tvCountIncidencias.setText(String.valueOf(queryDocumentSnapshots.size()));
        });
    }

    /**
     * Define el comportamiento de los eventos de click. Implementa la navegación
     * entre Activities mediante Intents y gestiona la invalidación de la
     * sesión en Firebase Auth.
     */
    private void setupListeners() {
        btnCrearComunidad.setOnClickListener(v -> {
            startActivity(new Intent(this, CrearComunidadActivity.class));
        });

        btnGestionarUsuarios.setOnClickListener(v -> {
            startActivity(new Intent(this, GestionUsariosActivity.class));
        });

        btnPublicarAnuncio.setOnClickListener(v -> {
            Toast.makeText(this, "Módulo de anuncios en desarrollo", Toast.LENGTH_SHORT).show();
        });

        btnGestionarVotaciones.setOnClickListener(v -> {
            startActivity(new Intent(this, CrearVotacionActivity.class));
        });

        btnCerrarSesion.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            // Limpieza del back-stack para evitar navegación regresiva tras logout
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Callback del ciclo de vida para garantizar que las métricas del panel
     * se sincronicen cada vez que la Activity vuelve al primer plano.
     */
    @Override
    protected void onResume() {
        super.onResume();
        cargarEstadisticas();
    }
}