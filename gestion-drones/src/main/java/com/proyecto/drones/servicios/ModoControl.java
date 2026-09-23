package com.proyecto.drones.servicios;

import java.util.Objects;

import com.proyecto.drones.modelo.Dron;

/**
 * Implementador abstracto del patron Bridge.
 *
 * <p>Define las operaciones que pueden variar de forma independiente de
 * {@link ControlDron}. El proyecto mantiene exactamente dos subclases:
 * {@link ControlManual} y {@link ControlAutonomo}. Cada subclase expone un
 * codigo estable que puede persistirse en PostgreSQL sin modificar la entidad
 * {@link Dron}.</p>
 *
 * @since 1.1
 */
public abstract class ModoControl {
    /** Codigo persistido para el modo manual. */
    public static final String CODIGO_MANUAL = "MANUAL";
    /** Codigo persistido para el modo autonomo. */
    public static final String CODIGO_AUTONOMO = "AUTONOMO";

    /**
     * @return codigo estable usado en PostgreSQL
     */
    public abstract String getCodigo();

    /**
     * @return nombre legible del modo de control
     */
    public abstract String getNombre();

    /**
     * @param dron dron controlado
     * @return descripcion del despegue
     */
    public abstract String despegar(Dron dron);

    /**
     * @param dron dron controlado
     * @return descripcion de la navegacion
     */
    public abstract String navegar(Dron dron);

    /**
     * @param dron dron controlado
     * @return descripcion del aterrizaje
     */
    public abstract String aterrizar(Dron dron);

    /** Valida el objeto recibido sin modificar su estado. */
    protected final void validarDron(Dron dron) {
        Objects.requireNonNull(dron, "El dron controlado no puede ser nulo.");
    }

    /**
     * Reconstruye la subclase Bridge correspondiente a un codigo persistido.
     *
     * @param codigo valor almacenado en la columna {@code modo_control}
     * @return instancia manual o autonoma
     * @throws IllegalArgumentException si el codigo no pertenece a un modo conocido
     */
    public static ModoControl desdeCodigo(String codigo) {
        if (CODIGO_MANUAL.equalsIgnoreCase(codigo)) {
            return new ControlManual();
        }
        if (CODIGO_AUTONOMO.equalsIgnoreCase(codigo)) {
            return new ControlAutonomo();
        }
        throw new IllegalArgumentException("Modo de control desconocido: " + codigo);
    }

    /**
     * Primera subclase concreta del Bridge: control operado manualmente.
     */
    public static final class ControlManual extends ModoControl {

        @Override
        public String getCodigo() {
            return CODIGO_MANUAL;
        }

        @Override
        public String getNombre() {
            return "Control manual";
        }

        @Override
        public String despegar(Dron dron) {
            validarDron(dron);
            return "Despegue manual autorizado por el operador.";
        }

        @Override
        public String navegar(Dron dron) {
            validarDron(dron);
            return "Navegacion manual mediante instrucciones directas del operador.";
        }

        @Override
        public String aterrizar(Dron dron) {
            validarDron(dron);
            return "Aterrizaje manual confirmado por el operador.";
        }
    }

    /**
     * Segunda subclase concreta del Bridge: control con operacion autonoma.
     */
    public static final class ControlAutonomo extends ModoControl {

        @Override
        public String getCodigo() {
            return CODIGO_AUTONOMO;
        }

        @Override
        public String getNombre() {
            return "Control autonomo";
        }

        @Override
        public String despegar(Dron dron) {
            validarDron(dron);
            return "Despegue autonomo tras verificacion automatica de condiciones.";
        }

        @Override
        public String navegar(Dron dron) {
            validarDron(dron);
            return "Navegacion autonoma mediante ruta y correcciones automaticas.";
        }

        @Override
        public String aterrizar(Dron dron) {
            validarDron(dron);
            return "Aterrizaje autonomo con aproximacion y descenso automaticos.";
        }
    }
}
