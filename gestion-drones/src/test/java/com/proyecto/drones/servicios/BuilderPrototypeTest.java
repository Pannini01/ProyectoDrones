package com.proyecto.drones.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.proyecto.drones.modelo.Agricultura;
import com.proyecto.drones.modelo.Dron;
import com.proyecto.drones.modelo.Piloto;
import com.proyecto.drones.modelo.Sensor;
import com.proyecto.drones.modelo.Vigilancia;

class BuilderPrototypeTest {

    @Test
    void builderConstruyeDronAgricola() throws Exception {
        Agricultura dron = new DronBuilder.AgriculturaBuilder()
                .setId("A-1")
                .setSerial("AG-001")
                .setModelo("Agro X")
                .setFabricante("AeroTech")
                .setPeso(12.5)
                .setCapacidadTanque(24)
                .build();

        assertEquals("AG-001", dron.getSerial());
        assertEquals(24, dron.getCapacidadTanque());
    }

    @Test
    void builderConstruyeDronVigilancia() throws Exception {
        Vigilancia dron = new DronBuilder.VigilanciaBuilder()
                .setId("V-1")
                .setSerial("VG-001")
                .setModelo("Guardian")
                .setFabricante("AeroTech")
                .setPeso(8.2)
                .setDeteccionTermica(true)
                .build();

        assertTrue(dron.isDeteccionTermica());
    }

    @Test
    void prototypeClonaDesdeServiciosSinRegistroPrevio() throws Exception {
        Vigilancia original = new Vigilancia(
                "V-1", "VG-001", "Guardian", "AeroTech", 8.2, true);
        original.setPiloto(new Piloto("P-1", "Ana", "LIC-01", "3000000000"));
        original.getSensores().add(new Sensor("S-1", "Termico", "SensorCorp"));

        Dron clon = new Prototipo(original).clone();

        assertNotSame(original, clon);
        assertTrue(clon instanceof Vigilancia);
        assertEquals(original.getSerial(), clon.getSerial());
        assertEquals(original.getModelo(), clon.getModelo());

        assertNotSame(original.getPiloto(), clon.getPiloto());
        assertEquals(original.getPiloto().getNombre(), clon.getPiloto().getNombre());

        assertNotSame(original.getSensores(), clon.getSensores());
        assertNotSame(original.getSensores().get(0), clon.getSensores().get(0));
        assertEquals(original.getSensores().get(0).getTipo(), clon.getSensores().get(0).getTipo());
    }
}
