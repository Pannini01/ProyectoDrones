package com.proyecto.drones.servicios;

import java.util.ArrayList;
import java.util.List;

import com.proyecto.drones.excepciones.PrototipoException;
import com.proyecto.drones.modelo.Agricultura;
import com.proyecto.drones.modelo.Dron;
import com.proyecto.drones.modelo.Piloto;
import com.proyecto.drones.modelo.Sensor;
import com.proyecto.drones.modelo.Vigilancia;

/**
 * Implementacion compacta del patron Prototype para los drones.
 *
 * <p>La logica permanece completamente en el paquete de servicios para evitar
 * que las entidades del paquete modelo dependan del patron. La clase recibe un
 * {@link Dron} existente y construye una nueva instancia independiente del
 * mismo subtipo.</p>
 *
 * <p>No utiliza {@link java.lang.Cloneable}, {@code Object.clone()},
 * {@code super.clone()}, PrototypeRegistry ni registro previo.</p>
 *
 * @since 1.0
 */
public final class Prototipo {

    /** Dron que actua como objeto origen del Prototype. */
    private final Dron origen;

    /**
     * Crea un Prototype a partir de un dron existente.
     *
     * @param origen dron que se desea copiar
     * @throws PrototipoException si el dron es nulo o su subtipo no esta soportado
     */
    public Prototipo(Dron origen) throws PrototipoException {
        if (origen == null) {
            throw new PrototipoException("El dron origen no puede ser nulo.");
        }

        if (!(origen instanceof Agricultura) && !(origen instanceof Vigilancia)) {
            throw new PrototipoException(
                    "El tipo de dron " + origen.getClass().getSimpleName()
                    + " no esta soportado por Prototype.");
        }

        this.origen = origen;
    }

    /**
     * Genera una copia independiente del dron origen.
     *
     * @return nueva instancia del mismo subtipo del dron origen
     */
    public Dron clone() {
        Dron copia = crearCopiaDelSubtipo();
        copiarObjetosRelacionados(copia);
        return copia;
    }

    /**
     * Construye una nueva instancia preservando los atributos comunes y el
     * atributo especifico del subtipo concreto.
     *
     * @return nueva instancia de {@link Agricultura} o {@link Vigilancia}
     */
    private Dron crearCopiaDelSubtipo() {
        if (origen instanceof Agricultura agricultura) {
            return new Agricultura(
                    agricultura.getId(),
                    agricultura.getSerial(),
                    agricultura.getModelo(),
                    agricultura.getFabricante(),
                    agricultura.getPeso(),
                    agricultura.getCapacidadTanque());
        }

        if (origen instanceof Vigilancia vigilancia) {
            return new Vigilancia(
                    vigilancia.getId(),
                    vigilancia.getSerial(),
                    vigilancia.getModelo(),
                    vigilancia.getFabricante(),
                    vigilancia.getPeso(),
                    vigilancia.isDeteccionTermica());
        }

        // El constructor ya impide llegar a este estado.
        throw new IllegalStateException("Subtipo de dron no soportado.");
    }

    /**
     * Realiza copia profunda de los objetos mutables asociados al dron.
     *
     * @param copia dron que recibira piloto y sensores independientes
     */
    private void copiarObjetosRelacionados(Dron copia) {
        copiarPiloto(copia);
        copiarSensores(copia);
    }

    /**
     * Copia el piloto mediante su constructor de copia.
     *
     * @param copia dron destino
     */
    private void copiarPiloto(Dron copia) {
        Piloto pilotoOrigen = origen.getPiloto();
        copia.setPiloto(pilotoOrigen == null ? null : new Piloto(pilotoOrigen));
    }

    /**
     * Copia la coleccion de sensores creando una instancia nueva para cada
     * sensor, de forma que original y copia no compartan objetos mutables.
     *
     * @param copia dron destino
     */
    private void copiarSensores(Dron copia) {
        List<Sensor> sensoresCopia = new ArrayList<>();

        if (origen.getSensores() != null) {
            for (Sensor sensor : origen.getSensores()) {
                if (sensor != null) {
                    sensoresCopia.add(new Sensor(sensor));
                }
            }
        }

        copia.setSensores(sensoresCopia);
    }
}
