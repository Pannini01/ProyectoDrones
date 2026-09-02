package com.proyecto.drones.servicios;

import com.proyecto.drones.excepciones.ValidacionException;
import com.proyecto.drones.modelo.Agricultura;
import com.proyecto.drones.modelo.Dron;

/**
 * Centraliza las reglas de validación aplicables a todos los drones.
 *
 * @since 1.0
 */
public final class ValidadorDron {
    /** Impide instanciar una clase compuesta solo por operaciones estáticas. */
    private ValidadorDron() {
    }

    /**
     * Comprueba datos comunes y atributos particulares de la instancia.
     *
     * @param dron objeto que se validará
     * @throws ValidacionException si falta un dato obligatorio o un valor
     *         numérico no es positivo
     */
    public static void validar(Dron dron) throws ValidacionException {
        if (dron.getId() == null || dron.getId().isBlank()) {
            throw new ValidacionException("El identificador del dron es obligatorio.");
        }
        if (dron.getSerial() == null || dron.getSerial().isBlank()) {
            throw new ValidacionException("El serial es obligatorio.");
        }
        if (dron.getModelo() == null || dron.getModelo().isBlank()) {
            throw new ValidacionException("El modelo es obligatorio.");
        }
        if (dron.getFabricante() == null || dron.getFabricante().isBlank()) {
            throw new ValidacionException("El fabricante es obligatorio.");
        }
        if (dron.getPeso() <= 0) {
            throw new ValidacionException("El peso debe ser mayor que cero.");
        }
        if (dron instanceof Agricultura agricultura && agricultura.getCapacidadTanque() <= 0) {
            throw new ValidacionException("La capacidad del tanque debe ser mayor que cero.");
        }
    }
}
