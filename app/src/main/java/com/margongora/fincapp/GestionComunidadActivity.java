package com.margongora.fincapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Actividad principal del panel de una comunidad.
 * <p>
 * Menú principal tras seleccionar una comunidad.
 * Gestiona la navegación hacia los diferentes módulos (Votaciones, Anuncios, Perfil)
 * y controla la visualización de una guía de usuario única.
 *  * @author Mar Góngora
 * </p>
 */
public class GestionComunidadActivity extends AppCompatActivity {

    /** TextView que muestra el nombre de la comunidad. */
    private TextView tvNombreComunidad;

    /** Vistas que actúan como botones de acceso a las distintas secciones. */
    private View btnInicio, btnDatos, btnRecibos, btnActas, btnAnuncios, btnVotaciones, btnContacto;

    /** TextView con funcionalidad de botón para finalizar la actividad. */
    private TextView btnVolver;

    /** Identificador único de la comunidad en la base de datos Firestore. */
    private String comunidadId;

    /** Nombre legible de la comunidad para mostrar en la cabecera. */
    private String comunidadNombre;

    /** Muestra la ayuda/guía al usuario. */
    private LinearLayout layoutGuiaMenu;

    /** Botón para confirmar la lectura de la guía y ocultarla. */
    private Button btnEntendidoMenu;


    private static final String PREFS_NAME = "FincAppPrefs";

    /** Almacena si el usuario ya ha visualizado la guía de bienvenida. */
    private static final String KEY_GUIA_MENU_VISTA = "guiaMenuPrincipalVista";

    /**
     * Método de ciclo de vida donde se inicializa la actividad.
     * * @param savedInstanceState Bundle con el estado guardado.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_comunidad);


        comunidadId = getIntent().getStringExtra("COMUNIDAD_ID");
        comunidadNombre = getIntent().getStringExtra("COMUNIDAD_NOMBRE");

        vincularVistas();
        configurarNavegacion();
        comprobarGuiaBienvenida();
    }

    /**
     * Obtiene las referencias de los componentes visuales definidos en el layout.
     */
    private void vincularVistas() {
        tvNombreComunidad = findViewById(R.id.tvNombreComunidad);
        btnInicio = findViewById(R.id.btnInicio);
        btnDatos = findViewById(R.id.btnDatos);
        btnRecibos = findViewById(R.id.btnRecibos);
        btnActas = findViewById(R.id.btnActas);
        btnAnuncios = findViewById(R.id.btnAnuncios);
        btnVotaciones = findViewById(R.id.btnVotaciones);
        btnContacto = findViewById(R.id.btnContacto);
        btnVolver = findViewById(R.id.btnVolver);

        layoutGuiaMenu = findViewById(R.id.layoutGuiaMenu);
        btnEntendidoMenu = findViewById(R.id.btnEntendidoMenu);
    }

    /**
     * Configura los escuchadores de clics para la navegación.
     */
    private void configurarNavegacion() {
        if (comunidadNombre != null && tvNombreComunidad != null) {
            tvNombreComunidad.setText(comunidadNombre);
        }

        // Navegación al módulo de Votaciones.
        if (btnVotaciones != null) {
            btnVotaciones.setOnClickListener(v -> {
                Intent intent = new Intent(this, VotacionesActivity.class);
                intent.putExtra("COMUNIDAD_ID", comunidadId);
                startActivity(intent);
            });
        }

        // Navegación al Tablón de Anuncios.
        if (btnAnuncios != null) {
            btnAnuncios.setOnClickListener(v -> {
                Intent intent = new Intent(this, TablonAnunciosActivity.class);
                intent.putExtra("COMUNIDAD_ID", comunidadId);
                startActivity(intent);
            });
        }

        // Navegación a perfiles y contactos.
        if (btnDatos != null) {
            btnDatos.setOnClickListener(v -> startActivity(new Intent(this, DatosPerfilActivity.class)));
        }

        if (btnContacto != null) {
            btnContacto.setOnClickListener(v -> startActivity(new Intent(this, ContactoActivity.class)));
        }

        // funciones pendientes de implementación
        View.OnClickListener proximaVersion = v ->
                Toast.makeText(this, "Funcionalidad en desarrollo para próximas versiones", Toast.LENGTH_SHORT).show();

        if (btnRecibos != null) btnRecibos.setOnClickListener(proximaVersion);
        if (btnActas != null) btnActas.setOnClickListener(proximaVersion);

        // Botón para retroceder
        if (btnVolver != null) btnVolver.setOnClickListener(v -> finish());

        // Lógica para cerrar la guía de ayuda
        if (btnEntendidoMenu != null) {
            btnEntendidoMenu.setOnClickListener(v -> {
                layoutGuiaMenu.setVisibility(View.GONE);
                marcarGuiaComoVista();
            });
        }
    }

    /**
     * Verifica en {@link SharedPreferences} si es la primera vez que el usuario accede
     * a este menú para mostrar u ocultar la guía visual.
     */
    private void comprobarGuiaBienvenida() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean yaVista = prefs.getBoolean(KEY_GUIA_MENU_VISTA, false);
        if (!yaVista && layoutGuiaMenu != null) {
            layoutGuiaMenu.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Registra de forma persistente que el usuario ha visto la guía del menú,
     * evitando que aparezca en futuros inicios de la actividad.
     */
    private void marcarGuiaComoVista() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_GUIA_MENU_VISTA, true).apply();
    }
}