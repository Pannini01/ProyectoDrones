package com.proyecto.drones.modelo;

/**
 * Tipos de dron admitidos por el sistema y por la tabla PostgreSQL.
 *
 * @since 1.0
 */
public enum TipoDron {
    /** Dron destinado a labores agrícolas. */
    AGRICULTURA("Agricultura"),
    /** Dron destinado a labores de vigilancia. */
    VIGILANCIA("Vigilancia");

    /** Nombre legible usado por la vista. */
    private final String etiqueta;

    /**
     * Asocia un valor interno con su etiqueta visible.
     *
     * @param etiqueta nombre que se presentará al usuario
     */
    TipoDron(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /**
     * Obtiene el nombre legible que se presenta en la interfaz.
     *
     * @return etiqueta visible del tipo
     */
    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    /**
     * Devuelve la etiqueta legible del tipo.
     *
     * @return nombre mostrado en controles JavaFX
     */
    public String toString() {
        return etiqueta;
    }
}
