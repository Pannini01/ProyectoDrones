package com.proyecto.drones.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Clase base de todos los drones administrados por la aplicacion.
 *
 * <p>Concentra exclusivamente los datos y comportamientos propios del dominio.
 * Los patrones de diseno que operan sobre un dron se implementan desde el
 * paquete de servicios.</p>
 *
 * @since 1.0
 */
public abstract class Dron {
    /** Identificador unico persistido como clave primaria. */
    private String id;
    /** Serial unico asignado por el fabricante. */
    private String serial;
    /** Referencia comercial del dron. */
    private String modelo;
    /** Empresa fabricante. */
    private String fabricante;
    /** Peso expresado en kilogramos. */
    private double peso;
    /** Piloto asignado, cuando existe. */
    private Piloto piloto;
    /** Sensores instalados en el dron. */
    private List<Sensor> sensores = new ArrayList<>();

    /** Crea un dron vacio para constructores Builder o mapeo de datos. */
    protected Dron() {
    }

    /**
     * Inicializa los datos comunes de un dron.
     *
     * @param id identificador unico
     * @param serial serial unico del fabricante
     * @param modelo modelo comercial
     * @param fabricante fabricante del dron
     * @param peso peso en kilogramos
     */
    protected Dron(String id, String serial, String modelo, String fabricante, double peso) {
        this.id = id;
        this.serial = serial;
        this.modelo = modelo;
        this.fabricante = fabricante;
        this.peso = peso;
    }

    /**
     * Obtiene el discriminador del subtipo concreto.
     *
     * @return tipo de dron utilizado tambien en PostgreSQL
     */
    public abstract TipoDron getTipo();

    /** @return identificador unico del dron */
    public String getId() { return id; }
    /** @param id identificador unico del dron */
    public void setId(String id) { this.id = id; }
    /** @return serial unico del dron */
    public String getSerial() { return serial; }
    /** @param serial serial unico del dron */
    public void setSerial(String serial) { this.serial = serial; }
    /** @return modelo comercial */
    public String getModelo() { return modelo; }
    /** @param modelo modelo comercial */
    public void setModelo(String modelo) { this.modelo = modelo; }
    /** @return nombre del fabricante */
    public String getFabricante() { return fabricante; }
    /** @param fabricante nombre del fabricante */
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }
    /** @return peso del dron en kilogramos */
    public double getPeso() { return peso; }
    /** @param peso peso del dron en kilogramos */
    public void setPeso(double peso) { this.peso = peso; }
    /** @return piloto asignado, o {@code null} si aun no existe asignacion */
    public Piloto getPiloto() { return piloto; }
    /** @param piloto piloto que operara el dron */
    public void setPiloto(Piloto piloto) { this.piloto = piloto; }
    /** @return lista mutable de sensores instalados */
    public List<Sensor> getSensores() { return sensores; }

    /**
     * Reemplaza los sensores por una copia de la lista proporcionada.
     *
     * @param sensores sensores instalados; puede ser {@code null}
     */
    public void setSensores(List<Sensor> sensores) {
        this.sensores = sensores == null ? new ArrayList<>() : new ArrayList<>(sensores);
    }

    /**
     * Compara drones mediante su identificador.
     *
     * @param obj objeto que se comparara
     * @return {@code true} si los identificadores son iguales
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Dron otro && Objects.equals(id, otro.id);
    }

    /** @return codigo hash calculado a partir del identificador */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
