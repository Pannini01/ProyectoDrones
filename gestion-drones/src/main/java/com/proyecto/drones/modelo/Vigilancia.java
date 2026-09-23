package com.proyecto.drones.modelo;

/**
 * Dron especializado en vigilancia y deteccion termica.
 *
 * @since 1.0
 */
public class Vigilancia extends Dron {
    /** Indica si el equipo incorpora deteccion termica. */
    private boolean deteccionTermica;

    /** Crea un dron de vigilancia vacio para utilizarlo con Builder. */
    public Vigilancia() {
    }

    /**
     * Crea un dron de vigilancia completamente configurado.
     *
     * @param id identificador unico
     * @param serial serial unico
     * @param modelo modelo comercial
     * @param fabricante fabricante
     * @param peso peso en kilogramos
     * @param deteccionTermica indica si dispone de deteccion termica
     */
    public Vigilancia(String id, String serial, String modelo, String fabricante,
            double peso, boolean deteccionTermica) {
        super(id, serial, modelo, fabricante, peso);
        this.deteccionTermica = deteccionTermica;
    }

    /** @return siempre {@link TipoDron#VIGILANCIA} */
    @Override
    public TipoDron getTipo() {
        return TipoDron.VIGILANCIA;
    }

    /** @return {@code true} si posee deteccion termica */
    public boolean isDeteccionTermica() { return deteccionTermica; }
    /** @return {@code true} si posee deteccion termica; accesor para JavaFX */
    public boolean getDeteccionTermica() { return deteccionTermica; }
    /** @param deteccionTermica disponibilidad de deteccion termica */
    public void setDeteccionTermica(boolean deteccionTermica) { this.deteccionTermica = deteccionTermica; }
}
