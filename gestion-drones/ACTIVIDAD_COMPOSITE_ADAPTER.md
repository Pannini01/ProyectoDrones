# Actividad - Composite y Adapter

## 1. Composite de sensores

El patron se implementa en `com.proyecto.drones.servicios` sin modificar el package `modelo`.

### Clases

- `ComponenteSensor`: componente abstracto. Define `getNombre()`, `mostrarEstructura()`, `contarComponentes()` y `esCompuesto()`.
- `SensorCompuesto`: subclase Composite. Mantiene `List<ComponenteSensor>` y permite `agregar`, `quitar` y consultar hijos.
- `SensorHoja`: subclase Leaf. Representa sensores que no contienen otros sensores.
- `CompositeSensores`: clase de apoyo que construye exactamente la jerarquia requerida.

### Jerarquia demostrada

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

En JavaFX se agrego el boton `Mostrar Composite de sensores`, conectado a `DronController.demostrarComposite()`.

## 2. Adapter de Mision a JSON

El patron tambien se encuentra en `com.proyecto.drones.servicios` y no modifica `modelo.Mision`.

### Clases

- `ExportadorMision`: Target del Adapter. Define `exportar(Mision, Path)`.
- `MisionJsonAdapter`: Adapter. Convierte la instancia de `Mision` a JSON.
- `ArchivoJson`: Adaptee. Recibe texto JSON y lo escribe en disco.

`DronController.demostrarAdapter()` crea una Mision precargada:

- ID: `MIS-ADAPTER-001`
- Nombre: `Inspeccion de infraestructura`
- Ubicacion: `Bogota D.C.`
- Fecha: `2026-09-23`

El boton `Exportar mision a JSON` crea el archivo:

```text
exports/mision-MIS-ADAPTER-001.json
```

Si hay un dron seleccionado en la tabla, se incluye tambien en el arreglo `drones` del JSON.

## 3. Base de datos

Se agrego `sensor_composite` como evidencia persistida de la jerarquia requerida. La tabla utiliza una llave foranea autorreferenciada:

```text
sensor_composite.padre_id -> sensor_composite.id
```

El script para actualizar una base existente es:

```text
database/actualizar_composite_adapter.sql
```

Adapter no requiere una tabla nueva porque el resultado solicitado es un archivo JSON.

## 4. Pruebas JUnit

Se agrego:

```text
src/test/java/com/proyecto/drones/servicios/CompositeTest.java
src/test/java/com/proyecto/drones/servicios/AdapterTest.java
```

CompositeTest verifica la jerarquia de 13 componentes y el comportamiento Leaf/Composite. AdapterTest verifica la creacion real del archivo JSON.

## 5. Commits solicitados

Para cumplir el enunciado, se recomienda separar los cambios al subirlos al repositorio:

```text
ft:composite
ft:adapter
```

Primero haga commit de las clases y pruebas de Composite (incluido el SQL relacionado). Luego haga commit de las clases, prueba y controles JavaFX de Adapter.
