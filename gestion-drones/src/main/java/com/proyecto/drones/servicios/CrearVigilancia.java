package com.proyecto.drones.servicios;

import com.proyecto.drones.modelo.Vigilancia;

/**
 * Fábrica concreta que produce drones de vigilancia vacíos.
 *
 * @since 1.0
 */
public class CrearVigilancia extends FactoryCreator {
    /**
     * {@inheritDoc}
     *
     * @return nuevo dron de vigilancia
     */
    @Override
    public Vigilancia crearDron() {
        return new Vigilancia();
    }
}
