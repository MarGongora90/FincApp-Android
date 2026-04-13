package com.margongora.fincapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Pantalla donde el usuario puede ver los detalles de una votación y emitir su voto.
 * Esta clase incluye medidas de seguridad para que un vecino solo pueda votar una vez
 * y muestra los resultados actualizados en tiempo real.
 * * @author Maria del Mar Góngora Sarabia
 */
public class VotacionesActivasActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String idComunidad, currentVotacionId, userId;
    private TextView txtTitulo, txtDescripcion, txtTotalVotos;
    private Button btnVotar;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votaciones_activas);

        db = FirebaseFirestore.getInstance();
        // Obtenemos el ID del usuario actual para registrar quién vota
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        vincularVistas();

        // Recuperamos los datos que nos pasó la pantalla anterior (ListaVotaciones)
        currentVotacionId = getIntent().getStringExtra("idVotacion");
        idComunidad = getIntent().getStringExtra("COMUNIDAD_ID");

        if (currentVotacionId != null && idComunidad != null) {
            cargarDatosVotacion();
            comprobarSiYaVoto(); // Verificamos si ya ha participado para bloquear el botón
        } else {
            Toast.makeText(this, "Error al cargar la votación", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnVotar.setOnClickListener(v -> intentarVotar());
    }

    /**
     * Enlaza los elementos del diseño XML con el código Java.
     */
    private void vincularVistas() {
        txtTitulo = findViewById(R.id.txtTituloVotacion);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtTotalVotos = findViewById(R.id.txtTotalVotos);
        btnVotar = findViewById(R.id.btnVotar);
        radioGroup = findViewById(R.id.radioGroupOpciones);
    }

    /**
     * Carga el título y la descripción de la votación desde Firebase.
     * Usa un "SnapshotListener" para que si otro vecino vota, el número total
     * de votos se actualice automáticamente en esta pantalla.
     */
    private void cargarDatosVotacion() {
        db.collection("artifacts/fincapp/public/data/comunidades")
                .document(idComunidad)
                .collection("votaciones")
                .document(currentVotacionId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e("VOTACION", "Error de red: " + e.getMessage());
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        txtTitulo.setText(snapshot.getString("titulo"));
                        txtDescripcion.setText(snapshot.getString("descripcion"));

                        // Sumamos los votos de cada opción para mostrar el total acumulado
                        long si = snapshot.contains("votosSi") ? snapshot.getLong("votosSi") : 0;
                        long no = snapshot.contains("votosNo") ? snapshot.getLong("votosNo") : 0;
                        long abs = snapshot.contains("votosAbstencion") ? snapshot.getLong("votosAbstencion") : 0;

                        txtTotalVotos.setText("Votos realizados hasta ahora: " + (si + no + abs));
                    }
                });
    }

    /**
     * Revisa qué opción ha marcado el usuario y lanza el proceso de guardado.
     */
    private void intentarVotar() {
        int selectedId = radioGroup.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this, "Debes seleccionar una opción para votar", Toast.LENGTH_SHORT).show();
            return;
        }

        String textoOpcion = "";
        String campoContador = "";

        // Identificamos la opción elegida
        if (selectedId == R.id.rbSi) {
            textoOpcion = "A Favor";
            campoContador = "votosSi";
        } else if (selectedId == R.id.rbNo) {
            textoOpcion = "En Contra";
            campoContador = "votosNo";
        } else if (selectedId == R.id.rbAbstencion) {
            textoOpcion = "Abstención";
            campoContador = "votosAbstencion";
        }

        enviarVotoAFirebase(textoOpcion, campoContador);
    }

    /**
     * Registra el voto en la base de datos.
     * Primero crea un documento con el ID del usuario para impedir que vote dos veces
     * y luego suma +1 al contador general de la opción elegida.
     */
    private void enviarVotoAFirebase(String textoOpcion, String campoContador) {
        DocumentReference votacionRef = db.collection("artifacts/fincapp/public/data/comunidades")
                .document(idComunidad)
                .collection("votaciones")
                .document(currentVotacionId);

        Map<String, Object> registroVoto = new HashMap<>();
        registroVoto.put("usuarioId", userId);
        registroVoto.put("opcion", textoOpcion);
        registroVoto.put("fecha", FieldValue.serverTimestamp()); // Hora oficial del servidor

        // Guardamos el voto individual usando el UID del usuario como nombre del documento
        votacionRef.collection("votos").document(userId).set(registroVoto)
                .addOnSuccessListener(aVoid -> {
                    // Si se guarda bien, incrementamos el contador de la votación
                    votacionRef.update(campoContador, FieldValue.increment(1))
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(this, "Voto registrado. ¡Gracias por participar!", Toast.LENGTH_SHORT).show();
                                finish(); // Cerramos la pantalla al terminar
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "No se pudo registrar el voto. Inténtalo más tarde.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Busca si ya existe un voto con el ID de este usuario.
     * Si lo encuentra, bloquea el botón y las opciones para que no pueda participar de nuevo.
     */
    private void comprobarSiYaVoto() {
        db.collection("artifacts/fincapp/public/data/comunidades")
                .document(idComunidad)
                .collection("votaciones")
                .document(currentVotacionId)
                .collection("votos")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // El usuario ya participó: bloqueamos la interfaz
                        btnVotar.setEnabled(false);
                        btnVotar.setText("YA HAS VOTADO EN ESTA SESIÓN");
                        btnVotar.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));

                        findViewById(R.id.rbSi).setEnabled(false);
                        findViewById(R.id.rbNo).setEnabled(false);
                        findViewById(R.id.rbAbstencion).setEnabled(false);
                    }
                });
    }
}