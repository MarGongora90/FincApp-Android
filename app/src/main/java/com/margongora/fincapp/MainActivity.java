package com.margongora.fincapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Actividad principal que gestiona el Login de la aplicación.
 * Sincronizada con los IDs del XML: etUsuario, etPassword, btnLogin.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FincApp_Debug";
    private EditText etUsuario, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Log para saber que la actividad ha arrancado
        Log.d(TAG, "Iniciando onCreate de MainActivity");

        try {
            // 1. Inflamos el diseño
            setContentView(R.layout.activity_main);
            Log.d(TAG, "setContentView ejecutado correctamente");

            // 2. Enlazamos componentes
            etUsuario = findViewById(R.id.etUsuario);
            etPassword = findViewById(R.id.etPassword);
            btnLogin = findViewById(R.id.btnLogin);

            // 3. Verificación de seguridad
            if (btnLogin != null) {
                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        validarLogin();
                    }
                });
                Log.d(TAG, "Botón login enlazado y listo");

                // Si llegamos aquí sin errores, el problema de la pantalla negra
                // podría ser el color de fondo o el tema del dispositivo.
                Toast.makeText(this, "FincApp Lista", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "CRÍTICO: No se encontró btnLogin. Revisa activity_main.xml");
            }

        } catch (Exception e) {
            Log.e(TAG, "ERROR en onCreate: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida las credenciales del usuario consultando la base de datos SQLite.
     */
    private void validarLogin() {
        AdminSQLiteOpenHelper admin = null;
        SQLiteDatabase db = null;
        Cursor fila = null;

        try {
            admin = new AdminSQLiteOpenHelper(this);
            db = admin.getReadableDatabase();

            String user = etUsuario.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (user.length() > 0 && pass.length() > 0) {
                fila = db.rawQuery(
                        "select nombre from usuarios where dni=? and password=?",
                        new String[]{user, pass});

                if (fila.moveToFirst()) {
                    String nombreCompleto = fila.getString(0);
                    Intent intent = new Intent(MainActivity.this, MenuPrincipalActivity.class);
                    intent.putExtra("USUARIO_NOMBRE", nombreCompleto);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "DNI o contraseña incorrectos", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en validarLogin: " + e.getMessage());
            Toast.makeText(this, "Error de acceso: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (fila != null) fila.close();
            if (db != null) db.close();
        }
    }
}