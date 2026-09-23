package com.proyecto.drones.servicios;

/**
 * Construye la estructura Composite solicitada para la demostracion academica
 * de sensores.
 *
 * <p>La clase no es un participante adicional del patron; actua como apoyo
 * para construir siempre el mismo arbol requerido por la actividad.</p>
 *
 * @since 1.2
 */
public final class CompositeSensores {

    /** Evita instancias de la clase utilitaria. */
    private CompositeSensores() {
    }

    /**
     * Construye el arbol completo mostrado en el enunciado de la actividad.
     *
     * @return raiz {@code Sensor General}
     */
    public static SensorCompuesto crearEstructuraRequerida() {
        SensorCompuesto temperatura = new SensorCompuesto("Sensor Temperatura")
                .agregar(new SensorHoja("Sensor Infrarrojo"))
                .agregar(new SensorHoja("RTD"));

        SensorCompuesto camara = new SensorCompuesto("Sensor Cámara")
                .agregar(new SensorHoja("Sensor CMOS"))
                .agregar(new SensorHoja("Sensor CCD"));

        SensorCompuesto digital = new SensorCompuesto("Sensor Digital")
                .agregar(new SensorHoja("SPI"))
                .agregar(new SensorHoja("UART"));

        SensorCompuesto sonido = new SensorCompuesto("Sensor Sonido")
                .agregar(new SensorHoja("Sensor Analógico"))
                .agregar(digital);

        return new SensorCompuesto("Sensor General")
                .agregar(temperatura)
                .agregar(camara)
                .agregar(sonido)
                .agregar(new SensorHoja("Sensor Inteligente"));
    }
}
