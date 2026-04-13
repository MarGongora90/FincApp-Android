package com.margongora.fincapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Pantalla de selección para el apartado de votaciones.
 * Permite al vecino elegir entre participar en votaciones abiertas o
 * consultar los resultados de las votaciones ya cerradas.
 * * @author Maria del Mar Góngora Sarabia
 */
public class VotacionesActivity extends AppCompatActivity {
    private String idComunidadRecibido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votaciones);

        // identificador de la comunidad para saber qué datos mostrar después
        idComunidadRecibido = getIntent().getStringExtra("COMUNIDAD_ID");

        // Comprobación de seguridad por si el nombre de la clave varía
        if (idComunidadRecibido == null) {
            idComunidadRecibido = getIntent().getStringExtra("idComunidad");
        }

        // botón para ver votaciones que están activasq
        findViewById(R.id.btnVotacionesActivas).setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaVotacionesActivity.class);
            // Indicamos que solo queremos ver las que tienen estado "Activa"
            intent.putExtra("estadoFiltro", "Activa");
            intent.putExtra("COMUNIDAD_ID", idComunidadRecibido);
            startActivity(intent);
        });

        // botón para ver el histórico y resultados finales
        findViewById(R.id.btnVotacionesFinalizadas).setOnClickListener(v -> {
            Intent intent = new Intent(this, VotacionesFinalizadasActivity.class);


            intent.putExtra("VOTACION_ID", "Instalacion de Camaras");
            intent.putExtra("COMUNIDAD_ID", idComunidadRecibido);

            startActivity(intent);
        });
    }
}