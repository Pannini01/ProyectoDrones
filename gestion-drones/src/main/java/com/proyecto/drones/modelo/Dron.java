package com.proyecto.drones.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.proyecto.drones.servicios.Prototipo;

/**
 * Clase base de todos los drones administrados por la aplicación.
 *
 * <p>Concentra los datos comunes y participa en el patrón Prototype mediante
 * {@link Prototipo}. Cada subtipo implementa {@link #clonar()} para conservar
 * su información específica.</p>
 *
 * @since 1.0
 */
public abstract class Dron implements Prototipo<Dron> {
    /** Identificador único persistido como clave primaria. */
    private String id;
    /** Serial único asignado por el fabricante. */
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

    /** Crea un dron vacío para constructores Builder o mapeo de datos. */
    protected Dron() {
    }

    /**
     * Inicializa los datos comunes de un dron.
     *
     * @param id identificador único
     * @param serial serial único del fabricante
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
     * Copia los atributos comunes en otro dron, incluidos piloto y sensores.
     *
     * <p>El piloto y cada sensor se copian como objetos independientes para
     * garantizar una clonación profunda.</p>
     *
     * @param destino instancia que recibirá los datos
     */
    protected void copiarDatosEn(Dron destino) {
        destino.id = id;
        destino.serial = serial;
        destino.modelo = modelo;
        destino.fabricante = fabricante;
        destino.peso = peso;
        destino.piloto = piloto == null ? null : new Piloto(piloto);
        destino.sensores = sensores.stream().map(Sensor::new).toList();
        destino.sensores = new ArrayList<>(destino.sensores);
    }

    /**
     * Obtiene el discriminador del subtipo concreto.
     *
     * @return tipo de dron utilizado también en PostgreSQL
     */
    public abstract TipoDron getTipo();

    /** @return identificador único del dron */
    public String getId() { return id; }
    /** @param id identificador único del dron */
    public void setId(String id) { this.id = id; }
    /** @return serial único del dron */
    public String getSerial() { return serial; }
    /** @param serial serial único del dron */
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
    /** @return piloto asignado, o {@code null} si aún no existe asignación */
    public Piloto getPiloto() { return piloto; }
    /** @param piloto piloto que operará el dron */
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

    @Override
    /**
     * Compara drones mediante su identificador.
     *
     * @param obj objeto que se comparará
     * @return {@code true} si los identificadores son iguales
     */
    public boolean equals(Object obj) {
        return obj instanceof Dron otro && Objects.equals(id, otro.id);
    }

    @Override
    /** @return código hash calculado a partir del identificador */
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
