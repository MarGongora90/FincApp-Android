package com.margongora.fincapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

/**
 * Clase auxiliar para la gestión de la base de datos local SQLite de FincApp.
 * Se utiliza para persistencia de datos básicos en el dispositivo.
 * * @author Mar Góngora
 */
public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    /** Nombre del archivo de la base de datos. */
    private static final String DATABASE_NAME = "fincapp.db";

    /** Versión de la base de datos. */
    private static final int DATABASE_VERSION = 3;

    public AdminSQLiteOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Define la estructura de las tablas locales para que coincidan con la lógica de Firebase.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tabla de Usuarios: Datos del perfil local
        db.execSQL("create table usuarios(" +
                "dni text primary key, " +
                "nombre text, " +
                "password text, " +
                "email text, " +
                "cuota real)");

        // Tabla de Comunidades: Almacena las comunidades a las que pertenece el usuario
        db.execSQL("create table comunidades(" +
                "id_comunidad text primary key, " +
                "nombre_comunidad text, " +
                "direccion text)");

        // Tabla de Votaciones: Registro local de temas (Copia de seguridad local)
        db.execSQL("create table votaciones(" +
                "id_votacion text primary key, " +
                "id_comunidad text, " +
                "tema text, " +
                "estado text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // En caso de actualización, eliminamos las versiones antiguas para evitar conflictos de columnas
        db.execSQL("drop table if exists usuarios");
        db.execSQL("drop table if exists comunidades");
        db.execSQL("drop table if exists votaciones");
        onCreate(db);
    }
}