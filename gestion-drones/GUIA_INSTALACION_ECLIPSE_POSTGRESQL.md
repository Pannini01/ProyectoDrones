# Guía completa de instalación y ejecución

Esta guía explica cómo descomprimir, importar y ejecutar el proyecto en Eclipse,
crear la base de datos PostgreSQL y comprobar que el CRUD funciona.

## 1. Programas requeridos

Instale antes de abrir el proyecto:

1. **JDK 17**. Debe ser un JDK completo y no solamente un JRE.
2. **Eclipse IDE for Java Developers** con soporte Maven `m2e`.
3. **PostgreSQL** y **pgAdmin 4**.
4. Maven externo es opcional para Eclipse, pero necesario para usar comandos
   `mvn` desde PowerShell o CMD.

Compruebe el JDK desde una terminal:

```text
java -version
```

La respuesta debe indicar una versión 17. Si instaló Maven externo, compruébelo
con `mvn -version`.

## 2. Descomprimir el proyecto

1. Descargue `ProyectoDrones-JavaFX-Maven.zip`.
2. Haga clic derecho y seleccione **Extraer todo**.
3. Elija una ruta sencilla, por ejemplo `C:\Proyectos`.
4. La carpeta que debe importar es `C:\Proyectos\proyecto-drones`.
5. Confirme que dentro de ella se encuentran `pom.xml`, `.env`, `src`,
   `database` y `README.md`.

No importe directamente el archivo ZIP y no seleccione la carpeta superior que
solo contiene `proyecto-drones`.

## 3. Configurar JDK 17 en Eclipse

1. Abra Eclipse.
2. Vaya a **Window > Preferences > Java > Installed JREs**.
3. Pulse **Add... > Standard VM > Next**.
4. En **JRE home** seleccione la carpeta del JDK 17, por ejemplo
   `C:\Program Files\Eclipse Adoptium\jdk-17...`.
5. Pulse **Finish**, marque el JDK 17 como predeterminado y seleccione
   **Apply and Close**.

Si el JDK 17 ya aparece seleccionado, no necesita agregarlo nuevamente.

## 4. Importar el proyecto Maven

1. En Eclipse seleccione **File > Import...**.
2. Abra **Maven > Existing Maven Projects** y pulse **Next**.
3. En **Root Directory** seleccione la carpeta `proyecto-drones`.
4. Eclipse debe encontrar `/pom.xml` y marcarlo automáticamente.
5. Pulse **Finish**.
6. Espere mientras Maven descarga JavaFX, JUnit y el controlador PostgreSQL.
7. Haga clic derecho sobre el proyecto y seleccione
   **Maven > Update Project...**.
8. Marque **Force Update of Snapshots/Releases** y pulse **OK**.
9. Verifique que el proyecto use `JavaSE-17` en **JRE System Library**.

Si aparecen errores por la versión de Java:

1. Abra **Project > Properties > Java Build Path > Libraries**.
2. Edite **JRE System Library** y seleccione **Workspace default JRE (JDK 17)**.
3. En **Java Compiler**, seleccione nivel de cumplimiento `17`.

## 5. Crear la base de datos con pgAdmin

### Opción gráfica recomendada

1. Inicie el servicio de PostgreSQL y abra pgAdmin 4.
2. Expanda **Servers** y conéctese a su servidor PostgreSQL.
3. Escriba la contraseña que definió al instalar PostgreSQL.
4. Haga clic derecho en **Databases** y seleccione
   **Create > Database...**.
5. Escriba `drones_db` en **Database**.
6. Seleccione `postgres` en **Owner** y pulse **Save**.

### Opción mediante SQL

1. Seleccione la base predeterminada `postgres`.
2. Abra **Tools > Query Tool**.
3. Abra `database/00_crear_base.sql` y ejecútelo con el botón ▶.

Utilice solo una de las dos opciones. Si `drones_db` ya existe, no vuelva a
ejecutar el script de creación.

## 6. Crear las tablas

1. En pgAdmin expanda **Databases** y seleccione `drones_db`.
2. Confirme en la barra superior del Query Tool que la conexión actual sea
   `drones_db`, no la base `postgres`.
3. Abra **Tools > Query Tool**.
4. Pulse el botón para abrir archivo y seleccione
   `proyecto-drones/database/schema.sql`.
5. Ejecute todo el archivo con el botón ▶.
6. Actualice **Schemas > public > Tables**.

Deben aparecer estas tablas:

- `dron`
- `piloto`
- `sensor`
- `mision`
- `mision_dron`

