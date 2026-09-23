package com.proyecto.drones.servicios;

/**
 * Hoja del patron Composite. Representa un sensor concreto que no contiene
 * otros componentes.
 *
 * @since 1.2
 */
public final class SensorHoja extends ComponenteSensor {

    /**
     * @param nombre nombre del sensor concreto
     */
    public SensorHoja(String nombre) {
        super(nombre);
    }

    /** {@inheritDoc} */
    @Override
    protected void agregarRepresentacion(StringBuilder salida, int nivel) {
        agregarLinea(salida, nivel);
    }

    /** {@inheritDoc} */
    @Override
    public int contarComponentes() {
        return 1;
    }

    /** {@inheritDoc} */
    @Override
    public boolean esCompuesto() {
        return false;
    }
}
