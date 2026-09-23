# Patrones estructurales: Bridge, Decorator, Composite y Adapter

## Bridge

El patron Bridge separa el control de un dron de la forma concreta en que se ejecuta ese control, sin modificar las clases del package `modelo`.

La estructura actual es:

1. `ControlDron`: abstraccion del Bridge. Conserva el `Dron` y una referencia a `ModoControl`.
2. `ModoControl`: implementador abstracto.
3. `ModoControl.ControlManual`: primera subclase concreta; ejecuta despegue, navegacion y aterrizaje manuales.
4. `ModoControl.ControlAutonomo`: segunda subclase concreta; ejecuta las mismas operaciones de forma autonoma.

`ModoControl` tiene exactamente dos subclases. `ControlDron` puede sustituir una implementacion por otra en tiempo de ejecucion mediante `cambiarModoControl(...)`, sin sustituir ni modificar el objeto `Dron`.

La interfaz JavaFX mantiene las acciones **Control manual** y **Control autonomo**. En un dron ya almacenado, la opcion elegida se persiste inmediatamente en la columna `dron.modo_control`; en un dron nuevo, el modo queda asociado al formulario y se guarda al presionar **Guardar**. Los valores admitidos son `MANUAL` y `AUTONOMO`, protegidos por una restriccion `CHECK`. Esta persistencia no agrega atributos al package `modelo`.

## Decorator

El patron Decorator agrega de forma opcional la descripcion de una bateria adicional sin agregar atributos a `Dron` ni editar el package `modelo`.

La estructura actual es:

1. `DescripcionDron`: componente abstracto del patron.
2. `DronDescripcionBase`: primera subclase concreta; representa la descripcion normal del dron.
3. `BateriaAdicionalDecorator`: segunda subclase concreta; envuelve una `DescripcionDron` y agrega la informacion de bateria adicional.

`DescripcionDron` tiene exactamente dos subclases. Cuando el CheckBox **El dron lleva bateria adicional** esta marcado, se utiliza `BateriaAdicionalDecorator`; cuando esta desmarcado se conserva `DronDescripcionBase`.

No existe un boton adicional para aplicar el decorador. Si el dron ya esta guardado, la seleccion del CheckBox se persiste inmediatamente. Si el dron es nuevo, se persiste al presionar **Guardar**. La seleccion se almacena mediante el DAO y PostgreSQL, sin modificar las clases del package `modelo`.

# Composite

El patron Composite para sensores se encuentra completamente en `servicios` y no modifica `modelo.Sensor`.

Participantes:

1. `ComponenteSensor`: componente abstracto comun.
2. `SensorCompuesto`: Composite que administra una lista de hijos mediante `agregar`, `quitar` y `getHijos`.
3. `SensorHoja`: Leaf que representa sensores terminales.
4. `CompositeSensores`: clase de apoyo que construye exactamente el arbol solicitado por la actividad.

La estructura demostrada es:

```text
Sensor General
  - Sensor Temperatura
    - Sensor Infrarrojo
    - RTD
  - Sensor Cámara
    - Sensor CMOS
    - Sensor CCD
  - Sensor Sonido
    - Sensor Analógico
    - Sensor Digital
      - SPI
      - UART
  - Sensor Inteligente
```

La interfaz JavaFX incorpora el boton **Mostrar Composite de sensores**. La jerarquia tambien queda documentada en PostgreSQL en `sensor_composite`, con una llave foranea autorreferenciada `padre_id -> sensor_composite.id`.

# Adapter

El patron Adapter transforma una instancia de `Mision` a un archivo JSON sin modificar la clase del package `modelo`.

Participantes:

1. `ExportadorMision`: Target que define `exportar(Mision, Path)`.
2. `MisionJsonAdapter`: Adapter que convierte los datos de Mision al formato JSON.
3. `ArchivoJson`: Adaptee que conoce la escritura fisica del archivo, pero no conoce Mision.

La interfaz JavaFX incorpora el boton **Exportar mision a JSON**. Se utiliza una Mision precargada y se crea `exports/mision-MIS-ADAPTER-001.json`. Adapter no necesita una tabla adicional porque el resultado exigido por la actividad es un archivo `.json`.

# Pruebas y commits de la actividad

`CompositeAdapterTest` contiene pruebas JUnit independientes para Composite y Adapter.

Para el repositorio, los cambios deben separarse en dos commits academicos:

- `ft:composite` para las clases, interfaz, pruebas, FXML y SQL relacionados con Composite.
- `ft:adapter` para las clases, interfaz, pruebas y FXML relacionados con Adapter.
