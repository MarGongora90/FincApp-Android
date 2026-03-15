package com.margongora.fincapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Actividad que permite al usuario enviar mensajes al administrador.

 */
public class ContactoActivity extends AppCompatActivity {

    /** Botón para cerrar la actividad y regresar a la pantalla anterior. */
    private Button btnVolver;

    /** Botón para procesar el envío del mensaje redactado. */
    private Button btnEnviar;

    /** Campo de texto donde el usuario escribe el contenido del mensaje. */
    private EditText etMensaje;

    /**
     * Inicializa la actividad, configura el diseño y vincula los componentes de la interfaz.
     * * @param savedInstanceState Si la actividad se reinicia, este bundle contiene los datos más recientes.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_contacto);

        // Inicialización de componentes vinculados al layout XML
        btnVolver = findViewById(R.id.btnVolverContacto);
        btnEnviar = findViewById(R.id.btnEnviarContacto);
        etMensaje = findViewById(R.id.etMensajeContacto);

        /**
         * Configura el evento de clic para el botón volver.
         */
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /**
         * Configura el evento de clic para el botón enviar.
         */
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = etMensaje.getText().toString().trim();

                if (!mensaje.isEmpty()) {
                    // Simulación de envío
                    mostrarConfirmacionEnvio();
                    limpiarFormulario();
                } else {
                    // Notificación de error por campo vacío
                    Toast.makeText(ContactoActivity.this,
                            "Por favor, escribe un mensaje", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Muestra un mensaje confirmando que el envío ha sido procesado.
     */
    private void mostrarConfirmacionEnvio() {
        Toast.makeText(ContactoActivity.this,
                "Mensaje enviado al administrador", Toast.LENGTH_SHORT).show();
    }

    /**
     * Restablece los campos de entrada de la interfaz de usuario.
     */
    private void limpiarFormulario() {
        if (etMensaje != null) {
            etMensaje.setText("");
        }
    }
}