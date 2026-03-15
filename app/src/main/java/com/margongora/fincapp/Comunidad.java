package com.margongora.fincapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de datos que representa una Comunidad de Vecinos en FincApp.
 * <p>
 * Implementa {@link Serializable} para permitir el paso de objetos entre Activities.
 *  * @author Mar Góngora
 * </p>
 */
public class Comunidad implements Serializable {

    /** Identificador único de la comunidad (UID de Firestore). */
    private String id;

    /** Nombre descriptivo de la comunidad. */
    private String nombre;

    /** Direccion física de la comunidad. */
    private String direccion;

    /** * Listado de identificadores de las votaciones asociadas a esta comunidad.
     * Se utiliza List de String para coincidir con la estructura de arrays de Firestore.
     */
    private List<String> votaciones = new ArrayList<>();

    /** * Listado de identificadores de los usuarios que pertenecen a la comunidad.
     */
    private List<String> usuarios = new ArrayList<>();

    /**
     * Constructor vacío requerido por Firebase Firestore para la
     * deserialización automática de documentos.
     */
    public Comunidad() {
    }

    // --- MÉTODOS GETTERS Y SETTERS ---

    /** @return El identificador único de la comunidad. */
    public String getId() { return id; }
    /** @param id El nuevo identificador para la comunidad. */
    public void setId(String id) { this.id = id; }

    /** @return El nombre de la comunidad. */
    public String getNombre() { return nombre; }
    /** @param nombre El nuevo nombre para la comunidad. */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** @return La dirección de la comunidad. */
    public String getDireccion() { return direccion; }
    /** @param direccion La nueva dirección de la comunidad. */
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /** @return La lista de IDs de votaciones. */
    public List<String> getVotaciones() { return votaciones; }
    /** @param votaciones El nuevo listado de ID de votaciones. */
    public void setVotaciones(List<String> votaciones) { this.votaciones = votaciones; }

    /** @return La lista de IDs de usuarios. */
    public List<String> getUsuarios() { return usuarios; }
    /** @param usuarios El nuevo listado de ID de usuarios. */
    public void setUsuarios(List<String> usuarios) { this.usuarios = usuarios; }
}