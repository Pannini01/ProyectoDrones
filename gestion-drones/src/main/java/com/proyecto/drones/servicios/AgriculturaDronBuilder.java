package com.proyecto.drones.servicios;

import com.proyecto.drones.excepciones.ValidacionException;
import com.proyecto.drones.modelo.Agricultura;

/**
 * Builder concreto encargado de construir drones de {@link Agricultura}.
 *
 * <p>Después de {@link #build()} se reinicia automáticamente y queda listo
 * para construir otro objeto.</p>
 *
 * @since 1.0
 */
public class AgriculturaDronBuilder implements DronBuilder<Agricultura> {
    /** Producto mutable que se configura paso a paso. */
    private Agricultura dron;

    /** Crea el Builder e inicializa el producto vacío. */
    public AgriculturaDronBuilder() {
        reset();
    }

    /** {@inheritDoc} */
    @Override
    public AgriculturaDronBuilder reset() {
        dron = new Agricultura();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public AgriculturaDronBuilder setId(String id) { dron.setId(id); return this; }
    /** {@inheritDoc} */
    @Override
    public AgriculturaDronBuilder setSerial(String serial) { dron.setSerial(serial); return this; }
    /** {@inheritDoc} */
    @Override
    public AgriculturaDronBuilder setModelo(String modelo) { dron.setModelo(modelo); return this; }
    /** {@inheritDoc} */
    @Override
    public AgriculturaDronBuilder setFabricante(String fabricante) { dron.setFabricante(fabricante); return this; }
    /** {@inheritDoc} */
    @Override
    public AgriculturaDronBuilder setPeso(double peso) { dron.setPeso(peso); return this; }

    /**
     * Define el atributo particular del dron agrícola.
     *
     * @param capacidadTanque capacidad del tanque en litros
     * @return este Builder
     */
    public AgriculturaDronBuilder setCapacidadTanque(double capacidadTanque) {
        dron.setCapacidadTanque(capacidadTanque);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public Agricultura build() throws ValidacionException {
        ValidadorDron.validar(dron);
        Agricultura resultado = dron;
        reset();
        return resultado;
    }
}
