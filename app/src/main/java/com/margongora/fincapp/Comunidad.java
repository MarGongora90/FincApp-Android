package com.margongora.fincapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de datos (POJO) que representa la entidad "Comunidad" en el sistema.
 * * Esta clase actúa como un puente de datos entre Cloud Firestore y la interfaz de usuario.
 * Implementa la interfaz {@link Serializable} para facilitar la persistencia temporal
 * y la transferencia de objetos complejos entre componentes mediante el Bundle de los Intents.
 * * @author Maria del Mar Góngora Sarabia
 */
public class Comunidad implements Serializable {

    /** Identificador único del documento en la colección de Firestore. */
    private String id;

    /** Nombre descriptivo de la comunidad de vecinos. */
    private String nombre;

    /** Ubicación física completa del inmueble gestionado. */
    private String direccion;

    /** * Colección de identificadores (UIDs) de votaciones vinculadas.
     * Se inicializa como ArrayList para prevenir NullPointerException durante
     * el proceso de binding de datos de Firebase.
     */
    private List<String> votaciones = new ArrayList<>();

    /** Colección de identificadores (UIDs) de los usuarios (propietarios) asociados. */
    private List<String> usuarios = new ArrayList<>();

    /**
     * Constructor público sin argumentos.
     * * Requisito mandatorio para que el SDK de Firebase pueda instanciar la clase
     * mediante reflexión al realizar operaciones de mapeo de documentos (toObject).
     */
    public Comunidad() {
    }

    // --- MÉTODOS DE ACCESO (ENCAPSULAMIENTO) ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /**
     * Obtiene el listado de referencias a votaciones.
     * @return List de Strings con los IDs de los documentos de votación.
     */
    public List<String> getVotaciones() { return votaciones; }
    public void setVotaciones(List<String> votaciones) { this.votaciones = votaciones; }

    public List<String> getUsuarios() { return usuarios; }
    public void setUsuarios(List<String> usuarios) { this.usuarios = usuarios; }
}