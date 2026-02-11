package com.margongora.fincapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

/**
 * Actividad que muestra las noticias y avisos de la comunidad.
 * Requisito de la Tarea 2.
 */
public class TablonAnunciosActivity extends AppCompatActivity {

    private ListView lvAnuncios;
    private TextView btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablon_anuncios);

        lvAnuncios = findViewById(R.id.lvAnuncios);
        btnVolver = findViewById(R.id.btnVolverGestion);

        // Datos de ejemplo para el tablón
        ArrayList<String> listaAnuncios = new ArrayList<>();
        listaAnuncios.add("📢 Corte de agua: Martes de 10:00 a 12:00 por mantenimiento.");
        listaAnuncios.add("🧹 Limpieza de garaje: Próximo viernes, por favor retiren los vehículos.");
        listaAnuncios.add("🗳️ Recordatorio: Votación para la nueva pintura de la fachada abierta.");
        listaAnuncios.add("🎉 Fiesta de verano: Sábado 15 en la zona de la piscina.");

        // Adaptador simple para mostrar los textos
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listaAnuncios
        );

        lvAnuncios.setAdapter(adapter);

        // Botón volver
        btnVolver.setOnClickListener(v -> finish());
    }
}