package com.proyecto.drones.servicios;

import com.proyecto.drones.modelo.Dron;

/**
 * Creador abstracto del patrón Factory Method.
 *
 * <p>Las subclases deciden qué subtipo de dron vacío se instancia.</p>
 *
 * @since 1.0
 */
public abstract class FactoryCreator {
    /**
     * Crea el producto definido por la fábrica concreta.
     *
     * @return nueva instancia de un subtipo de dron
     */
    public abstract Dron crearDron();
}
