package com.margongora.fincapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Actividad para visualizar y editar el perfil del usuario.
 * Gestiona el bloqueo de campos y la actualización en Firestore.
 ** @author Maria del Mar Góngora Sarabia
 */
public class DatosPerfilActivity extends AppCompatActivity {

    private static final String TAG = "DatosPerfilActivity";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private EditText etNombre, etDni, etDireccion, etEmail, etTelefono, etCuenta, etCuota;
    private Button btnVolver, btnGuardar, btnEditar, btnCerrarSesion;

    private boolean modoEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_perfil);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        vincularVistas();
        configurarBotones();

        // Por defecto los campos están bloqueados hasta pulsar "Editar"
        establecerEstadoEdicion(false);

        cargarDatosDesdeFirestore();
    }

    private void vincularVistas() {
        etNombre = findViewById(R.id.etNombrePerfil);
        etDni = findViewById(R.id.etDNIPerfil);
        etDireccion = findViewById(R.id.etDireccionPerfil);
        etEmail = findViewById(R.id.etEmailPerfil);
        etTelefono = findViewById(R.id.etTelefonoPerfil);
        etCuenta = findViewById(R.id.etCuentaPerfil);
        etCuota = findViewById(R.id.etCuotaPerfil);

        btnGuardar = findViewById(R.id.btnGuardarPerfil);
        btnEditar = findViewById(R.id.btnEditarPerfil);
        btnVolver = findViewById(R.id.btnVolverPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionPerfil);
    }

    private void configurarBotones() {
        // Botón EDITAR: Desbloquea los campos
        btnEditar.setOnClickListener(v -> establecerEstadoEdicion(true));

        // Botón GUARDAR: Envía los datos a Firestore
        btnGuardar.setOnClickListener(v -> actualizarDatosEnFirestore());

        // Botón VOLVER: Cierra la actividad
        btnVolver.setOnClickListener(v -> finish());

        // Botón CERRAR SESIÓN
        btnCerrarSesion.setOnClickListener(v -> {
            mAuth.signOut();
            finish();
        });
    }

    /**
     * Habilita o deshabilita los EditText según el estado de edición.
     */
    private void establecerEstadoEdicion(boolean habilitar) {
        modoEdicion = habilitar;
        EditText[] campos = {etNombre, etDni, etDireccion, etEmail, etTelefono, etCuenta, etCuota};
        for (EditText campo : campos) {
            campo.setEnabled(habilitar);
            campo.setFocusable(habilitar);
            campo.setFocusableInTouchMode(habilitar);
        }


        btnGuardar.setVisibility(habilitar ? View.VISIBLE : View.GONE);
        btnEditar.setVisibility(habilitar ? View.GONE : View.VISIBLE);
    }

    private void cargarDatosDesdeFirestore() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        DocumentReference docRef = db.collection("artifacts")
                .document("fincapp")
                .collection("public")
                .document("data")
                .collection("usuarios")
                .document(user.getUid());

        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Error al escuchar cambios", e);
                return;
            }

            // actualizamos los campos si NO estamos editando
            if (!modoEdicion && snapshot != null && snapshot.exists()) {
                etNombre.setText(snapshot.getString("nombre"));
                etDni.setText(snapshot.getString("dni"));
                etDireccion.setText(snapshot.getString("direccion"));
                etEmail.setText(snapshot.getString("email"));
                etTelefono.setText(snapshot.getString("telefono"));
                etCuenta.setText(snapshot.getString("cuenta_bancaria"));

                Object cuota = snapshot.get("cuota");
                etCuota.setText(cuota != null ? cuota.toString() : "");
            }
        });
    }

    private void actualizarDatosEnFirestore() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        Map<String, Object> data = new HashMap<>();
        data.put("nombre", etNombre.getText().toString().trim());
        data.put("dni", etDni.getText().toString().trim());
        data.put("direccion", etDireccion.getText().toString().trim());
        data.put("email", etEmail.getText().toString().trim());
        data.put("telefono", etTelefono.getText().toString().trim());
        data.put("cuenta_bancaria", etCuenta.getText().toString().trim());

        // Conversión segura de cuota a número
        try {
            String cuotaStr = etCuota.getText().toString().trim();
            if (!cuotaStr.isEmpty()) {
                data.put("cuota", Double.parseDouble(cuotaStr));
            }
        } catch (Exception e) {
            data.put("cuota", etCuota.getText().toString().trim());
        }

        db.collection("artifacts")
                .document("fincapp")
                .collection("public")
                .document("data")
                .collection("usuarios")
                .document(user.getUid())
                .update(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    establecerEstadoEdicion(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}