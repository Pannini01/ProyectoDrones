package com.proyecto.drones.modelo;

/**
 * Dron especializado en labores agricolas y transporte de liquidos.
 *
 * @since 1.0
 */
public class Agricultura extends Dron {
    /** Capacidad del tanque expresada en litros. */
    private double capacidadTanque;

    /** Crea un dron agricola vacio para utilizarlo con Builder. */
    public Agricultura() {
    }

    /**
     * Crea un dron agricola completamente configurado.
     *
     * @param id identificador unico
     * @param serial serial unico
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

    /** @return siempre {@link TipoDron#AGRICULTURA} */
    @Override
    public TipoDron getTipo() {
        return TipoDron.AGRICULTURA;
    }

    /** @return capacidad del tanque en litros */
    public double getCapacidadTanque() { return capacidadTanque; }
    /** @param capacidadTanque capacidad del tanque en litros */
    public void setCapacidadTanque(double capacidadTanque) { this.capacidadTanque = capacidadTanque; }
}
