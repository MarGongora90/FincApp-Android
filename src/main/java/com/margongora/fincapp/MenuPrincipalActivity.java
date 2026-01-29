package com.margongora.fincapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

/**
 * Clase que controla la lógica del Menú Principal.
 * Gestiona el saludo, la navegación y el cierre de sesión.
 */
public class MenuPrincipalActivity extends AppCompatActivity {

    private TextView tvSaludo;
    private MaterialButton btnLogout, btnIrVotaciones, btnRecibos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Vinculamos con el XML de diseño
        setContentView(R.layout.activity_menu_principal);

        // 1. Buscamos los componentes por su ID.
        // Si sale en rojo en Android Studio, verifica que en el XML el id sea android:id="@+id/tvSaludo"
        tvSaludo = findViewById(R.id.tvSaludo);
        btnLogout = findViewById(R.id.btnLogout);
        btnIrVotaciones = findViewById(R.id.btnIrVotaciones);
        btnRecibos = findViewById(R.id.btnRecibos);

        // 2. Recogemos el nombre del usuario enviado desde MainActivity
        String nombreUsuario = getIntent().getStringExtra("USUARIO_NOMBRE");
        if (nombreUsuario != null && !nombreUsuario.isEmpty() && tvSaludo != null) {
            tvSaludo.setText("¡Hola, " + nombreUsuario + "!");
        }

        // 3. Configuración del botón para cerrar sesión (Rojo)
        if (btnLogout != null) {
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MenuPrincipalActivity.this, MainActivity.class);
                    // Limpiamos la pila de actividades para seguridad
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    Toast.makeText(MenuPrincipalActivity.this, "Has salido de la aplicación", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 4. Configuración del botón para ir a Votaciones (Verde)
        if (btnIrVotaciones != null) {
            btnIrVotaciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MenuPrincipalActivity.this, "Cargando Sistema de Votación...", Toast.LENGTH_SHORT).show();
                    // Aquí irá el Intent cuando creemos VotacionesActivity
                }
            });
        }

        // 5. Configuración del botón para consultar Recibos (Azul/Blanco)
        if (btnRecibos != null) {
            btnRecibos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MenuPrincipalActivity.this, "Abriendo Histórico de Recibos...", Toast.LENGTH_SHORT).show();
                    // Aquí irá el Intent cuando creemos RecibosActivity
                }
            });
        }
    }
}