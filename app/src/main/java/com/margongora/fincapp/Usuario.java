package com.margongora.fincapp;

/**
 * Clase que representa a un usuario dentro de la aplicación.
 * Se utiliza para guardar y recuperar los datos de los vecinos y administradores
 * desde la base de datos de Firebase.
 *
 * @author Maria del Mar Góngora Sarabia
 */
public class Usuario {
    private String uid;
    private String nombre;
    private String email;
    private String rol;
    private String comunidadId;

    /**
     * Constructor vacío necesario para que Firebase pueda convertir
     * los datos de la base de datos directamente en un objeto de esta clase.
     */
    public Usuario() {}

    /**
     * Constructor para crear un usuario con sus datos principales.
     * * @param uid    Identificador único de usuario.
     * @param nombre Nombre completo del usuario.
     * @param email  Correo electrónico de contacto.
     * @param rol    Tipo de usuario (administrador o propietario).
     */
    public Usuario(String uid, String nombre, String email, String rol) {
        this.uid = uid;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
    }

    // Métodos para obtener y modificar los datos de la clase (Getters y Setters)

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getComunidadId() { return comunidadId; }
    public void setComunidadId(String comunidadId) { this.comunidadId = comunidadId; }
}