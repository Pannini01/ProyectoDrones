package com.proyecto.drones.servicios;

import java.util.Objects;

import com.proyecto.drones.modelo.Dron;

/**
 * Abstraccion principal del patron Bridge para controlar un {@link Dron}.
 *
 * <p>La clase mantiene separadas dos dimensiones: el dron que se controla y
 * la implementacion concreta del modo de control. El modo puede cambiarse en
 * tiempo de ejecucion sin modificar la entidad del paquete modelo.</p>
 *
 * @since 1.1
 */
public final class ControlDron {
    /** Entidad sobre la cual se demuestra el control. */
    private final Dron dron;

    /** Implementador actual del Bridge. */
    private ModoControl modoControl;

    /**
     * Crea el Bridge entre un dron y un modo de control.
     *
     * @param dron dron que sera controlado
     * @param modoControl implementacion inicial del control
     * @throws NullPointerException si alguno de los argumentos es nulo
     */
    public ControlDron(Dron dron, ModoControl modoControl) {
        this.dron = Objects.requireNonNull(dron, "El dron no puede ser nulo.");
        this.modoControl = Objects.requireNonNull(
                modoControl,
                "El modo de control no puede ser nulo.");
    }

    /**
     * Cambia solamente la implementacion del Bridge.
     *
     * @param modoControl nuevo modo manual o autonomo
     */
    public void cambiarModoControl(ModoControl modoControl) {
        this.modoControl = Objects.requireNonNull(
                modoControl,
                "El modo de control no puede ser nulo.");
    }

    /**
     * @return dron asociado al Bridge
     */
    public Dron getDron() {
        return dron;
    }

    /**
     * @return modo de control actualmente conectado
     */
    public ModoControl getModoControl() {
        return modoControl;
    }

    /**
     * Ejecuta una demostracion completa delegando el comportamiento al modo
     * concreto actualmente conectado.
     *
     * @return reporte de despegue, navegacion y aterrizaje
     */
    public String ejecutarControl() {
        return "Dron: " + dron.getSerial() + " (" + dron.getTipo() + ")\n"
                + "Modo: " + modoControl.getNombre() + "\n"
                + "1. " + modoControl.despegar(dron) + "\n"
                + "2. " + modoControl.navegar(dron) + "\n"
                + "3. " + modoControl.aterrizar(dron);
    }
}
