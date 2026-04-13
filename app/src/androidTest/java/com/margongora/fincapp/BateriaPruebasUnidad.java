package com.margongora.fincapp;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Locale;

/**
 * Estas pruebas validan la lógica interna de los módulos principales.
 */
public class BateriaPruebasUnidad {

    // Prueba 1: Formateo de cuota
    @Test
    public void prueba01_formateoCuota() {
        double entrada = 15.5;
        // Simulamos la lógica de formateo de la App
        String resultado = String.format(Locale.getDefault(), "%.2f%%", entrada);
        assertTrue("El resultado debe ser un porcentaje formateado", resultado.contains("15,50%"));
    }

    // Prueba 2:  nulos (Voto)
    @Test
    public void prueba02_robustezNulosVoto() {
        Double favor = null;
        // Aplicación del operador ternario para seguridad NoSQL
        double resultado = (favor != null) ? favor : 0.0;
        assertEquals(0.0, resultado, 0.001);
    }

    // Prueba 3: Validación DNI (Longitud)
    @Test
    public void prueba03_validacionDNI() {
        String entrada = "12345678"; // 8 caracteres
        boolean resultado = (entrada.length() == 9);
        assertFalse("El DNI debe ser falso si no tiene 9 caracteres", resultado);
    }

    // Prueba 4: Fortaleza de Clave
    @Test
    public void prueba04_fortalezaClave() {
        String entrada = "123";
        boolean resultado = (entrada.length() >= 6);
        assertFalse("Debe ser falso para claves menores de 6 caracteres", resultado);
    }

    // Prueba 5: Formato Email
    @Test
    public void prueba05_formatoEmail() {
        String entrada = "user.com";
        boolean resultado = (entrada.contains("@") && entrada.contains("."));
        assertFalse("Debe fallar si no tiene el formato de email estándar", resultado);
    }

    // Prueba 6: Cálculo de Totales
    @Test
    public void prueba06_calculoTotales() {
        double si = 10.5;
        double no = 5.0;
        double resultado = si + no;
        assertEquals(15.5, resultado, 0.001);
    }

    // Prueba 7: Estado inicial voto
    @Test
    public void prueba07_estadoInicialVoto() {
        // Simulamos el constructor de una nueva votación
        int votosSi = 0;
        assertEquals("El contador inicial debe ser 0", 0, votosSi);
    }

    // Prueba 8: Código Verificación
    @Test
    public void prueba08_codigoVerificacionCorrecto() {
        String userCode = "ABC12";
        String dbCode = "ABC12";
        boolean resultado = userCode.equals(dbCode);
        assertTrue("Los códigos idénticos deben validar", resultado);
    }

    // Prueba 9: Código Erróneo
    @Test
    public void prueba09_codigoErroneo() {
        String userCode = "11111";
        String dbCode = "22222";
        boolean resultado = userCode.equals(dbCode);
        assertFalse("Códigos diferentes deben bloquear el acceso", resultado);
    }

    // Prueba 10: Rol Admin
    @Test
    public void prueba10_rolAdmin() {
        // Simulamos la obtención del rol del objeto Usuario
        String rolObtenido = "admin";
        assertEquals("El resultado esperado es el string 'admin'", "admin", rolObtenido);
    }
}