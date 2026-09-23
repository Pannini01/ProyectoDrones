/**
 * Servicios de construccion, clonacion, validacion, conexion y demostracion de
 * patrones de diseno de la aplicacion.
 *
 * <p>Incluye Builder, Prototype, Factory Method, Singleton, Bridge, Decorator,
 * Composite y Adapter. Prototype se implementa mediante {@code Prototipo},
 * manteniendo al paquete {@code modelo} independiente del patron y sin utilizar
 * {@code Cloneable} ni un PrototypeRegistry.</p>
 *
 * <p>Bridge utiliza {@code ControlDron} como abstraccion y
 * {@code ModoControl} como implementador abstracto, con exactamente dos
 * subclases: {@code ControlManual} y {@code ControlAutonomo}. Decorator utiliza
 * {@code DescripcionDron} como componente abstracto, con exactamente dos
 * subclases: {@code DronDescripcionBase} y
 * {@code BateriaAdicionalDecorator}.</p>
 *
 * <p>Composite utiliza {@code ComponenteSensor} como componente comun,
 * {@code SensorCompuesto} para los nodos que agrupan sensores y
 * {@code SensorHoja} para sensores terminales. Adapter utiliza
 * {@code ExportadorMision} como Target, {@code MisionJsonAdapter} como Adapter
 * y {@code ArchivoJson} como Adaptee para crear archivos JSON a partir de
 * instancias de {@code Mision} sin modificar el package modelo.</p>
 *
 * @since 1.0
 */
package com.proyecto.drones.servicios;
