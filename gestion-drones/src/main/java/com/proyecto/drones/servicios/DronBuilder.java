package com.proyecto.drones.servicios;

import com.proyecto.drones.excepciones.ValidacionException;
import com.proyecto.drones.modelo.Agricultura;
import com.proyecto.drones.modelo.Dron;
import com.proyecto.drones.modelo.Vigilancia;

/**
 * Contrato del patrón Builder y sus implementaciones concretas para los
 * distintos subtipos de {@link Dron}.
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

    /**
     * Builder concreto para drones agrícolas.
     *
     * @since 1.0
     */
    class AgriculturaBuilder implements DronBuilder<Agricultura> {
        /** Producto mutable que se configura paso a paso. */
        private Agricultura dron;

        /** Crea el Builder e inicializa el producto vacío. */
        public AgriculturaBuilder() {
            reset();
        }

        /** {@inheritDoc} */
        @Override
        public AgriculturaBuilder reset() {
            dron = new Agricultura();
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public AgriculturaBuilder setId(String id) { dron.setId(id); return this; }

        /** {@inheritDoc} */
        @Override
        public AgriculturaBuilder setSerial(String serial) { dron.setSerial(serial); return this; }

        /** {@inheritDoc} */
        @Override
        public AgriculturaBuilder setModelo(String modelo) { dron.setModelo(modelo); return this; }

        /** {@inheritDoc} */
        @Override
        public AgriculturaBuilder setFabricante(String fabricante) { dron.setFabricante(fabricante); return this; }

        /** {@inheritDoc} */
        @Override
        public AgriculturaBuilder setPeso(double peso) { dron.setPeso(peso); return this; }

        /**
         * Define el atributo particular del dron agrícola.
         *
         * @param capacidadTanque capacidad del tanque en litros
         * @return este Builder
         */
        public AgriculturaBuilder setCapacidadTanque(double capacidadTanque) {
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

    /**
     * Builder concreto para drones de vigilancia.
     *
     * @since 1.0
     */
    class VigilanciaBuilder implements DronBuilder<Vigilancia> {
        /** Producto mutable que se configura paso a paso. */
        private Vigilancia dron;

        /** Crea el Builder e inicializa el producto vacío. */
        public VigilanciaBuilder() {
            reset();
        }

        /** {@inheritDoc} */
        @Override
        public VigilanciaBuilder reset() {
            dron = new Vigilancia();
            return this;
        }

        /** {@inheritDoc} */
        @Override
        public VigilanciaBuilder setId(String id) { dron.setId(id); return this; }

        /** {@inheritDoc} */
        @Override
        public VigilanciaBuilder setSerial(String serial) { dron.setSerial(serial); return this; }

        /** {@inheritDoc} */
        @Override
        public VigilanciaBuilder setModelo(String modelo) { dron.setModelo(modelo); return this; }

        /** {@inheritDoc} */
        @Override
        public VigilanciaBuilder setFabricante(String fabricante) { dron.setFabricante(fabricante); return this; }

        /** {@inheritDoc} */
        @Override
        public VigilanciaBuilder setPeso(double peso) { dron.setPeso(peso); return this; }

        /**
         * Define el atributo particular del dron de vigilancia.
         *
         * @param deteccionTermica {@code true} si dispone de detección térmica
         * @return este Builder
         */
        public VigilanciaBuilder setDeteccionTermica(boolean deteccionTermica) {
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
}
