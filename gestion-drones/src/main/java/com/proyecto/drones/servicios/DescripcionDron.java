package com.proyecto.drones.servicios;

import java.util.Objects;

import com.proyecto.drones.modelo.Dron;

/**
 * Componente abstracto del patron Decorator para describir un dron.
 *
 * <p>El proyecto mantiene exactamente dos subclases concretas:
 * {@link DronDescripcionBase} y {@link BateriaAdicionalDecorator}. De esta
 * forma la jerarquia es explicita sin modificar la entidad {@link Dron}.</p>
 *
 * @since 1.1
 */
public abstract class DescripcionDron {
    /** Dron original al cual pertenece la descripcion. */
    private final Dron dron;

    /**
     * Inicializa el componente Decorator.
     *
     * @param dron dron descrito
     */
    protected DescripcionDron(Dron dron) {
        this.dron = Objects.requireNonNull(dron, "El dron no puede ser nulo.");
    }

    /**
     * @return dron original, sin modificarlo
     */
    public final Dron getDron() {
        return dron;
    }

    /**
     * @return descripcion acumulada del componente y sus decoradores
     */
    public abstract String getDescripcion();
}
