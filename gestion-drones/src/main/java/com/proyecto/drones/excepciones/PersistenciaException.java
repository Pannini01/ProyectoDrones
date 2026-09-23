package com.proyecto.drones.excepciones;

/**
 * Representa errores de configuración, conexión o ejecución SQL.
 *
 * @since 1.0
 */
public class PersistenciaException extends AplicacionException {
    private static final long serialVersionUID = 1L;

    /**
     * Crea una excepción de persistencia sin causa asociada.
     *
     * @param mensaje descripción del problema
     */
    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea una excepción de persistencia conservando la causa técnica.
     *
     * @param mensaje descripción del problema
     * @param causa excepción original de configuración o JDBC
     */
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
