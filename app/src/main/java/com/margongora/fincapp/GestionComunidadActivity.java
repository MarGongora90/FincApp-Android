package com.margongora.fincapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Actividad que controla el panel de servicios de la comunidad.
 * Corregido: Se cambia el tipo de variable a View para evitar ClassCastException.
 */
public class GestionComunidadActivity extends AppCompatActivity {

    private TextView tvNombreComunidad;
    // Usamos View en lugar de LinearLayout para que acepte tanto botones como contenedores
    private View btnInicio, btnDatos, btnRecibos, btnActas, btnAnuncios, btnVotaciones, btnContacto;
    private TextView btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_comunidad);

        // 1. Enlace de los componentes del XML
        tvNombreComunidad = findViewById(R.id.tvNombreComunidad);

        // Enlazamos como View genérica para evitar el error de casteo
        btnInicio = findViewById(R.id.btnInicio);
        btnDatos = findViewById(R.id.btnDatos);
        btnRecibos = findViewById(R.id.btnRecibos);
        btnActas = findViewById(R.id.btnActas);
        btnAnuncios = findViewById(R.id.btnAnuncios);
        btnVotaciones = findViewById(R.id.btnVotaciones);
        btnContacto = findViewById(R.id.btnContacto);
        btnVolver = findViewById(R.id.btnVolver);

        // 2. Obtener el nombre de la comunidad
        String comunidad = getIntent().getStringExtra("COMUNIDAD_NOMBRE");
        if (comunidad != null && tvNombreComunidad != null) {
            tvNombreComunidad.setText(comunidad);
        }

        // 3. Configuración de la navegación

        // Tablón de Anuncios
        if (btnAnuncios != null) {
            btnAnuncios.setOnClickListener(v -> {
                Intent intent = new Intent(this, TablonAnunciosActivity.class);
                startActivity(intent);
            });
        }

        // Mis Datos
        if (btnDatos != null) {
            btnDatos.setOnClickListener(v ->
                    Toast.makeText(this, "Módulo de Mis Datos", Toast.LENGTH_SHORT).show());
        }

        // Votaciones
        if (btnVotaciones != null) {
            btnVotaciones.setOnClickListener(v ->
                    Toast.makeText(this, "Módulo de Votaciones", Toast.LENGTH_SHORT).show());
        }

        // Mensajes informativos para módulos no desarrollados
        View.OnClickListener proximaVersion = v ->
                Toast.makeText(this, "Disponible en la próxima versión", Toast.LENGTH_SHORT).show();

        if (btnRecibos != null) btnRecibos.setOnClickListener(proximaVersion);
        if (btnActas != null) btnActas.setOnClickListener(proximaVersion);
        if (btnContacto != null) btnContacto.setOnClickListener(proximaVersion);

        // Botón Volver
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }
    }
}