package com.proyecto.drones.servicios;

import java.util.Objects;

import com.proyecto.drones.modelo.Dron;

/**
 * Fachada de alto nivel para una demostracion operativa de drones.
 *
 * <p>Oculta la coordinacion entre Bridge, Decorator y Composite. El
 * controlador puede invocar una sola operacion sin conocer los pasos internos
 * de cada subsistema. La fachada no modifica las entidades del package
 * {@code modelo} ni requiere persistencia adicional.</p>
 *
 * @since 1.3
 */
public final class SistemaDronesFacade {

    /** Crea una fachada sin estado lista para coordinar los subsistemas. */
    public SistemaDronesFacade() {
    }

    /**
     * Genera un resumen integral coordinando varios subsistemas del proyecto.
     *
     * <p>La llamada aplica Decorator segun la bateria, ejecuta Bridge con el
     * modo indicado y consulta el arbol Composite de sensores. El cliente solo
     * necesita llamar a este metodo de fachada.</p>
     *
     * @param dron dron que se demostrara
     * @param bateriaAdicional indica si se aplica el Decorator de bateria
     * @param modoControl implementacion Bridge que se utilizara
     * @return reporte unificado de los subsistemas
     */
    public String generarResumenOperativo(Dron dron, boolean bateriaAdicional,
            ModoControl modoControl) {
        Objects.requireNonNull(dron, "El dron no puede ser nulo.");
        Objects.requireNonNull(modoControl, "El modo de control no puede ser nulo.");

        DescripcionDron descripcion = BateriaAdicionalDecorator.aplicarSi(
                new DronDescripcionBase(dron), bateriaAdicional);
        ControlDron control = new ControlDron(dron, modoControl);
        ComponenteSensor sensores = CompositeSensores.crearEstructuraRequerida();

        return "FACADE - RESUMEN DEL DRON\n\n"
                + "Decorator:\n" + descripcion.getDescripcion() + "\n\n"
                + "Bridge:\n" + control.ejecutarControl() + "\n\n"
                + "Composite:\nLa estructura academica contiene "
                + sensores.contarComponentes() + " componentes de sensores.\n"
                + "Raiz: " + sensores.getNombre();
    }
}
