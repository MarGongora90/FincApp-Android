package com.margongora.fincapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MenuPrincipalActivity extends AppCompatActivity {

    private static final String TAG = "FincApp_Debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Intentamos cargar el diseño. Si el nombre está mal, saltará al catch.
            setContentView(R.layout.activity_menu_principal);
            Log.d(TAG, "Layout cargado correctamente");

            TextView tvSaludo = findViewById(R.id.tvSaludo);
            LinearLayout btnComunidad1 = findViewById(R.id.btnComunidad1);
            TextView btnLogout = findViewById(R.id.btnLogout);

            // Recoger nombre del usuario
            String nombreUsuario = getIntent().getStringExtra("USUARIO_NOMBRE");
            if (nombreUsuario != null && tvSaludo != null) {
                tvSaludo.setText("Bienvenido/a " + nombreUsuario);
            }

            // Configuración del botón comunidad
            if (btnComunidad1 != null) {
                btnComunidad1.setOnClickListener(v -> {
                    Intent intent = new Intent(this, GestionComunidadActivity.class);
                    startActivity(intent);
                });
            }

            // Configuración logout
            if (btnLogout != null) {
                btnLogout.setOnClickListener(v -> finish());
            }

        } catch (Exception e) {
            // Si algo falla aquí, esto evitará que la app se cierre sin avisar
            Log.e(TAG, "ERROR CRÍTICO EN MENU: " + e.getMessage());
            Toast.makeText(this, "Error al abrir el menú: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
}