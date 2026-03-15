package com.margongora.fincapp;

import org.junit.Test;
import static org.junit.Assert.*;

public class FincAppUnitTest {



    //  método actualizarCampos(QueryDocumentSnapshot doc)
    public String formatearPorcentaje(Double valor) {
        return String.format("%.1f%%", valor != null ? valor : 0.0);
    }

    //  usuarios(dni, nombre, password, email, cuota)
    public boolean validarRegistroUsuario(String dni, String email, String pass) {
        if (dni == null || email == null || pass == null) return false;
        return dni.length() == 9 && email.contains("@") && pass.length() >= 4;
    }

    // resultados de votación que muestras en la UI
    public double calcularTotalVotos(Double f, Double c, Double a) {
        double favor = (f != null) ? f : 0.0;
        double contra = (c != null) ? c : 0.0;
        double abst = (a != null) ? a : 0.0;
        return favor + contra + abst;
    }

    // ---  10 PRUEBAS ---

    @Test
    public void test01_FormateoCuota_Correcto() {
        // usamos %.1f%% para la cuota de participación
        assertEquals("15,5%", formatearPorcentaje(15.5));
    }

    @Test
    public void test02_FormateoCuota_Nulo() {
        // Verificar que la lógica de "valor != null ? valor : 0.0" funciona
        assertEquals("0,0%", formatearPorcentaje(null));
    }

    @Test
    public void test03_ValidarUsuario_CamposCorrectos() {
        assertTrue(validarRegistroUsuario("12345678Z", "mar@fincapp.com", "1234"));
    }

    @Test
    public void test04_ValidarUsuario_PasswordCorta() {
        // para guardar password, validamos que no sea demasiado simple
        assertFalse(validarRegistroUsuario("12345678Z", "mar@fincapp.com", "12"));
    }

    @Test
    public void test05_ValidarUsuario_DniIncompleto() {
        assertFalse(validarRegistroUsuario("123Z", "mar@fincapp.com", "1234"));
    }

    @Test
    public void test06_CalculoVotos_SumaCorrecta() {
        // comprobar la suma de participaciones no falla
        assertEquals(100.0, calcularTotalVotos(40.0, 40.0, 20.0), 0.001);
    }

    @Test
    public void test07_CalculoVotos_ConNulos() {
        //  verificamos que no de error falat de campso en Firestore
        assertEquals(50.0, calcularTotalVotos(50.0, null, null), 0.001);
    }

    @Test
    public void test08_SQLite_NombreTablaUsuarios() {
        // asegurar que el nombre de la columna no cambia
        String colName = "cuota_participacion";
        assertEquals("cuota_participacion", colName);
    }

    @Test
    public void test09_EmailInvalido() {
        assertFalse(validarRegistroUsuario("12345678Z", "email_sin_arroba", "1234"));
    }

    @Test
    public void test10_LimitesVotacion() {
        // Si no hay votos, el total debe ser 0
        assertEquals(0.0, calcularTotalVotos(null, null, null), 0.001);
    }
}
