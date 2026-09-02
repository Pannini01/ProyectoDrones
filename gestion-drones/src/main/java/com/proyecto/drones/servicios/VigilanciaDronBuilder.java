package com.proyecto.drones.servicios;

import com.proyecto.drones.excepciones.ValidacionException;
import com.proyecto.drones.modelo.Vigilancia;

/**
 * Builder concreto encargado de construir drones de {@link Vigilancia}.
 *
 * <p>Después de {@link #build()} se reinicia automáticamente y queda listo
 * para construir otro objeto.</p>
 *
 * @since 1.0
 */
public class VigilanciaDronBuilder implements DronBuilder<Vigilancia> {
    /** Producto mutable que se configura paso a paso. */
    private Vigilancia dron;

    /** Crea el Builder e inicializa el producto vacío. */
    public VigilanciaDronBuilder() {
        reset();
    }

    /** {@inheritDoc} */
    @Override
    public VigilanciaDronBuilder reset() {
        dron = new Vigilancia();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public VigilanciaDronBuilder setId(String id) { dron.setId(id); return this; }
    /** {@inheritDoc} */
    @Override
    public VigilanciaDronBuilder setSerial(String serial) { dron.setSerial(serial); return this; }
    /** {@inheritDoc} */
    @Override
    public VigilanciaDronBuilder setModelo(String modelo) { dron.setModelo(modelo); return this; }
    /** {@inheritDoc} */
    @Override
    public VigilanciaDronBuilder setFabricante(String fabricante) { dron.setFabricante(fabricante); return this; }
    /** {@inheritDoc} */
    @Override
    public VigilanciaDronBuilder setPeso(double peso) { dron.setPeso(peso); return this; }

    /**
     * Define el atributo particular del dron de vigilancia.
     *
     * @param deteccionTermica {@code true} si dispone de detección térmica
     * @return este Builder
     */
    public VigilanciaDronBuilder setDeteccionTermica(boolean deteccionTermica) {
        dron.setDeteccionTermica(deteccionTermica);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public Vigilancia build() throws ValidacionException {
        ValidadorDron.validar(dron);
        Vigilancia resultado = dron;
        reset();
        return resultado;
    }
}
