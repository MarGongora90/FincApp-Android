package com.margongora.fincapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Adaptador para el componente {@link RecyclerView} encargado de la
 * gestión de objetos {@link Comunidad}.
 * * Implementa el patrón ViewHolder para optimizar el rendimiento del scroll mediante
 * el reciclaje de vistas, minimizando las llamadas al método findViewById().
 * * @author Maria del Mar Góngora Sarabia
 */
public class ComunidadAdaptador extends RecyclerView.Adapter<ComunidadAdaptador.ViewHolder> {

    private List<Comunidad> comunidades;
    private final OnItemClickListener listener;

    /**
     * Definición de interfaz para el desacoplamiento de la lógica de clics.
     * Permite que la Activity implemente la acción de navegación.
     */
    public interface OnItemClickListener {
        /**
         * Callback disparado al detectar un evento de selección en el item.
         * @param comunidad Entidad de datos asociada a la posición seleccionada.
         */
        void onItemClick(Comunidad comunidad);
    }

    public ComunidadAdaptador(List<Comunidad> comunidades, OnItemClickListener listener) {
        this.comunidades = comunidades;
        this.listener = listener;
    }

    /**
     * Instancia el ViewHolder inflando el diseño XML (item_comunidad)
     * y definiendo el contexto de visualización.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comunidad, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Gestiona la lógica de presentación dinámica, como el conteo de colecciones anidadas.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comunidad comunidad = comunidades.get(position);

        holder.tvNombre.setText(comunidad.getNombre());

        if (comunidad.getDireccion() != null) {
            holder.tvDireccion.setText(comunidad.getDireccion());
        }

        // Lógica de presentación: Cálculo de cardinalidad de la lista de usuarios
        int count = (comunidad.getUsuarios() != null) ? comunidad.getUsuarios().size() : 0;
        holder.tvPropietariosCount.setText("Propietarios: " + count);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(comunidad);
        });
    }

    @Override
    public int getItemCount() {
        return comunidades != null ? comunidades.size() : 0;
    }

    /**
     * Actualiza el dataset del adaptador. Invoca notifyDataSetChanged() para
     * invalidar la vista actual y forzar el refresco del RecyclerView.
     * @param comunidades Nueva colección de datos.
     */
    public void setComunidades(List<Comunidad> comunidades) {
        this.comunidades = comunidades;
        notifyDataSetChanged();
    }

    /**
     * Clase interna que actúa como contenedor de referencias a las vistas hijas
     * del item, evitando redundancia en el acceso al árbol de vistas.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDireccion, tvInfoExtra, tvPropietariosCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreComunidad);
            tvDireccion = itemView.findViewById(R.id.tvDireccionComunidad);
            tvInfoExtra = itemView.findViewById(R.id.tvInfoExtra);
            tvPropietariosCount = itemView.findViewById(R.id.tvPropietariosCount);
        }
    }
}