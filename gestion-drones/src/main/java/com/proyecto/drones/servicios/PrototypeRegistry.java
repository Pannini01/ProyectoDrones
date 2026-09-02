package com.proyecto.drones.servicios;

import java.util.HashMap;
import java.util.Map;

import com.proyecto.drones.excepciones.PrototipoException;

/**
 * Registro genérico de objetos clonables utilizado por el patrón Prototype.
 *
 * <p>El registro almacena una copia del objeto recibido y entrega una nueva
 * copia en cada consulta. De esta manera nunca expone ni modifica el prototipo
 * interno.</p>
 *
 * @param <T> tipo de prototipo administrado
 * @since 1.0
 */
public class PrototypeRegistry<T extends Prototipo<T>> {
    /** Prototipos almacenados por clave normalizada. */
    private final Map<String, T> cache = new HashMap<>();

    /**
     * Registra una copia del prototipo bajo una clave normalizada.
     *
     * @param clave nombre utilizado para recuperar el prototipo
     * @param prototipo objeto que se copiará y almacenará
     * @throws PrototipoException si la clave está vacía o el objeto es nulo
     */
    public void registrarPrototipo(String clave, T prototipo) throws PrototipoException {
        if (clave == null || clave.isBlank()) {
            throw new PrototipoException("La clave del prototipo es obligatoria.");
        }
        if (prototipo == null) {
            throw new PrototipoException("Debe seleccionar o construir un dron para registrarlo.");
        }
        cache.put(clave.trim().toLowerCase(), prototipo.clonar());
    }

    /**
     * Obtiene una nueva copia del prototipo asociado a una clave.
     *
     * @param clave nombre del prototipo registrado
     * @return clon independiente del prototipo
     * @throws PrototipoException si la clave está vacía o no existe
     */
    public T obtenerClon(String clave) throws PrototipoException {
        if (clave == null || clave.isBlank()) {
            throw new PrototipoException("Digite la clave del prototipo que desea clonar.");
        }
        T prototipo = cache.get(clave.trim().toLowerCase());
        if (prototipo == null) {
            throw new PrototipoException("No existe un prototipo registrado con la clave '" + clave + "'.");
        }
        return prototipo.clonar();
    }
}
