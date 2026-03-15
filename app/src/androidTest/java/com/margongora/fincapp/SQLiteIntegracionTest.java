package com.margongora.fincapp;

import static org.junit.Assert.*;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Prueba de Integración para verificar la interacción entre AdminSQLiteOpenHelper 
 * y el motor de base de datos SQLite del sistema Android.
 */
@RunWith(AndroidJUnit4.class)
public class SQLiteIntegracionTest {

    private AdminSQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    @Before
    public void setUp() {
        // Inicializamos el helper usando  la aplicación de prueba
        dbHelper = new AdminSQLiteOpenHelper(InstrumentationRegistry.getInstrumentation().getTargetContext());
        db = dbHelper.getWritableDatabase();
    }

    @After
    public void tearDown() {
        // Cerramos y limpiamos para no dejar basura entre pruebas
        db.close();
        dbHelper.close();
    }

    @Test
    public void testIntegracion_InsertarYLeerUsuario() {
        // 1. Preparar datos del usuario
        ContentValues values = new ContentValues();
        values.put("dni", "12345678Z");
        values.put("nombre", "Usuario de Prueba");
        values.put("password", "pass123");
        values.put("email", "test@fincapp.com");
        values.put("cuota", 10.5);

        // 2. Ejecutar la integración: Insertar en la base de datos real
        long id = db.insert("usuarios", null, values);

        // Verificamos que se insertó correctamente (id no debe ser -1)
        assertNotEquals("La inserción falló en la base de datos", -1, id);

        // 3. Verificar persistencia: Consultar el dato recién insertado
        Cursor cursor = db.rawQuery("SELECT nombre FROM usuarios WHERE dni = ?", new String[]{"12345678Z"});

        assertTrue("El cursor debería tener resultados", cursor.moveToFirst());
        String nombreRecuperado = cursor.getString(0);

        // Comprobar que el componente DB devolvió exactamente lo que guardamos
        assertEquals("El nombre recuperado no coincide con el insertado", "Usuario de Prueba", nombreRecuperado);

        cursor.close();
    }

    @Test
    public void testIntegracion_EstructuraTablasExistente() {
        // Verifica que la tabla 'anuncios' se creó con la estructura correcta tras la inicialización
        Cursor cursor = db.rawQuery("PRAGMA table_info(anuncios)", null);

        // Debería haber varias columnas (id, titulo, contenido, fecha)
        assertTrue("La tabla anuncios no existe o no tiene columnas", cursor.getCount() > 0);
        cursor.close();
    }
}