package com.proyecto.drones.excepciones;

/**
 * Indica un uso inválido del registro de prototipos.
 *
 * @since 1.0
 */
public class PrototipoException extends AplicacionException {
    private static final long serialVersionUID = 1L;

    /**
     * Crea una excepción relacionada con Prototype.
     *
     * @param mensaje descripción del problema
     */
    public PrototipoException(String mensaje) {
        super(mensaje);
    }
}
