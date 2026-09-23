package com.proyecto.drones.servicios;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import com.proyecto.drones.modelo.Dron;
import com.proyecto.drones.modelo.Mision;

/**
 * Adapter que transforma una instancia de {@link Mision} al texto requerido
 * por {@link ArchivoJson} y crea un archivo con extension {@code .json}.
 *
 * <p>La entidad Mision permanece intacta. La conversion pertenece por completo
 * al paquete servicios, como exige la actividad.</p>
 *
 * @since 1.2
 */
public final class MisionJsonAdapter implements ExportadorMision {
    /** Adaptee responsable de la escritura fisica. */
    private final ArchivoJson archivoJson;

    /** Crea el Adapter con el escritor JSON estandar. */
    public MisionJsonAdapter() {
        this(new ArchivoJson());
    }

    /**
     * Permite inyectar el Adaptee, principalmente para pruebas.
     *
     * @param archivoJson servicio de escritura
     */
    public MisionJsonAdapter(ArchivoJson archivoJson) {
        this.archivoJson = Objects.requireNonNull(archivoJson, "El escritor JSON no puede ser nulo.");
    }

    /** {@inheritDoc} */
    @Override
    public Path exportar(Mision mision, Path directorio) throws IOException {
        Objects.requireNonNull(mision, "La mision no puede ser nula.");
        Objects.requireNonNull(directorio, "El directorio no puede ser nulo.");
        validarMision(mision);

        String nombreSeguro = mision.getId().replaceAll("[^a-zA-Z0-9_-]", "_");
        Path destino = directorio.resolve("mision-" + nombreSeguro + ".json");
        return archivoJson.guardar(destino, convertirAJson(mision));
    }

    /**
     * Convierte la entidad del dominio a JSON sin agregar dependencias externas
     * ni alterar la clase Mision.
     *
     * @param mision mision que se adaptara
     * @return representacion JSON legible
     */
    public String convertirAJson(Mision mision) {
        Objects.requireNonNull(mision, "La mision no puede ser nula.");
        validarMision(mision);

        StringBuilder json = new StringBuilder();
        json.append("{\n")
                .append("  \"id\": \"").append(escapar(mision.getId())).append("\",\n")
                .append("  \"nombre\": \"").append(escapar(mision.getNombre())).append("\",\n")
                .append("  \"ubicacion\": \"").append(escapar(mision.getUbicacion())).append("\",\n")
                .append("  \"fecha\": \"").append(mision.getFecha()).append("\",\n")
                .append("  \"drones\": [");

        for (int i = 0; i < mision.getDrones().size(); i++) {
            Dron dron = mision.getDrones().get(i);
            if (i > 0) {
                json.append(',');
            }
            json.append("\n    {")
                    .append("\n      \"id\": \"").append(escapar(dron.getId())).append("\",")
                    .append("\n      \"serial\": \"").append(escapar(dron.getSerial())).append("\",")
                    .append("\n      \"tipo\": \"").append(dron.getTipo().name()).append("\",")
                    .append("\n      \"modelo\": \"").append(escapar(dron.getModelo())).append("\"")
                    .append("\n    }");
        }

        if (!mision.getDrones().isEmpty()) {
            json.append('\n').append("  ");
        }
        json.append("]\n}");
        return json.toString();
    }

    /** Valida los datos minimos utilizados en el nombre y contenido JSON. */
    private void validarMision(Mision mision) {
        if (mision.getId() == null || mision.getId().isBlank()
                || mision.getNombre() == null || mision.getNombre().isBlank()
                || mision.getUbicacion() == null || mision.getUbicacion().isBlank()
                || mision.getFecha() == null) {
            throw new IllegalArgumentException("La mision debe tener id, nombre, ubicacion y fecha.");
        }
    }

    /** Escapa los caracteres esenciales de una cadena JSON. */
    private String escapar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