Para comprobar la instalación, ejecute `database/99_verificar.sql` conectado a
`drones_db`. La consulta de `dron` debe devolver cero filas inicialmente, sin
mostrar errores.

## 7. Configurar el archivo .env

Abra `.env`, ubicado junto a `pom.xml`, y escriba los datos reales de su
instalación:

```dotenv
DB_URL=jdbc:postgresql://localhost:5432/drones_db
DB_USER=postgres
DB_PASSWORD=SU_CONTRASEÑA_DE_POSTGRESQL
```

Consideraciones:

- Sustituya `SU_CONTRASEÑA_DE_POSTGRESQL` por la contraseña real.
- No agregue espacios antes o después del signo `=`.
- Si PostgreSQL usa otro puerto, reemplace `5432` en la URL.
- No cambie `jdbc:postgresql://`.
- Guarde el archivo con el nombre exacto `.env`, sin extensión `.txt`.
- La configuración debe permanecer en la raíz del proyecto.

Para consultar el puerto en pgAdmin, haga clic derecho sobre el servidor,
seleccione **Properties > Connection** y revise **Port**.

## 8. Ejecutar la aplicación

### Desde Eclipse como aplicación Java

1. Abra `src/main/java/com/proyecto/drones/Launcher.java`.
2. Haga clic derecho dentro del editor.
3. Seleccione **Run As > Java Application**.
4. Debe abrirse la ventana **Gestión de drones**.

### Alternativa recomendada si JavaFX no inicia

1. Haga clic derecho sobre el proyecto.
2. Seleccione **Run As > Maven build...**.
3. En **Goals** escriba `clean javafx:run`.
4. Pulse **Run**.

La aplicación muestra una tabla vacía cuando la conexión funciona y todavía no
se han guardado drones. Una tabla vacía no representa un error.

## 9. Probar el CRUD

1. Pulse **Nuevo**.
2. Seleccione `Agricultura`.
3. Complete serial, modelo, fabricante, peso y capacidad de tanque.
4. Pulse **Guardar** y confirme que el dron aparece en la tabla.
5. Seleccione la fila, modifique un dato y pulse **Actualizar**.
6. Copie el ID y pruebe el botón **Buscar**.
7. Seleccione el registro y pulse **Eliminar**.

Repita la prueba con `Vigilancia` y marque o desmarque la detección térmica.

## 10. Probar Builder y Prototype

### Builder

1. Seleccione Agricultura o Vigilancia.
2. Pulse **Construir ejemplo con Builder**.
3. Revise los datos creados en el formulario.
4. Pulse **Guardar** para persistir el objeto.

### Prototype

1. Seleccione un dron guardado en la tabla.
2. Escriba una clave, por ejemplo `vigilancia-base`.
3. Pulse **Registrar prototipo**.
4. Pulse **Clonar** utilizando la misma clave.
5. El clon aparecerá con ID y serial nuevos.
6. Pulse **Guardar** si desea persistirlo.

## 11. Generar la página web Javadoc

En Windows ejecute `generar-javadoc.bat`, o desde Eclipse:

1. Haga clic derecho en el proyecto.
2. Seleccione **Run As > Maven build...**.
3. Escriba `clean javadoc:javadoc` en **Goals**.
4. Pulse **Run**.
5. Abra `target/site/apidocs/index.html` con el navegador.

## 12. Errores frecuentes

### No se encontró el archivo .env

Confirme que `.env` esté en la misma carpeta que `pom.xml` y que la carpeta de
trabajo de la configuración de ejecución sea la raíz del proyecto.

### No fue posible conectar con PostgreSQL

Revise que el servicio esté iniciado y que host, puerto, usuario, contraseña y
nombre de la base coincidan con `.env`.

### No existe la tabla dron

`schema.sql` fue ejecutado en otra base. Ejecútelo nuevamente verificando que el
Query Tool esté conectado a `drones_db`.

### Ya existe un dron con ese ID o serial

El serial es único. Pulse **Nuevo** y utilice un serial diferente.

### JavaFX runtime components are missing

Ejecute la aplicación mediante el objetivo Maven `clean javafx:run` y confirme
que Eclipse haya actualizado correctamente las dependencias del `pom.xml`.

### Error de carga en dron-view.fxml, línea 91

La versión corregida configura la política de ajuste de columnas desde
`DronController` y no desde FXML. Si Eclipse conserva una copia anterior,
ejecute **Project > Clean...**, después **Maven > Update Project...** y vuelva a
iniciar la aplicación.
