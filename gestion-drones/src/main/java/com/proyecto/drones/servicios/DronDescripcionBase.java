package com.proyecto.drones.servicios;

import com.proyecto.drones.modelo.Dron;

/**
 * Primera subclase concreta de {@link DescripcionDron}.
 *
 * <p>Representa la descripcion normal del dron antes de aplicar decoradores.</p>
 *
 * @since 1.1
 */
public final class DronDescripcionBase extends DescripcionDron {

    /**
     * Crea la descripcion base.
     *
     * @param dron dron que sera descrito
     */
    public DronDescripcionBase(Dron dron) {
        super(dron);
    }

    /** {@inheritDoc} */
    @Override
    public String getDescripcion() {
        Dron dron = getDron();
        return "Dron " + dron.getSerial()
                + " | tipo: " + dron.getTipo()
                + " | modelo: " + dron.getModelo()
                + " | fabricante: " + dron.getFabricante();
    }
}
