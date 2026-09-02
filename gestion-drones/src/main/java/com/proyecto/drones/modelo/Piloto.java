package com.proyecto.drones.modelo;

import java.util.Objects;

/**
 * Representa al piloto responsable de operar un dron.
 *
 * @since 1.0
 */
public class Piloto {
    /** Identificador único del piloto. */
    private String id;
    /** Nombre completo. */
    private String nombre;
    /** Número de licencia. */
    private String licencia;
    /** Teléfono de contacto. */
    private String telefono;

    /** Crea un piloto vacío para su posterior configuración. */
    public Piloto() {
    }

    /**
     * Crea un piloto con todos sus datos.
     *
     * @param id identificador único
     * @param nombre nombre completo
     * @param licencia número de licencia
     * @param telefono teléfono de contacto
     */
    public Piloto(String id, String nombre, String licencia, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.licencia = licencia;
        this.telefono = telefono;
    }

    /**
     * Crea una copia independiente de otro piloto.
     *
     * @param otro piloto que se copiará
     */
    public Piloto(Piloto otro) {
        this(otro.id, otro.nombre, otro.licencia, otro.telefono);
    }

    /** @return identificador único del piloto */
    public String getId() { return id; }
    /** @param id identificador único del piloto */
    public void setId(String id) { this.id = id; }
    /** @return nombre completo del piloto */
    public String getNombre() { return nombre; }
    /** @param nombre nombre completo del piloto */
    public void setNombre(String nombre) { this.nombre = nombre; }
    /** @return número de licencia del piloto */
    public String getLicencia() { return licencia; }
    /** @param licencia número de licencia del piloto */
    public void setLicencia(String licencia) { this.licencia = licencia; }
    /** @return teléfono de contacto */
    public String getTelefono() { return telefono; }
    /** @param telefono teléfono de contacto */
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    /**
     * Compara pilotos mediante su identificador.
     *
     * @param obj objeto que se comparará
     * @return {@code true} si ambos pilotos tienen el mismo identificador
     */
    public boolean equals(Object obj) {
        return obj instanceof Piloto otro && Objects.equals(id, otro.id);
    }

    @Override
    /** @return código hash calculado a partir del identificador */
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
