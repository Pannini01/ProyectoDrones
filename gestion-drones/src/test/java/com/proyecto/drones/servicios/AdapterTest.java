package com.proyecto.drones.servicios;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.proyecto.drones.modelo.Mision;

/** Pruebas academicas del patron Adapter de Mision a JSON. */
class AdapterTest {

    /** Verifica que Adapter cree un archivo JSON real desde una Mision. */
    @Test
    void adapterExportaMisionAJson(@TempDir Path directorioTemporal) throws Exception {
        Mision mision = new Mision();
        mision.setId("MIS-TEST-01");
        mision.setNombre("Mision de prueba");
        mision.setUbicacion("Bogota D.C.");
        mision.setFecha(LocalDate.of(2026, 9, 23));

        Path archivo = new MisionJsonAdapter().exportar(mision, directorioTemporal);

        assertTrue(Files.exists(archivo));
        assertTrue(archivo.getFileName().toString().endsWith(".json"));
        String contenido = Files.readString(archivo);
        assertTrue(contenido.contains("\"id\": \"MIS-TEST-01\""));
        assertTrue(contenido.contains("\"nombre\": \"Mision de prueba\""));
        assertTrue(contenido.contains("\"ubicacion\": \"Bogota D.C.\""));
        assertTrue(contenido.contains("\"fecha\": \"2026-09-23\""));
    }
}
