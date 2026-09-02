package com.proyecto.drones.servicios;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.proyecto.drones.excepciones.PersistenciaException;

/**
 * Lector del archivo {@code .env} que evita incluir credenciales dentro del
 * código fuente.
 *
 * <p>Admite comentarios, líneas vacías y valores encerrados entre comillas.</p>
 *
 * @since 1.0
 */
public final class ConfiguracionEnv {
    /** Variables leídas del archivo, indexadas por nombre. */
    private final Map<String, String> valores;

    /**
     * Crea una configuración ya analizada.
     *
     * @param valores variables disponibles
     */
    private ConfiguracionEnv(Map<String, String> valores) {
        this.valores = valores;
    }

    /**
     * Lee el archivo {@code .env} ubicado en la raíz de ejecución.
     *
     * @return configuración cargada en memoria
     * @throws PersistenciaException si el archivo no existe o no puede leerse
     */
    public static ConfiguracionEnv cargar() throws PersistenciaException {
        Path archivo = Path.of(".env");
        if (!Files.isRegularFile(archivo)) {
            throw new PersistenciaException("No se encontro el archivo .env en la raiz del proyecto.");
        }
        try {
            Map<String, String> valores = new HashMap<>();
            for (String linea : Files.readAllLines(archivo)) {
                String limpia = linea.trim();
                if (limpia.isEmpty() || limpia.startsWith("#") || !limpia.contains("=")) {
                    continue;
                }
                String[] partes = limpia.split("=", 2);
                valores.put(partes[0].trim(), quitarComillas(partes[1].trim()));
            }
            return new ConfiguracionEnv(valores);
        } catch (IOException e) {
            throw new PersistenciaException("No fue posible leer el archivo .env.", e);
        }
    }

    /**
     * Obtiene una propiedad obligatoria.
     *
     * @param clave nombre de la variable
     * @return valor configurado
     * @throws PersistenciaException si la variable no existe o está vacía
     */
    public String requerido(String clave) throws PersistenciaException {
        String valor = valores.get(clave);
        if (valor == null || valor.isBlank()) {
            throw new PersistenciaException("Falta configurar " + clave + " en el archivo .env.");
        }
        return valor;
    }

    /**
     * Retira comillas simples o dobles que envuelvan un valor.
     *
     * @param valor texto leído del archivo
     * @return valor sin comillas exteriores
     */
    private static String quitarComillas(String valor) {
        if (valor.length() >= 2 && ((valor.startsWith("\"") && valor.endsWith("\""))
                || (valor.startsWith("'") && valor.endsWith("'")))) {
            return valor.substring(1, valor.length() - 1);
        }
        return valor;
    }
}
