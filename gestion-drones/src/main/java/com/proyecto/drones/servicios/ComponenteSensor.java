package com.proyecto.drones.servicios;

import java.util.Objects;

/**
 * Componente abstracto del patron Composite para representar una estructura
 * jerarquica de sensores.
 *
 * <p>Las hojas y los grupos se manipulan mediante el mismo tipo, por lo que el
 * cliente puede recorrer el arbol sin conocer si cada elemento contiene hijos.
 * Esta clase pertenece al paquete {@code servicios}; no modifica la entidad
 * {@code Sensor} del paquete modelo.</p>
 *
 * @since 1.2
 */
public abstract class ComponenteSensor {
    /** Nombre visible del sensor o grupo. */
    private final String nombre;

    /**
     * Crea un componente con un nombre obligatorio.
     *
     * @param nombre nombre mostrado en la estructura
     */
    protected ComponenteSensor(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del componente sensor es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    /**
     * @return nombre del componente
     */
    public final String getNombre() {
        return nombre;
    }

    /**
     * Genera una representacion textual de todo el subarbol que comienza en
     * este componente.
     *
     * @return estructura jerarquica legible
     */
    public final String mostrarEstructura() {
        StringBuilder salida = new StringBuilder();
        agregarRepresentacion(salida, 0);
        return salida.toString().stripTrailing();
    }

    /**
     * Agrega la representacion del componente y, cuando corresponda, de sus
     * hijos.
     *
     * @param salida acumulador de texto
     * @param nivel profundidad dentro del arbol
     */
    protected abstract void agregarRepresentacion(StringBuilder salida, int nivel);

    /**
     * Cuenta el componente actual y todos los descendientes.
     *
     * @return cantidad total de nodos del subarbol
     */
    public abstract int contarComponentes();

    /**
     * Indica si el componente puede contener otros sensores.
     *
     * @return {@code true} para un Composite; {@code false} para una hoja
     */
    public abstract boolean esCompuesto();

    /** Escribe una linea con indentacion comun a hojas y compuestos. */
    protected final void agregarLinea(StringBuilder salida, int nivel) {
        Objects.requireNonNull(salida, "El acumulador no puede ser nulo.");
        if (nivel > 0) {
            salida.append("  ".repeat(nivel)).append("- ");
        }
        salida.append(nombre).append(System.lineSeparator());
    }
}
