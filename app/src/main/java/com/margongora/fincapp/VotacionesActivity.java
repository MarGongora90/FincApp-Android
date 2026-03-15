package com.margongora.fincapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Actividad que actúa como panel principal de la sección de votaciones.
 * <p>
 * Esta clase sirve para que el usuario decida si desea consultar
 * las votaciones que se encuentran actualmente en curso o revisar el histórico de
 * votaciones ya concluidas en su comunidad de vecinos.
 * </p>
 * * <b>Responsabilidades principales:</b>
 * <ul>
 * <li>Gestionar la navegación hacia las listas de votaciones activas y finalizadas.</li>
 * <li>Asegurar la persistencia del {@code comunidadId} a lo largo del flujo de navegación.</li>
 * <li>Controlar la integridad de los datos de entrada necesarios para la sesión.</li>
 * </ul>
 * * @author Mar Góngora
 */
public class VotacionesActivity extends AppCompatActivity {

    /** Identificador único de la comunidad seleccionada */
    private String comunidadId;

    /**
     * Inicializa la interfaz de usuario y recupera los parámetros de navegación.
     * <p>
     * Se encarga de validar que la actividad haya recibido un ID de comunidad válido.
     * Si el ID es nulo, la actividad se cierra para prevenir errores de referencia nula
     * en las consultas a la base de datos de actividades posteriores.
     * </p>
     * * @param savedInstanceState Bundle que contiene el estado previamente guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votaciones);

        // Recuperación del ID de la comunidad mediante el Intent de origen
        comunidadId = getIntent().getStringExtra("COMUNIDAD_ID");

        // Validación de seguridad: Previene fallos en cascada si no hay comunidad seleccionada
        if (comunidadId == null) {
            Toast.makeText(this, "Error: No se seleccionó una comunidad", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        View btnActivas = findViewById(R.id.btnActivas);
        View btnFinalizadas = findViewById(R.id.btnFinalizadas);
        TextView txtVolver = findViewById(R.id.txtVolver);

        // Configuración de eventos de navegación
        if (btnActivas != null) {
            btnActivas.setOnClickListener(v -> {
                Intent intent = new Intent(VotacionesActivity.this, VotacionesActivasActivity.class);

                // Transferencia de datos mediante Extras usando constantes de cadena consistentes
                intent.putExtra("EXTRA_COMUNIDAD_ID", comunidadId);

                /* *
                 * Actualmente se utiliza un ID estático ("videoportero") para demostración.
                 * En futuras versiones, este botón debera mpstrar una lista de votaciones
                 * o recibir dinámicamente el ID seleccionado.
                 */
                intent.putExtra("EXTRA_VOTACION_ID", "videoportero");
                startActivity(intent);
            });
        }

        if (btnFinalizadas != null) {
            btnFinalizadas.setOnClickListener(v -> {
                Intent intent = new Intent(VotacionesActivity.this, VotacionesFinalizadasActivity.class);
                intent.putExtra("EXTRA_COMUNIDAD_ID", comunidadId);
                startActivity(intent);
            });
        }

        // Finaliza la actividad actual para regresar a la pantalla anterior
        if (txtVolver != null) {
            txtVolver.setOnClickListener(v -> finish());
        }
    }
}