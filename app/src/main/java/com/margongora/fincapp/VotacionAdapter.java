package com.margongora.fincapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Esta clase se encarga de mostrar la lista de votaciones en la pantalla.
 * Convierte los datos de cada votación en un elemento visual que el usuario.
 * * @author Maria del Mar Góngora Sarabia
 */
public class VotacionAdapter extends RecyclerView.Adapter<VotacionAdapter.ViewHolder> {

    private List<Votacion> listaVotaciones;
    private Context context;
    private String idComunidad;

    /**
     * Constructor para preparar el adaptador.
     * @param listaVotaciones La lista de votaciones que queremos mostrar.
     * @param context La pantalla donde se muestra la lista.
     * @param idComunidad El identificador de la comunidad a la que pertenecen.
     */
    public VotacionAdapter(List<Votacion> listaVotaciones, Context context, String idComunidad) {
        this.listaVotaciones = listaVotaciones;
        this.context = context;
        this.idComunidad = idComunidad;
    }

    /**
     * Crea el diseño visual para cada elemento de la lista.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_votacion, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Rellena cada elemento de la lista con la información de una votación concreta.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Votacion votacion = listaVotaciones.get(position);

        holder.tvTitulo.setText(votacion.getTitulo());
        holder.tvDescripcion.setText(votacion.getDescripcion());

        // Al pulsar sobre una votación, abrimos la pantalla para votar
        holder.itemView.setOnClickListener(v -> {
            String idVotacion = votacion.getId();

            if (idVotacion != null && idComunidad != null) {
                Intent intent = new Intent(context, VotacionesActivasActivity.class);

                // Pasamos los IDs necesarios a la siguiente pantalla para saber qué estamos votando
                intent.putExtra("idVotacion", idVotacion);
                intent.putExtra("COMUNIDAD_ID", idComunidad);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "No se han podido cargar los datos de la votación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Indica cuántos elementos hay en la lista.
     */
    @Override
    public int getItemCount() {
        return listaVotaciones.size();
    }

    /**
     * Clase interna que guarda las referencias a las vistas (textos) de cada elemento.
     * Sirve para que el adaptador funcione de forma más rápida y fluida.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloVotacion);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionVotacion);
        }
    }
}