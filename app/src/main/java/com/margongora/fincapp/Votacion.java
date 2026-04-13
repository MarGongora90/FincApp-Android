package com.margongora.fincapp;

/**
 * Clase que representa un proceso de votación en una comunidad.
 * Contiene la información sobre el asunto a votar y el recuento de
 * las participaciones de los vecinos.
 * * @author Maria del Mar Góngora Sarabia
 */
public class Votacion {
    private String id;
    private String titulo;
    private String descripcion;
    private String estado;
    private int votosSi;
    private int votosNo;
    private int votosAbstencion;

    /**
     * Constructor vacío. Es obligatorio tenerlo para que Firebase pueda
     * crear objetos de este tipo automáticamente al leer los datos.
     */
    public Votacion() {}

    /**
     * Constructor para crear una nueva votación desde la aplicación.
     * Al inicio, todos los contadores de votos se establecen en cero.
     * * @param titulo      Nombre o asunto de la votación.
     * @param descripcion Detalles adicionales sobre lo que se vota.
     * @param estado      Situación actual de la votación.
     */
    public Votacion(String titulo, String descripcion, String estado) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.votosSi = 0;
        this.votosNo = 0;
        this.votosAbstencion = 0;
    }

    // Métodos para leer y modificar los datos de la votación (Getters y Setters)

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getVotosSi() { return votosSi; }
    public void setVotosSi(int votosSi) { this.votosSi = votosSi; }

    public int getVotosNo() { return votosNo; }
    public void setVotosNo(int votosNo) { this.votosNo = votosNo; }

    public int getVotosAbstencion() { return votosAbstencion; }
    public void setVotosAbstencion(int votosAbstencion) { this.votosAbstencion = votosAbstencion; }
}