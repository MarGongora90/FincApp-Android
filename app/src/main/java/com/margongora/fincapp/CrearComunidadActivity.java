package com.margongora.fincapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador encargado de la persistencia de nuevas entidades.
 * * Implementa la lógica necesaria para la inicialización de una comunidad,
 * estableciendo las relaciones de propiedad y los roles administrativos
 * iniciales mediante estructuras de datos distribuidas en Cloud Firestore.
 * * @author Maria del Mar Góngora Sarabia
 */
public class CrearComunidadActivity extends AppCompatActivity {

    private EditText etNombreComunidad, etDireccion;
    private Button btnGuardarComunidad;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Espacio de nombres (Namespace) jerárquico para la colección en Firestore
    private static final String PATH_COMUNIDADES = "artifacts/fincapp/public/data/comunidades";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_comunidad);

        // Inicialización de servicios de persistencia y autenticación
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initViews();
    }

    /**
     * Inyecta las dependencias de la UI y vincula los event listeners.
     */
    private void initViews() {
        etNombreComunidad = findViewById(R.id.etNombreComunidad);
        etDireccion = findViewById(R.id.etDireccion);
        btnGuardarComunidad = findViewById(R.id.btnGuardarComunidad);

        // Uso de Lambda para simplificar el Callback de interacción
        btnGuardarComunidad.setOnClickListener(v -> crearNuevaComunidad());
    }

    /**
     * Procesa la validación de campos y ejecuta la transacción de inserción en Firestore.
     * Genera un documento con una estructura de datos basada en pares clave-valor (Map),
     * cumpliendo con los requisitos de gestión de roles (admins/miembros).
     */
    private void crearNuevaComunidad() {
        String nombre = etNombreComunidad.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();

        // Verificación de estado de sesión (Pre-condition check)
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Error de sesión: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nombre.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(this, "Error de validación: Existen campos obligatorios vacíos", Toast.LENGTH_SHORT).show();
            return;
        }

        String adminId = mAuth.getCurrentUser().getUid();

        // Inicialización de colecciones de UID para control de acceso y participación
        List<String> administradores = new ArrayList<>();
        administradores.add(adminId);

        List<String> miembros = new ArrayList<>();
        miembros.add(adminId);

        // Definición del esquema del documento (Data Schema)
        Map<String, Object> comunidad = new HashMap<>();
        comunidad.put("nombre", nombre);
        comunidad.put("direccion", direccion);
        comunidad.put("admins", administradores);
        comunidad.put("miembros", miembros);
        comunidad.put("creadorId", adminId);
        comunidad.put("fechaCreacion", System.currentTimeMillis());
        comunidad.put("tablonHabilitado", true);

        // Ejecución de la operación asíncrona de inserción
        db.collection(PATH_COMUNIDADES)
                .add(comunidad)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Entidad creada con éxito en el sistema", Toast.LENGTH_LONG).show();
                    finish(); // Cierre de la actividad y retorno al stack anterior
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error en la capa de persistencia: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}