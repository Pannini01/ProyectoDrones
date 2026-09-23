package com.proyecto.drones.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.proyecto.drones.modelo.Agricultura;
import com.proyecto.drones.modelo.Dron;

/**
 * Pruebas académicas de los patrones Bridge y Decorator.
 */
class BridgeDecoratorTest {

    /**
     * Comprueba que una misma abstracción Bridge puede cambiar de control
     * manual a autónomo sin sustituir ni modificar el dron asociado.
     */
    @Test
    void bridgeIntercambiaModoSinCambiarDron() {
        Dron dron = crearDronPrueba();
        ControlDron control = new ControlDron(dron, new ModoControl.ControlManual());

        String reporteManual = control.ejecutarControl();
        assertTrue(reporteManual.contains("Control manual"));
        assertTrue(reporteManual.contains("Despegue manual"));
        assertEquals(ModoControl.CODIGO_MANUAL, control.getModoControl().getCodigo());

        control.cambiarModoControl(new ModoControl.ControlAutonomo());
        String reporteAutonomo = control.ejecutarControl();

        assertTrue(reporteAutonomo.contains("Control autonomo"));
        assertTrue(reporteAutonomo.contains("Despegue autonomo"));
        assertEquals(ModoControl.CODIGO_AUTONOMO, control.getModoControl().getCodigo());
        assertTrue(ModoControl.desdeCodigo(ModoControl.CODIGO_MANUAL) instanceof ModoControl.ControlManual);
        assertTrue(ModoControl.desdeCodigo(ModoControl.CODIGO_AUTONOMO) instanceof ModoControl.ControlAutonomo);
        assertSame(dron, control.getDron());
        assertEquals("AG-BRIDGE-01", dron.getSerial());
    }

    /**
     * Comprueba que el Decorator agrega la batería cuando la opción es
     * seleccionada y conserva exactamente la misma entidad de dominio.
     */
    @Test
    void decoratorAgregaBateriaCuandoSeSelecciona() {
        Dron dron = crearDronPrueba();
        DescripcionDron base = new DronDescripcionBase(dron);
        DescripcionDron resultado = BateriaAdicionalDecorator.aplicarSi(base, true);

        assertFalse(base.getDescripcion().contains(BateriaAdicionalDecorator.DESCRIPCION_BATERIA));
        assertTrue(resultado.getDescripcion().contains(BateriaAdicionalDecorator.DESCRIPCION_BATERIA));
        assertSame(dron, resultado.getDron());
        assertEquals("Agro Test", dron.getModelo());
        assertEquals(12.5, dron.getPeso());
    }

    /**
     * Comprueba que al seleccionar que el dron no lleva batería adicional,
     * la descripción base permanece sin decoración.
     */
    @Test
    void decoratorNoAgregaBateriaCuandoNoSeSelecciona() {
        Dron dron = crearDronPrueba();
        DescripcionDron base = new DronDescripcionBase(dron);
        DescripcionDron resultado = BateriaAdicionalDecorator.aplicarSi(base, false);

        assertSame(base, resultado);
        assertFalse(resultado.getDescripcion().contains(BateriaAdicionalDecorator.DESCRIPCION_BATERIA));
        assertSame(dron, resultado.getDron());
    }


    /**
     * Verifica explicitamente las jerarquias requeridas para la sustentacion:
     * Bridge tiene dos subclases de ModoControl y Decorator tiene dos
     * subclases de DescripcionDron.
     */
    @Test
    void jerarquiasTienenExactamenteLasSubclasesEsperadas() {
        assertEquals(ModoControl.class, ModoControl.ControlManual.class.getSuperclass());
        assertEquals(ModoControl.class, ModoControl.ControlAutonomo.class.getSuperclass());
        assertEquals(DescripcionDron.class, DronDescripcionBase.class.getSuperclass());
        assertEquals(DescripcionDron.class, BateriaAdicionalDecorator.class.getSuperclass());
    }

    /** Crea una entidad válida sin involucrar PostgreSQL. */
    private Dron crearDronPrueba() {
        return new Agricultura(
                "A-BRIDGE-1",
                "AG-BRIDGE-01",
                "Agro Test",
                "Universidad",
                12.5,
                24.0);
    }
}
