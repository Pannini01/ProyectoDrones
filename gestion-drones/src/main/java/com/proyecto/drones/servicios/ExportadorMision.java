package com.proyecto.drones.servicios;

import java.io.IOException;
import java.nio.file.Path;

import com.proyecto.drones.modelo.Mision;

/**
 * Contrato objetivo (Target) del patron Adapter para exportar una
 * {@link Mision} a un formato de archivo.
 *
 * @since 1.2
 */
public interface ExportadorMision {

    /**
     * Adapta y exporta la mision recibida.
     *
     * @param mision instancia del modelo que se exportara
     * @param directorio carpeta donde se creara el archivo
     * @return ruta absoluta del archivo creado
     * @throws IOException si el archivo no puede escribirse
     */
    Path exportar(Mision mision, Path directorio) throws IOException;
}
