package com.proyecto.drones.excepciones;

/**
 * Excepción base para los errores controlados de la aplicación.
 *
 * <p>Permite que el controlador gestione de forma uniforme los errores y los
 * presente en la vista sin imprimirlos en la consola.</p>
 *
 * @since 1.0
 */
public class AplicacionException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Crea una excepción con un mensaje apto para mostrar al usuario.
     *
     * @param mensaje descripción del error
     */
    public AplicacionException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea una excepción con mensaje y causa técnica original.
     *
     * @param mensaje descripción del error
     * @param causa excepción que originó el problema
     */
    public AplicacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
