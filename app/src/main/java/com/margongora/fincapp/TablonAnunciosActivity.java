package com.margongora.fincapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Actividad que visualiza el tablón de anuncios de la comunidad.
 * <p>
 * Permite a los usuarios consultar avisos, actas y noticias relevantes.
 * </p>
 * * @author María del Mar Góngora Sarabia
 */
public class TablonAnunciosActivity extends AppCompatActivity {

    /** Lista visual de anuncios. */
    private ListView lvAnuncios;

    /** Botón para volver a la pantalla de gestión anterior. */
    private Button btnVolver;

    /** Colección de objetos de tipo Anuncio. */
    private List<Anuncio> listaAnuncios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablon_anuncios);


        lvAnuncios = findViewById(R.id.lvAnuncios);
        btnVolver = findViewById(R.id.btnVolverGestion);

        // Inicialización de datos
        cargarAnunciosSimulados();


        AnuncioAdapter adapter = new AnuncioAdapter(this, listaAnuncios);
        lvAnuncios.setAdapter(adapter);

        // Listener para cerrar la actividad
        btnVolver.setOnClickListener(v -> finish());


        lvAnuncios.setOnItemClickListener((parent, view, position, id) -> {
            Anuncio anunc = listaAnuncios.get(position);
            Toast.makeText(this, "Abriendo detalle: " + anunc.titulo, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Genera datos de prueba para la interfaz.
     */
    private void cargarAnunciosSimulados() {
        listaAnuncios = new ArrayList<>();
        listaAnuncios.add(new Anuncio("Corte de Agua Urgente", "Se realizará un corte por avería de 10:00 a 14:00.", "HOY", "URGENTE"));
        listaAnuncios.add(new Anuncio("Acta Junta General Octubre", "Ya disponible el acta de la última reunión ordinaria.", "15 Oct", "ACTA"));
        listaAnuncios.add(new Anuncio("Limpieza de Garajes", "Se ruega retirar los vehículos el próximo lunes.", "20 Oct", "AVISO"));
        listaAnuncios.add(new Anuncio("Certificado Eficiencia", "Documento técnico disponible para consulta.", "12 Oct", "DOC"));
    }

    /**
     * Clase POJO  que representa un anuncio.
     * Contiene la información necesaria para mostrar en el tablón.
     */
    class Anuncio {
        String titulo, cuerpo, fecha, tipo;

        /**
         * Constructor del modelo Anuncio.
         * @param t Título breve.
         * @param c Descripción completa del aviso.
         * @param f Fecha de publicación.
         * @param tp Categoría (determina el color del indicador).
         */
        Anuncio(String t, String c, String f, String tp) {
            this.titulo = t; this.cuerpo = c; this.fecha = f; this.tipo = tp;
        }
    }

    /**
     * Adaptador personalizado para la gestión de la vista de cada anuncio.
     */
    class AnuncioAdapter extends BaseAdapter {
        private AppCompatActivity context;
        private List<Anuncio> items;

        AnuncioAdapter(AppCompatActivity context, List<Anuncio> items) {
            this.context = context; this.items = items;
        }

        @Override public int getCount() { return items.size(); }
        @Override public Object getItem(int position) { return items.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Reutilización de la vista para optimizar memoria
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_anuncio, parent, false);
            }

            Anuncio current = items.get(position);

            // Localización de elementos en el item_anuncio.xml
            TextView titulo = convertView.findViewById(R.id.tvTituloAnuncio);
            TextView fecha = convertView.findViewById(R.id.tvFechaAnuncio);
            TextView cuerpo = convertView.findViewById(R.id.tvCuerpoAnuncio);
            View indicador = convertView.findViewById(R.id.indicatorTipo);

            // Asignación de textos
            titulo.setText(current.titulo);
            fecha.setText(current.fecha);
            cuerpo.setText(current.cuerpo);

            // Colores basada en el tipo de anuncio
            switch (current.tipo) {
                case "URGENTE":
                    indicador.setBackgroundColor(Color.RED);
                    break;
                case "ACTA":
                    indicador.setBackgroundColor(Color.BLUE);
                    break;
                case "AVISO":
                    indicador.setBackgroundColor(Color.GREEN);
                    break;
                default:
                    indicador.setBackgroundColor(Color.GRAY);
                    break;
            }

            return convertView;
        }
    }
}