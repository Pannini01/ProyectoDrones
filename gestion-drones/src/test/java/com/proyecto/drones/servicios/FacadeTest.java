package com.proyecto.drones.servicios;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.proyecto.drones.modelo.Agricultura;
import com.proyecto.drones.modelo.Dron;

/** Pruebas JUnit del patron Facade. */
class FacadeTest {

    /**
     * Comprueba que una sola llamada de fachada coordina Decorator, Bridge y
     * Composite sin modificar el objeto Dron.
     */
    @Test
    void facadeCoordinaSubsistemasConUnaSolaOperacion() {
        Dron dron = new Agricultura(
                "FAC-1", "FAC-001", "Agro Facade", "Universidad", 10.0, 20.0);
        SistemaDronesFacade facade = new SistemaDronesFacade();

        String resumen = facade.generarResumenOperativo(
                dron,
                true,
                new ModoControl.ControlAutonomo());

        assertTrue(resumen.contains("FACADE - RESUMEN DEL DRON"));
        assertTrue(resumen.contains(BateriaAdicionalDecorator.DESCRIPCION_BATERIA));
        assertTrue(resumen.contains("Control autonomo"));
        assertTrue(resumen.contains("Composite"));
        assertTrue(resumen.contains("Sensor General"));
        assertTrue(resumen.contains("13 componentes"));
    }
}
