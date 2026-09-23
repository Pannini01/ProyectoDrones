package com.proyecto.drones.servicios;

import java.util.Objects;

/**
 * Segunda subclase concreta de {@link DescripcionDron}.
 *
 * <p>Envuelve otra descripcion y agrega la informacion de bateria adicional.
 * El objeto del package modelo permanece intacto; la seleccion Si/No se
 * persiste mediante el DAO.</p>
 *
 * @since 1.1
 */
public final class BateriaAdicionalDecorator extends DescripcionDron {
    /** Texto agregado por el decorador. */
    public static final String DESCRIPCION_BATERIA =
            "Bateria adicional instalada como respaldo de energia";

    /** Componente original o previamente decorado. */
    private final DescripcionDron componente;

    /**
     * Crea el decorador de bateria adicional.
     *
     * @param componente descripcion que sera extendida
     */
    public BateriaAdicionalDecorator(DescripcionDron componente) {
        super(Objects.requireNonNull(
                componente,
                "El componente decorado no puede ser nulo.").getDron());
        this.componente = componente;
    }

    /**
     * Aplica el decorador solo cuando la seleccion indica que el dron lleva
     * bateria adicional.
     *
     * @param componente descripcion base
     * @param incluirBateria true para decorar; false para mantener la base
     * @return descripcion correspondiente a la seleccion
     */
    public static DescripcionDron aplicarSi(
            DescripcionDron componente,
            boolean incluirBateria) {

        Objects.requireNonNull(
                componente,
                "El componente decorado no puede ser nulo.");

        return incluirBateria
                ? new BateriaAdicionalDecorator(componente)
                : componente;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescripcion() {
        return componente.getDescripcion()
                + " | "
                + DESCRIPCION_BATERIA;
    }
}
