package com.proyecto.drones.servicios;

import com.proyecto.drones.excepciones.ValidacionException;
import com.proyecto.drones.modelo.Dron;

/**
 * Contrato genérico del patrón Builder para cualquier subtipo de {@link Dron}.
 *
 * @param <T> subtipo de dron producido por el constructor
 * @since 1.0
 */
public interface DronBuilder<T extends Dron> {
    /** @return el Builder reiniciado con una instancia vacía */
    DronBuilder<T> reset();
    /**
     * @param id identificador único
     * @return este Builder
     */
    DronBuilder<T> setId(String id);
    /**
     * @param serial serial único
     * @return este Builder
     */
    DronBuilder<T> setSerial(String serial);
    /**
     * @param modelo modelo comercial
     * @return este Builder
     */
    DronBuilder<T> setModelo(String modelo);
    /**
     * @param fabricante nombre del fabricante
     * @return este Builder
     */
    DronBuilder<T> setFabricante(String fabricante);
    /**
     * @param peso peso en kilogramos
     * @return este Builder
     */
    DronBuilder<T> setPeso(double peso);
    /**
     * Valida y entrega el objeto construido.
     *
     * @return dron completamente configurado
     * @throws ValidacionException si los atributos no cumplen las reglas
     */
    T build() throws ValidacionException;
}
