package com.proyecto.drones.modelo;

/**
 * Dron especializado en labores agrícolas y transporte de líquidos.
 *
 * @since 1.0
 */
public class Agricultura extends Dron {
    /** Capacidad del tanque expresada en litros. */
    private double capacidadTanque;

    /** Crea un dron agrícola vacío para utilizarlo con Builder. */
    public Agricultura() {
    }

    /**
     * Crea un dron agrícola completamente configurado.
     *
     * @param id identificador único
     * @param serial serial único
     * @param modelo modelo comercial
     * @param fabricante fabricante
     * @param peso peso en kilogramos
     * @param capacidadTanque capacidad del tanque en litros
     */
    public Agricultura(String id, String serial, String modelo, String fabricante,
            double peso, double capacidadTanque) {
        super(id, serial, modelo, fabricante, peso);
        this.capacidadTanque = capacidadTanque;
    }

    @Override
    /**
     * Crea una copia profunda del dron agrícola.
     *
     * @return nueva instancia con los mismos datos
     */
    public Agricultura clonar() {
        Agricultura copia = new Agricultura();
        copiarDatosEn(copia);
        copia.capacidadTanque = capacidadTanque;
        return copia;
    }

    @Override
    /** @return siempre {@link TipoDron#AGRICULTURA} */
    public TipoDron getTipo() {
        return TipoDron.AGRICULTURA;
    }

    /** @return capacidad del tanque en litros */
    public double getCapacidadTanque() { return capacidadTanque; }
    /** @param capacidadTanque capacidad del tanque en litros */
    public void setCapacidadTanque(double capacidadTanque) { this.capacidadTanque = capacidadTanque; }
}
