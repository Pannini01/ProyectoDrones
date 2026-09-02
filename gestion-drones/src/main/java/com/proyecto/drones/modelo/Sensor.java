package com.proyecto.drones.modelo;

/**
 * Dispositivo de medición o captura instalado en un dron.
 *
 * @since 1.0
 */
public class Sensor {
    /** Identificador único del sensor. */
    private String id;
    /** Tipo de medición o captura. */
    private String tipo;
    /** Empresa fabricante. */
    private String fabricante;

    /** Crea un sensor vacío. */
    public Sensor() {
    }

    /**
     * Crea un sensor con sus datos básicos.
     *
     * @param id identificador único
     * @param tipo clase de medición realizada
     * @param fabricante fabricante del sensor
     */
    public Sensor(String id, String tipo, String fabricante) {
        this.id = id;
        this.tipo = tipo;
        this.fabricante = fabricante;
    }

    /**
     * Crea una copia independiente de otro sensor.
     *
     * @param otro sensor que se copiará
     */
    public Sensor(Sensor otro) {
        this(otro.id, otro.tipo, otro.fabricante);
    }

    /** @return identificador único del sensor */
    public String getId() { return id; }
    /** @param id identificador único del sensor */
    public void setId(String id) { this.id = id; }
    /** @return tipo de sensor */
    public String getTipo() { return tipo; }
    /** @param tipo tipo de sensor */
    public void setTipo(String tipo) { this.tipo = tipo; }
    /** @return nombre del fabricante */
    public String getFabricante() { return fabricante; }
    /** @param fabricante nombre del fabricante */
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }
}
