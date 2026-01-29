package com.margongora.fincapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

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
}