package com.proyecto.drones.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Pruebas academicas del patron Composite de sensores. */
class CompositeTest {

    /** Verifica la jerarquia exacta solicitada para el Composite de sensores. */
    @Test
    void compositeConstruyeJerarquiaRequerida() {
        SensorCompuesto general = CompositeSensores.crearEstructuraRequerida();

        assertEquals("Sensor General", general.getNombre());
        assertEquals(4, general.getHijos().size());
        assertEquals(13, general.contarComponentes());
        assertTrue(general.esCompuesto());

        String estructura = general.mostrarEstructura();
        assertTrue(estructura.contains("Sensor Temperatura"));
        assertTrue(estructura.contains("Sensor Infrarrojo"));
        assertTrue(estructura.contains("RTD"));
        assertTrue(estructura.contains("Sensor Cámara"));
        assertTrue(estructura.contains("Sensor CMOS"));
        assertTrue(estructura.contains("Sensor CCD"));
        assertTrue(estructura.contains("Sensor Sonido"));
        assertTrue(estructura.contains("Sensor Analógico"));
        assertTrue(estructura.contains("Sensor Digital"));
        assertTrue(estructura.contains("SPI"));
        assertTrue(estructura.contains("UART"));
        assertTrue(estructura.contains("Sensor Inteligente"));
    }

    /** Verifica que una hoja no se comporte como un Composite. */
    @Test
    void sensorHojaNoEsCompuesto() {
        SensorHoja hoja = new SensorHoja("RTD");
        assertFalse(hoja.esCompuesto());
        assertEquals(1, hoja.contarComponentes());
    }
}
