# Gestión de drones — JavaFX, Maven y PostgreSQL

Aplicación de escritorio importable en Eclipse con CRUD completo para `Dron`,
interfaz CRUD genérica, FXML, PostgreSQL, manejo visual de excepciones y
demostraciones de los patrones Builder y Prototype.

Para una instalación detallada desde cero consulte
`GUIA_INSTALACION_ECLIPSE_POSTGRESQL.md`.

## Requisitos

- JDK 17.
- Eclipse IDE for Java Developers con Maven (m2e).
- Maven 3.9 o superior si se ejecuta por terminal.
- PostgreSQL 14 o superior.

## 1. Crear y configurar la base de datos

Desde `psql` o pgAdmin cree la base:

```sql
CREATE DATABASE drones_db;
```

Conéctese a `drones_db` y ejecute `database/schema.sql`.

Edite el archivo `.env` ubicado en la raíz del proyecto:

```dotenv
DB_URL=jdbc:postgresql://localhost:5432/drones_db
DB_USER=postgres
DB_PASSWORD=su_clave_real
```

La URL, el usuario y la contraseña no están escritos en las clases Java. El
archivo `.env` está excluido de Git mediante `.gitignore`; `.env.example` sirve
como plantilla.

## 2. Importar en Eclipse

1. Abra **File > Import...**.
2. Seleccione **Maven > Existing Maven Projects**.
3. Elija la carpeta `proyecto-drones` y confirme el `pom.xml`.
4. Espere a que Eclipse descargue las dependencias.
5. Si es necesario: clic derecho al proyecto, **Maven > Update Project**.
6. Ejecute `com.proyecto.drones.Launcher` como **Java Application**.

También puede ejecutar desde terminal, situado en la raíz del proyecto:

```bash
mvn clean test
mvn javafx:run
```

## Generar la documentación Javadoc como página web

Todo el código Java incluye comentarios Javadoc y cada paquete posee un archivo
`package-info.java` con su descripción general. Para exportar la documentación
HTML, sitúese en la raíz del proyecto y ejecute:

```bash
mvn clean javadoc:javadoc
```

Abra en el navegador el archivo:

```text
target/site/apidocs/index.html
```

En Windows también puede ejecutar `generar-javadoc.bat`; el script genera la
página y abre automáticamente `index.html`. En Linux o macOS puede utilizar
`./generar-javadoc.sh`.

La página generada contiene el índice de paquetes, jerarquía de clases,
interfaces, constructores, métodos, parámetros, valores de retorno y
excepciones. La configuración está en `maven-javadoc-plugin` dentro de
`pom.xml`; se usa UTF-8 y se documentan también los miembros privados para que
pueda estudiarse el controlador FXML completo.

## 3. Probar el CRUD

1. Pulse **Nuevo** y elija Agricultura o Vigilancia.
2. Complete serial, modelo, fabricante, peso y el atributo específico.
3. Pulse **Guardar**. La creación utiliza el Builder correspondiente.
4. Seleccione un registro de la tabla para **Actualizar** o **Eliminar**.
5. Para buscar, escriba un ID en el campo superior y pulse **Buscar**.

Todas las excepciones controladas llegan a `DronController` y aparecen en una
alerta JavaFX; no se utiliza `printStackTrace` ni se muestran errores por consola.

## 4. Demostrar Builder

- Seleccione el tipo de dron.
- Pulse **Construir ejemplo con Builder**.
- La vista crea en memoria una instancia mediante
  `AgriculturaDronBuilder` o `VigilanciaDronBuilder` y carga el resultado en el
  formulario.
- Pulse **Guardar** si desea persistir el resultado.

El contrato `DronBuilder<T extends Dron>` es genérico y ambas implementaciones
permiten construcción fluida paso a paso.

## 5. Demostrar Prototype

1. Seleccione un dron de la tabla o complete un dron válido en el formulario.
2. Escriba una clave, por ejemplo `termico-base`.
3. Pulse **Registrar prototipo**.
4. Con la misma clave, pulse **Clonar**.
5. Se crea una copia profunda independiente con ID y serial nuevos. Pulse
   **Guardar** para persistirla.

`PrototypeRegistry<T extends Prototipo<T>>` guarda una copia y entrega otra
copia, por lo que nunca expone el prototipo original.

## Estructura

```text
src/main/java/com/proyecto/drones/
├── controlador/   DronController
├── dao/           Crud<T,ID>, DronDAO
├── excepciones/   Excepciones de aplicación
├── modelo/        Dron, Agricultura, Vigilancia, Piloto, Sensor, Mision
└── servicios/     Builder, Prototype, Factory y conexión Singleton
```

El diagrama corregido está en `docs/diagrama-clases.puml` y también existe una
versión Mermaid en `docs/diagrama-clases.md`. `DronDAO` posee conexiones UML
directas a las clases que realmente usa, incluso cuando están en otros paquetes.

## Decisiones de diseño corregidas

- Se eliminó el Singleton genérico duplicado; solo permanece
  `PostgresConnection`.
- `Dron` es abstracta y sus subtipos son `Agricultura` y `Vigilancia`.
- Builder admite ambos subtipos, no únicamente Agricultura.
- Prototype es genérico y funciona con cualquier subtipo de Dron.
- `Mision.fecha` utiliza `LocalDate` en vez de `String`.
- El CRUD contiene las cinco operaciones y dos parámetros genéricos.
- Cada conexión JDBC se cierra; el Singleton centraliza la configuración, no
  mantiene una conexión global que pueda quedar inválida.
