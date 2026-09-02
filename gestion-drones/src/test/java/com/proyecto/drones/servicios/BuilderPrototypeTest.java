package com.proyecto.drones.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.proyecto.drones.modelo.Agricultura;
import com.proyecto.drones.modelo.Dron;
import com.proyecto.drones.modelo.Sensor;
import com.proyecto.drones.modelo.Vigilancia;

class BuilderPrototypeTest {

    @Test
    void builderConstruyeDronAgricola() throws Exception {
        Agricultura dron = new AgriculturaDronBuilder()
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
        Vigilancia dron = new VigilanciaDronBuilder()
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
    void prototypeProduceCopiaProfunda() throws Exception {
        Vigilancia original = new Vigilancia("V-1", "VG-001", "Guardian", "AeroTech", 8.2, true);
        original.getSensores().add(new Sensor("S-1", "Termico", "SensorCorp"));
        PrototypeRegistry<Dron> registro = new PrototypeRegistry<>();
        registro.registrarPrototipo("termico", original);

        Dron clon = registro.obtenerClon("termico");

        assertNotSame(original, clon);
        assertNotSame(original.getSensores(), clon.getSensores());
        assertNotSame(original.getSensores().get(0), clon.getSensores().get(0));
        assertEquals(original.getSerial(), clon.getSerial());
    }
}
