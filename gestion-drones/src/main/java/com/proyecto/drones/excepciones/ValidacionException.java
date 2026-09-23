package com.proyecto.drones.excepciones;

/**
 * Indica que los datos proporcionados no cumplen las reglas del dominio.
 *
 * @since 1.0
 */
public class ValidacionException extends AplicacionException {
    private static final long serialVersionUID = 1L;

    /**
     * Crea una excepción de validación.
     *
     * @param mensaje regla de validación incumplida
     */
    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
