package com.proyecto.drones.servicios;

/**
 * Contrato genérico del patrón Prototype.
 *
 * @param <T> tipo concreto que se puede clonar
 * @since 1.0
 */
public interface Prototipo<T> {
    /**
     * Produce una instancia independiente basada en el objeto actual.
     *
     * @return copia independiente del prototipo
     */
    T clonar();
}
