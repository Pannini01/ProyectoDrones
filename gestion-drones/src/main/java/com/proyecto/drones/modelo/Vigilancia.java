package com.proyecto.drones.modelo;

/**
 * Dron especializado en vigilancia y detección térmica.
 *
 * @since 1.0
 */
public class Vigilancia extends Dron {
    /** Indica si el equipo incorpora detección térmica. */
    private boolean deteccionTermica;

    /** Crea un dron de vigilancia vacío para utilizarlo con Builder. */
    public Vigilancia() {
    }

    /**
     * Crea un dron de vigilancia completamente configurado.
     *
     * @param id identificador único
     * @param serial serial único
     * @param modelo modelo comercial
     * @param fabricante fabricante
     * @param peso peso en kilogramos
     * @param deteccionTermica indica si dispone de detección térmica
     */
    public Vigilancia(String id, String serial, String modelo, String fabricante,
            double peso, boolean deteccionTermica) {
        super(id, serial, modelo, fabricante, peso);
        this.deteccionTermica = deteccionTermica;
    }

    @Override
    /**
     * Crea una copia profunda del dron de vigilancia.
     *
     * @return nueva instancia con los mismos datos
     */
    public Vigilancia clonar() {
        Vigilancia copia = new Vigilancia();
        copiarDatosEn(copia);
        copia.deteccionTermica = deteccionTermica;
        return copia;
    }

    @Override
    /** @return siempre {@link TipoDron#VIGILANCIA} */
    public TipoDron getTipo() {
        return TipoDron.VIGILANCIA;
    }

    /** @return {@code true} si posee detección térmica */
    public boolean isDeteccionTermica() { return deteccionTermica; }
    /** @return {@code true} si posee detección térmica; accesor para JavaFX */
    public boolean getDeteccionTermica() { return deteccionTermica; }
    /** @param deteccionTermica disponibilidad de detección térmica */
    public void setDeteccionTermica(boolean deteccionTermica) { this.deteccionTermica = deteccionTermica; }
}
