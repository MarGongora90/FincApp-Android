package com.margongora.fincapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador encargado del mantenimiento de usuarios  del sistema.
 * * Implementa la lógica de gestión de identidades permitiendo la creación de perfiles,
 * la actualización de roles mediante cuadros de diálogo modales y la persistencia
 * reactiva en Cloud Firestore. Utiliza un patrón de arquitectura desacoplada para
 * la gestión de comunidades vinculadas.
 * * @author Maria del Mar Góngora Sarabia
 */
public class GestionUsariosActivity extends AppCompatActivity {

    private RecyclerView rvVecinos;
    private TextView tvTotal;
    private FirebaseFirestore db;
    private List<Map<String, Object>> listaVecinos;
    private VecinosAdapter adapter;

    private static final String PATH_USUARIOS = "artifacts/fincapp/public/data/usuarios";
    private static final String PATH_COMUNIDADES = "artifacts/fincapp/public/data/comunidades";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_usuarios);

        db = FirebaseFirestore.getInstance();
        initViews();
        cargarVecinos();
    }

    private void initViews() {
        rvVecinos = findViewById(R.id.rvVecinos);
        tvTotal = findViewById(R.id.tvTotalVecinos);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddUser);

        listaVecinos = new ArrayList<>();
        rvVecinos.setLayoutManager(new LinearLayoutManager(this));

        fabAdd.setOnClickListener(v -> cargarComunidadesYMostrarDialogo());
    }

    /**
     * Recupera los datos de usuarios desde Firestore.
     * Realiza un mapeo dinámico de los documentos a una estructura de lista de mapas,
     * inyectando el ID del documento para permitir operaciones de actualización/borrado.
     */
    private void cargarVecinos() {
        db.collection(PATH_USUARIOS).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                listaVecinos.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();
                    data.put("idDoc", document.getId());
                    listaVecinos.add(data);
                }
                adapter = new VecinosAdapter(listaVecinos);
                rvVecinos.setAdapter(adapter);
                tvTotal.setText("Total de vecinos: " + listaVecinos.size());
            } else {
                Toast.makeText(this, "Error de red al sincronizar usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Utilizado para el registro de nuevos usuarios.
     * Recupera primero las comunidades disponibles para garantizar la integridad referencial
     * mediante un selector dinámico (Spinner) en el formulario de alta.
     */
    private void cargarComunidadesYMostrarDialogo() {
        db.collection(PATH_COMUNIDADES).get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<String> nombresComunidades = new ArrayList<>();
            List<String> idsComunidades = new ArrayList<>();

            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String nombre = doc.contains("nombre") ? doc.getString("nombre") : "Comunidad sin nombre";
                nombresComunidades.add(nombre);
                idsComunidades.add(doc.getId());
            }

            if (nombresComunidades.isEmpty()) {
                Toast.makeText(this, "Acción requerida: Cree una comunidad antes de registrar vecinos", Toast.LENGTH_LONG).show();
                return;
            }

            mostrarDialogoRegistro(nombresComunidades, idsComunidades);
        });
    }

    /**
     * Construye un cuadro de diálogo para el formulario de registro.
     */
    private void mostrarDialogoRegistro(List<String> nombres, List<String> ids) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Registrar Nuevo Vecino");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 20);

        final EditText etNombre = new EditText(this);
        etNombre.setHint("Nombre y Apellidos");

        final EditText etEmail = new EditText(this);
        etEmail.setHint("Correo electrónico");

        final Spinner spComunidad = new Spinner(this);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nombres);
        spComunidad.setAdapter(spinnerAdapter);

        layout.addView(etNombre);
        layout.addView(etEmail);
        layout.addView(spComunidad);

        builder.setView(layout);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String idComunidad = ids.get(spComunidad.getSelectedItemPosition());

            if (!TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(email)) {
                ejecutarAltaUsuario(nombre, email, idComunidad);
            }
        });

        builder.setNegativeButton("Cerrar", null);
        builder.show();
    }

    private void ejecutarAltaUsuario(String nombre, String email, String idComunidad) {
        Map<String, Object> user = new HashMap<>();
        user.put("nombre", nombre);
        user.put("email", email);
        user.put("rol", "vecino");
        user.put("comunidadId", idComunidad);

        db.collection(PATH_USUARIOS).add(user).addOnSuccessListener(doc -> {
            cargarVecinos();
        });
    }

    /**
     * Realiza una actualización parcial (Update) del documento de usuario.
     * Utiliza un diálogo de selección única para modificar el atributo de rol.
     */
    private void cambiarRolUsuario(String idDoc, String rolActual) {
        final String[] roles = {"Propietario", "administrador", "presidente"};
        int seleccionado = 0;
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].equalsIgnoreCase(rolActual)) seleccionado = i;
        }

        new AlertDialog.Builder(this)
                .setTitle("Cambiar Rol del Usuario")
                .setSingleChoiceItems(roles, seleccionado, (dialog, which) -> {
                    String nuevoRol = roles[which];
                    db.collection(PATH_USUARIOS).document(idDoc)
                            .update("rol", nuevoRol)
                            .addOnSuccessListener(aVoid -> {
                                cargarVecinos();
                                dialog.dismiss();
                            });
                }).show();
    }

    /**
     * Ejecuta la eliminación física del documento en Firestore tras confirmación del usuario.
     */
    private void borrarUsuario(String idDoc) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Desea eliminar permanentemente este registro?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    db.collection(PATH_USUARIOS).document(idDoc)
                            .delete()
                            .addOnSuccessListener(aVoid -> cargarVecinos());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Adaptador interno optimizado para la representación de entidades de usuario.
     */
    private class VecinosAdapter extends RecyclerView.Adapter<VecinosAdapter.ViewHolder> {
        private List<Map<String, Object>> mData;

        VecinosAdapter(List<Map<String, Object>> data) { this.mData = data; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vecino, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, Object> item = mData.get(position);
            String docId = (String) item.get("idDoc");
            String nombre = (String) item.get("nombre");
            String email = (String) item.get("email");
            String rol = (String) item.get("rol");

            holder.tvN.setText(nombre != null ? nombre : "N/A");
            holder.tvE.setText(email != null ? email : "N/A");
            holder.tvR.setText("Rol: " + (rol != null ? rol : "Propietario"));

            holder.itemView.setOnClickListener(v -> cambiarRolUsuario(docId, rol));
            holder.btnDel.setOnClickListener(v -> borrarUsuario(docId));
        }

        @Override
        public int getItemCount() { return mData.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvN, tvE, tvR;
            ImageButton btnDel;

            ViewHolder(View v) {
                super(v);
                tvN = v.findViewById(R.id.tvNombre);
                tvE = v.findViewById(R.id.tvEmail);
                tvR = v.findViewById(R.id.tvRol);
                btnDel = v.findViewById(R.id.btnDelete);
            }
        }
    }
}