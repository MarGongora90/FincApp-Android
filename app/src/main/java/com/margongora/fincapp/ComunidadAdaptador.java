package com.margongora.fincapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Adaptador para el RecyclerView que gestiona la visualización de las comunidades.
 * <p>
 * Se encarga de inflar el diseño individual de cada comunidad y de vincular los datos
 * del modelo {@link Comunidad} con los componentes del XML.
 * </p>

 */
public class ComunidadAdaptador extends RecyclerView.Adapter<ComunidadAdaptador.ViewHolder> {

    private List<Comunidad> comunidades;
    private OnItemClickListener listener;

    /**
     * Interfaz para gestionar los eventos de clic en los elementos de la lista.
     */
    public interface OnItemClickListener {
        /**
         * Cuando el usuario pulsa sobre una comunidad.
         * @param comunidad Objeto comunidad de la posición pulsada.
         */
        void onItemClick(Comunidad comunidad);
    }

    /**
     * Constructor del adaptador.
     * @param comunidades Lista inicial de comunidades a mostrar.
     * @param listener Implementación de la interfaz para manejar eventos de clic.
     */
    public ComunidadAdaptador(List<Comunidad> comunidades, OnItemClickListener listener) {
        this.comunidades = comunidades;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comunidad, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comunidad comunidad = comunidades.get(position);

        //Datos básicos
        holder.tvNombre.setText(comunidad.getNombre());

        if (comunidad.getDireccion() != null) {
            holder.tvDireccion.setText(comunidad.getDireccion());
        }

        /**
         * Cálculo dinámico del número de propietarios.
         */
        int count = (comunidad.getUsuarios() != null) ? comunidad.getUsuarios().size() : 0;
        holder.tvPropietariosCount.setText("Propietarios: " + count);

        // Configuración del listener de clic para el elemento completo
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(comunidad);
        });
    }

    @Override
    public int getItemCount() {
        return comunidades != null ? comunidades.size() : 0;
    }

    /**
     * Actualiza la lista de datos  y notifica los cambios al RecyclerView.
     * @param comunidades Nueva lista de comunidades.
     */
    public void setComunidades(List<Comunidad> comunidades) {
        this.comunidades = comunidades;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvDireccion;
        TextView tvInfoExtra;
        /** TextView que muestra el total de propietarios. */
        TextView tvPropietariosCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvNombreComunidad);
            tvDireccion = itemView.findViewById(R.id.tvDireccionComunidad);
            tvInfoExtra = itemView.findViewById(R.id.tvInfoExtra);
            tvPropietariosCount = itemView.findViewById(R.id.tvPropietariosCount);
        }
    }
}