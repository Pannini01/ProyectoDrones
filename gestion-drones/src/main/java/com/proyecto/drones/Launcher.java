package com.proyecto.drones;

/**
 * Punto de entrada alternativo para ejecutar la aplicación desde Eclipse.
 *
 * <p>Esta clase evita los problemas de detección del runtime JavaFX que pueden
 * aparecer cuando un IDE intenta ejecutar directamente una subclase de
 * {@code Application}.</p>
 *
 * @since 1.0
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Delega el inicio de la aplicación en {@link App#main(String[])}.
     *
     * @param args argumentos recibidos desde la línea de comandos
     */
    public static void main(String[] args) {
        App.main(args);
    }
}
