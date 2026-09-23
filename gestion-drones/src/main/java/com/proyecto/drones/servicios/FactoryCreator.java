package com.proyecto.drones.servicios;

import com.proyecto.drones.modelo.Agricultura;
import com.proyecto.drones.modelo.Dron;
import com.proyecto.drones.modelo.Vigilancia;

/**
 * Creador abstracto y fábricas concretas del patrón Factory Method.
 *
 * <p>Las implementaciones anidadas deciden qué subtipo de dron vacío se
 * instancia, manteniendo toda la lógica del patrón en un único archivo.</p>
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

    /**
     * Fábrica concreta que produce drones agrícolas vacíos.
     *
     * @since 1.0
     */
    public static class CrearAgricultura extends FactoryCreator {
        /** {@inheritDoc} */
        @Override
        public Agricultura crearDron() {
            return new Agricultura();
        }
    }

    /**
     * Fábrica concreta que produce drones de vigilancia vacíos.
     *
     * @since 1.0
     */
    public static class CrearVigilancia extends FactoryCreator {
        /** {@inheritDoc} */
        @Override
        public Vigilancia crearDron() {
            return new Vigilancia();
        }
    }
}
