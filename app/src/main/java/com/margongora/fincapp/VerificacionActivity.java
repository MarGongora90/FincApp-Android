package com.margongora.fincapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Pantalla encargada de verificar que el usuario es real mediante un código.
 * Compara el número introducido por el usuario con el guardado en la base de datos
 * para permitirle el acceso definitivo a la aplicación.
 * * @author Maria del MAr Góngora Sarabia
 */
public class VerificacionActivity extends AppCompatActivity {
    private EditText etCodigo;
    private Button btnValidar;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificacion);

        db = FirebaseFirestore.getInstance();

        // Recuperamos el ID del usuario actual para saber a quién estamos verificando
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        etCodigo = findViewById(R.id.etCodigoVerificacion);
        btnValidar = findViewById(R.id.btnValidarCodigo);

        btnValidar.setOnClickListener(v -> validarCodigo());
    }

    /**
     * Consulta en la base de datos si el código escrito coincide con el enviado por email.
     */
    private void validarCodigo() {
        String codigoIntroducido = etCodigo.getText().toString().trim();

        if (codigoIntroducido.isEmpty()) {
            Toast.makeText(this, "Por favor, introduce el código enviado a tu correo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Accedemos al perfil del usuario en Firestore para leer su código guardado
        db.collection("artifacts/fincapp/public/data/usuarios")
                .document(userId).get().addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String codigoReal = doc.getString("codigoVerificacion");

                        if (codigoIntroducido.equals(codigoReal)) {
                            // Si los códigos coinciden, activamos la cuenta
                            activarUsuario();
                        } else {
                            Toast.makeText(this, "El código no es correcto. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No se ha podido encontrar tu perfil de usuario.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al conectar con el servidor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Actualiza el estado del usuario en la base de datos a "verificado".
     * Una vez activado, el usuario ya no tendrá que volver a pasar por esta pantalla.
     */
    private void activarUsuario() {
        db.collection("artifacts/fincapp/public/data/usuarios")
                .document(userId)
                .update("verificado", true) // Cambiamos el estado a verificado
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "¡Tu cuenta ha sido activada!", Toast.LENGTH_SHORT).show();

                    // Al terminar, enviamos al usuario al menú principal
                    Intent intent = new Intent(VerificacionActivity.this, MenuPrincipalActivity.class);

                    // Limpiamos el historial de pantallas para que no pueda volver atrás a la verificación
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Hubo un problema al activar tu cuenta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}