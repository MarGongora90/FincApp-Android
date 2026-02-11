package com.margongora.fincapp;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Esta clase DEBE estar dentro del paquete com.margongora.fincapp
 * para que el sistema pueda encontrarla.
 */
public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fincapp.db";
    private static final int DATABASE_VERSION = 1;

    public AdminSQLiteOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabla de usuarios
        db.execSQL("create table usuarios(" +
                "dni text primary key," +
                "nombre text," +
                "password text)");

        // Usuario de prueba: DNI 12345678A y clave 1234
        db.execSQL("insert into usuarios values('12345678A', 'Usuario de Prueba', '1234')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Esta clase controla la pantalla donde el usuario elige su comunidad.
     * Es el paso intermedio entre el Login y la Gestión de la Comunidad.
     */
    public static class MenuPrincipalActivity extends AppCompatActivity {

        private TextView tvSaludo;
        private LinearLayout btnComunidad1, btnComunidad2;
        private TextView btnLogout;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_menu_principal);

            // Enlazamos los componentes del XML
            tvSaludo = findViewById(R.id.tvSaludo);
            btnComunidad1 = findViewById(R.id.btnComunidad1);
            btnComunidad2 = findViewById(R.id.btnComunidad2);
            btnLogout = findViewById(R.id.btnLogout);

            // Recibimos el nombre del usuario desde el Login (MainActivity)
            String nombreUsuario = getIntent().getStringExtra("USUARIO_NOMBRE");
            if (nombreUsuario != null) {
                tvSaludo.setText("Bienvenido/a " + nombreUsuario);
            }

            // --- CONFIGURACIÓN DE CLICS ---

            // Al pulsar en la primera comunidad (Edificio La Paz)
            btnComunidad1.setOnClickListener(v -> {
                Intent intent = new Intent(MenuPrincipalActivity.this, GestionComunidadActivity.class);
                intent.putExtra("COMUNIDAD_NOMBRE", "Edificio La Paz");
                startActivity(intent);
            });

            // Al pulsar en la segunda comunidad
            btnComunidad2.setOnClickListener(v -> {
                Intent intent = new Intent(MenuPrincipalActivity.this, GestionComunidadActivity.class);
                intent.putExtra("COMUNIDAD_NOMBRE", "Edificio María Eugenia");
                startActivity(intent);
            });

            // Cerrar sesión
            btnLogout.setOnClickListener(v -> {
                finish(); // Cierra esta pantalla y vuelve al Login
            });
        }
    }
}