package com.proyecto.drones.servicios;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Nodo Composite que agrupa otros {@link ComponenteSensor}.
 *
 * <p>Permite agregar y quitar tanto hojas como otros grupos, haciendo posible
 * construir estructuras de profundidad arbitraria como la requerida para los
 * sensores de temperatura, camara y sonido.</p>
 *
 * @since 1.2
 */
public final class SensorCompuesto extends ComponenteSensor {
    /** Hijos directos del grupo. */
    private final List<ComponenteSensor> hijos = new ArrayList<>();

    /**
     * @param nombre nombre del grupo de sensores
     */
    public SensorCompuesto(String nombre) {
        super(nombre);
    }

    /**
     * Agrega un hijo al Composite.
     *
     * @param componente hoja o grupo que se agregara
     * @return esta misma instancia para facilitar la construccion fluida
     */
    public SensorCompuesto agregar(ComponenteSensor componente) {
        hijos.add(Objects.requireNonNull(componente, "El componente hijo no puede ser nulo."));
        return this;
    }

    /**
     * Elimina un hijo directo si se encuentra en el grupo.
     *
     * @param componente componente a eliminar
     * @return {@code true} si fue eliminado
     */
    public boolean quitar(ComponenteSensor componente) {
        return hijos.remove(componente);
    }

    /**
     * @return vista de solo lectura de los hijos directos
     */
    public List<ComponenteSensor> getHijos() {
        return Collections.unmodifiableList(hijos);
    }

    /** {@inheritDoc} */
    @Override
    protected void agregarRepresentacion(StringBuilder salida, int nivel) {
        agregarLinea(salida, nivel);
        for (ComponenteSensor hijo : hijos) {
            hijo.agregarRepresentacion(salida, nivel + 1);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int contarComponentes() {
        return 1 + hijos.stream().mapToInt(ComponenteSensor::contarComponentes).sum();
    }

    /** {@inheritDoc} */
    @Override
    public boolean esCompuesto() {
        return true;
    }
}
