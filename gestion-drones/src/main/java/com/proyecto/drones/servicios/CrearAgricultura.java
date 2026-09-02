package com.proyecto.drones.servicios;

import com.proyecto.drones.modelo.Agricultura;

/**
 * Fábrica concreta que produce drones agrícolas vacíos.
 *
 * @since 1.0
 */
public class CrearAgricultura extends FactoryCreator {
    /**
     * {@inheritDoc}
     *
     * @return nuevo dron agrícola
     */
    @Override
    public Agricultura crearDron() {
        return new Agricultura();
    }
}
