package com.proyecto.drones.servicios;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Servicio existente/adaptado (Adaptee) que sabe guardar texto JSON en disco,
 * pero no conoce la clase de dominio {@code Mision}.
 *
 * @since 1.2
 */
public final class ArchivoJson {

    /**
     * Guarda un texto JSON en la ruta indicada, creando las carpetas necesarias.
     *
     * @param archivo ruta final del archivo
     * @param contenido contenido JSON ya preparado
     * @return ruta absoluta normalizada del archivo creado
     * @throws IOException si ocurre un error de escritura
     */
    public Path guardar(Path archivo, String contenido) throws IOException {
        Objects.requireNonNull(archivo, "La ruta del archivo no puede ser nula.");
        Objects.requireNonNull(contenido, "El contenido JSON no puede ser nulo.");

        Path absoluta = archivo.toAbsolutePath().normalize();
        Path padre = absoluta.getParent();
        if (padre != null) {
            Files.createDirectories(padre);
        }
        Files.writeString(absoluta, contenido, StandardCharsets.UTF_8);
        return absoluta;
    }
}
