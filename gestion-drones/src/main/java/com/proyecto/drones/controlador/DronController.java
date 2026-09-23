package com.proyecto.drones.controlador;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.proyecto.drones.dao.DronDAO;
import com.proyecto.drones.excepciones.AplicacionException;
import com.proyecto.drones.excepciones.ValidacionException;
import com.proyecto.drones.modelo.Agricultura;
import com.proyecto.drones.modelo.Dron;
import com.proyecto.drones.modelo.Mision;
import com.proyecto.drones.modelo.TipoDron;
import com.proyecto.drones.modelo.Vigilancia;
import com.proyecto.drones.servicios.BateriaAdicionalDecorator;
import com.proyecto.drones.servicios.ComponenteSensor;
import com.proyecto.drones.servicios.CompositeSensores;
import com.proyecto.drones.servicios.ControlDron;
import com.proyecto.drones.servicios.DescripcionDron;
import com.proyecto.drones.servicios.DronDescripcionBase;
import com.proyecto.drones.servicios.MisionJsonAdapter;
import com.proyecto.drones.servicios.ModoControl;
import com.proyecto.drones.servicios.Prototipo;
import com.proyecto.drones.servicios.DronBuilder.AgriculturaBuilder;
import com.proyecto.drones.servicios.DronBuilder.VigilanciaBuilder;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la vista principal de gestión de drones.
 *
 * <p>Coordina el CRUD, construye entidades mediante Builder, demuestra
 * Prototype mediante clonación directa y ejecuta los patrones Bridge, Decorator,
 * Composite y Adapter. La selección de batería adicional del Decorator y el modo
 * de control del Bridge se persisten sin modificar las clases del package modelo.
 * Composite construye la jerarquía académica de sensores y Adapter exporta una
 * instancia de Mision a un archivo JSON.
 * También convierte las excepciones controladas en alertas JavaFX.
 * Los métodos marcados con {@link FXML} son invocados desde
 * {@code dron-view.fxml}.</p>
 *
 * @since 1.0
 */
public class DronController {
    /** Tabla principal enlazada desde FXML. */
    @FXML private TableView<Dron> tablaDrones;
    /** Columna del identificador. */
    @FXML private TableColumn<Dron, String> colId;
    /** Columna del serial. */
    @FXML private TableColumn<Dron, String> colSerial;
    /** Columna del subtipo. */
    @FXML private TableColumn<Dron, String> colTipo;
    /** Columna del modelo. */
    @FXML private TableColumn<Dron, String> colModelo;
    /** Columna del fabricante. */
    @FXML private TableColumn<Dron, String> colFabricante;
    /** Columna del peso. */
    @FXML private TableColumn<Dron, Double> colPeso;
    /** Columna del atributo específico del subtipo. */
    @FXML private TableColumn<Dron, String> colCaracteristica;

    /** Campo del identificador. */
    @FXML private TextField txtId;
    /** Campo del serial. */
    @FXML private TextField txtSerial;
    /** Campo del modelo. */
    @FXML private TextField txtModelo;
    /** Campo del fabricante. */
    @FXML private TextField txtFabricante;
    /** Campo del peso en kilogramos. */
    @FXML private TextField txtPeso;
    /** Selector del subtipo de dron. */
    @FXML private ComboBox<TipoDron> cmbTipo;
    /** Etiqueta del atributo agrícola. */
    @FXML private Label lblCapacidad;
    /** Campo de capacidad del tanque. */
    @FXML private TextField txtCapacidad;
    /** Selección de detección térmica. */
    @FXML private CheckBox chkTermica;
    /** Selección opcional de batería adicional para la demostración Decorator. */
    @FXML private CheckBox chkBateriaAdicional;
    /** Texto de estado mostrado al pie de la ventana. */
    @FXML private Label lblEstado;

    /** DAO de drones. Persiste CRUD, Decorator y modo Bridge. */
    private final DronDAO dronDAO = new DronDAO();
    /** Colección observable que alimenta la tabla. */
    private final ObservableList<Dron> datos = FXCollections.observableArrayList();
    /** Modo Bridge asociado al formulario o al dron seleccionado. */
    private ModoControl modoControlFormulario = new ModoControl.ControlManual();

    /**
     * Configura controles, eventos, tabla y carga inicial de PostgreSQL.
     */
    @FXML
    public void initialize() {
        configurarTabla();
        cmbTipo.setItems(FXCollections.observableArrayList(TipoDron.values()));
        cmbTipo.valueProperty().addListener((observable, anterior, actual) -> actualizarCamposTipo(actual));
        tablaDrones.getSelectionModel().selectedItemProperty()
                .addListener((observable, anterior, actual) -> cargarFormulario(actual));
        nuevo();
        cargarDatos();
    }

    /** Configura las fábricas de valores y enlaza la lista observable. */
    private void configurarTabla() {
        tablaDrones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSerial.setCellValueFactory(new PropertyValueFactory<>("serial"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colFabricante.setCellValueFactory(new PropertyValueFactory<>("fabricante"));
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
        colTipo.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getTipo().toString()));
        colCaracteristica.setCellValueFactory(celda -> new SimpleStringProperty(caracteristica(celda.getValue())));
        tablaDrones.setItems(datos);
    }

    /** Limpia el formulario y genera el ID de un nuevo dron. */
    @FXML
    private void nuevo() {
        tablaDrones.getSelectionModel().clearSelection();
        txtId.setText(UUID.randomUUID().toString());
        txtSerial.clear();
        txtModelo.clear();
        txtFabricante.clear();
        txtPeso.clear();
        txtCapacidad.clear();
        chkTermica.setSelected(false);
        chkBateriaAdicional.setSelected(false);
        modoControlFormulario = new ModoControl.ControlManual();
        cmbTipo.setValue(TipoDron.AGRICULTURA);
        lblEstado.setText("Formulario listo para un nuevo dron.");
    }

    /** Construye con Builder y persiste el dron, Decorator y modo Bridge. */
    @FXML
    private void guardar() {
        try {
            Dron dron = construirDesdeFormulario();
            boolean bateriaAdicional = chkBateriaAdicional.isSelected();
            String codigoModo = modoControlFormulario.getCodigo();
            String nombreModo = modoControlFormulario.getNombre();
            dronDAO.crear(dron, bateriaAdicional, codigoModo);

            String descripcion = crearDescripcionDecorada(dron, bateriaAdicional).getDescripcion();
            cargarDatos();
            nuevo();
            mostrarInfo("Dron creado",
                    "El dron fue construido con Builder y guardado correctamente.\n\n"
                    + "Bridge: " + nombreModo + "\n"
                    + "Decorator: " + descripcion);
        } catch (AplicacionException e) {
            mostrarError(e.getMessage());
        } catch (RuntimeException e) {
            mostrarError("Ocurrio un error inesperado al guardar el dron.");
        }
    }

    /** Actualiza en PostgreSQL el dron, Decorator y modo Bridge. */
    @FXML
    private void actualizar() {
        try {
            Dron dron = construirDesdeFormulario();
            boolean bateriaAdicional = chkBateriaAdicional.isSelected();
            String codigoModo = modoControlFormulario.getCodigo();
            String nombreModo = modoControlFormulario.getNombre();
            if (!dronDAO.actualizar(dron, bateriaAdicional, codigoModo)) {
                throw new ValidacionException("El dron que intenta actualizar ya no existe.");
            }
            cargarDatos();
            String descripcion = crearDescripcionDecorada(dron, bateriaAdicional).getDescripcion();
            mostrarInfo("Dron actualizado",
                    "Los cambios fueron guardados correctamente.\n\n"
                    + "Bridge: " + nombreModo + "\n"
                    + "Decorator: " + descripcion);
        } catch (AplicacionException e) {
            mostrarError(e.getMessage());
        } catch (RuntimeException e) {
            mostrarError("Ocurrio un error inesperado al actualizar el dron.");
        }
    }

    /** Solicita confirmación y elimina el dron seleccionado. */
    @FXML
    private void eliminar() {
        Dron seleccionado = tablaDrones.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Seleccione en la tabla el dron que desea eliminar.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminacion");
        confirmacion.setHeaderText("Eliminar dron " + seleccionado.getSerial());
        confirmacion.setContentText("Esta operacion eliminara el registro de PostgreSQL.");
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }
        try {
            if (!dronDAO.eliminar(seleccionado.getId())) {
                throw new ValidacionException("El dron seleccionado ya no existe.");
            }
            cargarDatos();
            nuevo();
            mostrarInfo("Dron eliminado", "El registro fue eliminado correctamente.");
        } catch (AplicacionException e) {
            mostrarError(e.getMessage());
        } catch (RuntimeException e) {
            mostrarError("Ocurrio un error inesperado al eliminar el dron.");
        }
    }

    /** Busca un dron por el ID escrito y lo carga en el formulario. */
    @FXML
    private void buscar() {
        try {
            String id = obligatorio(txtId.getText(), "Digite el ID que desea buscar.");
            Optional<Dron> encontrado = dronDAO.buscarPorId(id);
            if (encontrado.isEmpty()) {
                throw new ValidacionException("No se encontro un dron con ese ID.");
            }
            cargarFormulario(encontrado.get());
            tablaDrones.getSelectionModel().select(encontrado.get());
            lblEstado.setText("Dron encontrado por ID.");
        } catch (AplicacionException e) {
            mostrarError(e.getMessage());
        } catch (RuntimeException e) {
            mostrarError("Ocurrio un error inesperado durante la busqueda.");
        }
    }

    /**
     * Construye un ejemplo del tipo seleccionado y demuestra Builder sin
     * persistirlo automáticamente.
     */
    @FXML
    private void demostrarBuilder() {
        try {
            TipoDron tipo = cmbTipo.getValue() == null ? TipoDron.AGRICULTURA : cmbTipo.getValue();
            txtId.setText(UUID.randomUUID().toString());
            String sufijo = txtId.getText().substring(0, 6).toUpperCase();
            txtSerial.setText(tipo == TipoDron.AGRICULTURA ? "AG-" + sufijo : "VG-" + sufijo);
            txtModelo.setText(tipo == TipoDron.AGRICULTURA ? "Agro Builder X1" : "Guardian Builder T1");
            txtFabricante.setText("AeroTech");
            txtPeso.setText(tipo == TipoDron.AGRICULTURA ? "12.5" : "7.8");
            txtCapacidad.setText("24");
            chkTermica.setSelected(true);
            Dron construido = construirDesdeFormulario();
            cargarFormulario(construido);
            lblEstado.setText("Builder demostro la construccion paso a paso de un " + tipo + ".");
            mostrarInfo("Patron Builder", "Se construyo un objeto " + tipo
                    + " en memoria. Use Guardar para persistirlo.");
        } catch (AplicacionException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Clona directamente el dron seleccionado o, si no existe selección, el
     * dron diligenciado en el formulario. No requiere registrar previamente
     * un prototipo.
     *
     * <p>La copia se genera en memoria mediante Prototipo, servicio que
     * implementa el contrato Prototype sin acoplar el paquete modelo al patron. Después se asignan un ID y un serial nuevos para evitar
     * colisiones si el usuario decide persistirla.</p>
     */
    @FXML
    private void clonarPrototipo() {
        try {
            Dron origen = obtenerDronParaDemostracion();
            Dron clon = new Prototipo(origen).clone();

            clon.setId(UUID.randomUUID().toString());
            String serialOrigen = clon.getSerial();
            String baseSerial = serialOrigen.length() > 65
                    ? serialOrigen.substring(0, 65)
                    : serialOrigen;
            clon.setSerial(baseSerial + "-COPIA-"
                    + clon.getId().substring(0, 6).toUpperCase());

            tablaDrones.getSelectionModel().clearSelection();
            cargarFormulario(clon);
            lblEstado.setText("Prototype creo un clon desde servicios sin registro previo.");
            mostrarInfo("Patron Prototype",
                    "Se creo un clon independiente directamente desde el dron seleccionado "
                    + "o desde el formulario. No se requiere registrar un prototipo. "
                    + "Use Guardar si desea persistir la copia.");
        } catch (AplicacionException e) {
            mostrarError(e.getMessage());
        } catch (RuntimeException e) {
            mostrarError("Ocurrio un error inesperado al clonar el dron.");
        }
    }

    /**
     * Demuestra el patrón Bridge utilizando el modo de control manual sobre
     * el dron seleccionado o el diligenciado en el formulario.
     */
    @FXML
    private void demostrarControlManual() {
        demostrarBridge(new ModoControl.ControlManual());
    }

    /**
     * Demuestra el patrón Bridge utilizando la implementación de control
     * autónomo sobre el dron seleccionado o el diligenciado en el formulario.
     */
    @FXML
    private void demostrarControlAutonomo() {
        demostrarBridge(new ModoControl.ControlAutonomo());
    }

    /**
     * Ejecuta la abstracción del Bridge y conserva la selección para persistirla.
     *
     * <p>Si existe un dron seleccionado y ya almacenado, el modo se guarda de
     * inmediato en PostgreSQL. Si se trabaja con un dron nuevo, la selección
     * queda asociada al formulario y se guarda al presionar Guardar. El estado
     * se persiste fuera del package modelo.</p>
     *
     * @param modoControl modo manual o autónomo
     */
    private void demostrarBridge(ModoControl modoControl) {
        try {
            Dron seleccionado = tablaDrones.getSelectionModel().getSelectedItem();
            Dron dron = seleccionado != null ? seleccionado : construirDesdeFormulario();

            String persistencia;
            if (seleccionado != null) {
                if (!dronDAO.actualizarModoControl(seleccionado.getId(), modoControl.getCodigo())) {
                    throw new ValidacionException("El dron seleccionado ya no existe.");
                }
                modoControlFormulario = modoControl;
                persistencia = "El modo fue guardado inmediatamente en PostgreSQL.";
            } else {
                modoControlFormulario = modoControl;
                persistencia = "El modo se guardara en PostgreSQL al presionar Guardar.";
            }

            ControlDron control = new ControlDron(dron, modoControl);
            String resultado = control.ejecutarControl();
            lblEstado.setText("Bridge: " + modoControl.getNombre() + " para el dron "
                    + dron.getSerial() + ". " + persistencia);
            mostrarInfo("Patron Bridge - " + modoControl.getNombre(),
                    resultado + "\n\n" + persistencia
                    + "\nLa implementacion se mantiene separada del objeto Dron.");
        } catch (AplicacionException e) {
            mostrarError(e.getMessage());
        } catch (RuntimeException e) {
            mostrarError("Ocurrio un error inesperado durante la demostracion de Bridge.");
        }
    }

    /**
     * Guarda automáticamente la selección de batería adicional cuando el
     * usuario marca o desmarca el CheckBox.
     *
     * <p>Si el dron ya existe en PostgreSQL, el cambio se persiste de inmediato.
     * Si se está diligenciando un dron nuevo, la selección queda en el formulario
     * y se persiste junto con el dron al presionar Guardar. No existe un botón
     * adicional para aplicar el Decorator.</p>
     */
    @FXML
    private void cambiarBateriaAdicional() {
        boolean incluirBateria = chkBateriaAdicional.isSelected();
        Dron seleccionado = tablaDrones.getSelectionModel().getSelectedItem();

        try {
            if (seleccionado != null) {
                if (!dronDAO.actualizarBateriaAdicional(seleccionado.getId(), incluirBateria)) {
                    throw new ValidacionException("El dron seleccionado ya no existe.");
                }

                String descripcion = crearDescripcionDecorada(seleccionado, incluirBateria).getDescripcion();
                lblEstado.setText("Decorator guardo bateria adicional: "
                        + (incluirBateria ? "SI" : "NO")
                        + " para el dron " + seleccionado.getSerial()
                        + ". " + descripcion);
                return;
            }

            lblEstado.setText("Bateria adicional: " + (incluirBateria ? "SI" : "NO")
                    + ". La seleccion se guardara al crear el dron.");
        } catch (AplicacionException e) {
            mostrarError(e.getMessage());
            chkBateriaAdicional.setSelected(!incluirBateria);
        } catch (RuntimeException e) {
            mostrarError("Ocurrio un error inesperado al guardar la seleccion de bateria adicional.");
            chkBateriaAdicional.setSelected(!incluirBateria);
        }
    }

    /**
     * Demuestra el patrón Composite construyendo exactamente la jerarquía de
     * sensores solicitada en la actividad.
     */
    @FXML
    private void demostrarComposite() {
        try {
            ComponenteSensor sensorGeneral = CompositeSensores.crearEstructuraRequerida();
            String estructura = sensorGeneral.mostrarEstructura();
            lblEstado.setText("Composite construyo " + sensorGeneral.contarComponentes()
                    + " componentes de sensores en memoria.");
            mostrarTextoLargo("Patron Composite - sensores",
                    estructura + "\n\nTotal de componentes: "
                    + sensorGeneral.contarComponentes());
        } catch (RuntimeException e) {
            mostrarError("Ocurrio un error inesperado durante la demostracion de Composite.");
        }
    }

    /**
     * Demuestra el patrón Adapter exportando una instancia precargada de
     * {@link Mision} a un archivo JSON dentro de la carpeta exports.
     */
    @FXML
    private void demostrarAdapter() {
        try {
            Mision mision = crearMisionAdapterEjemplo();
            Path archivo = new MisionJsonAdapter().exportar(mision, Path.of("exports"));
            lblEstado.setText("Adapter creo el archivo JSON de la mision " + mision.getId() + ".");
            mostrarInfo("Patron Adapter - Mision a JSON",
                    "La mision precargada fue adaptada correctamente.\n\n"
                    + "Mision: " + mision.getNombre() + "\n"
                    + "Archivo creado en:\n" + archivo);
        } catch (IOException | IllegalArgumentException e) {
            mostrarError("No fue posible crear el archivo JSON: " + e.getMessage());
        } catch (RuntimeException e) {
            mostrarError("Ocurrio un error inesperado durante la demostracion de Adapter.");
        }
    }

    /**
     * Crea la instancia de Mision usada por el boton de demostracion Adapter.
     * Los datos principales son precargados y, si existe un dron seleccionado,
     * se incluye como referencia dentro del JSON exportado.
     *
     * @return mision de demostracion completamente diligenciada
     */
    private Mision crearMisionAdapterEjemplo() {
        Mision mision = new Mision();
        mision.setId("MIS-ADAPTER-001");
        mision.setNombre("Inspeccion de infraestructura");
        mision.setUbicacion("Bogota D.C.");
        mision.setFecha(LocalDate.of(2026, 9, 23));

        Dron seleccionado = tablaDrones.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            mision.setDrones(List.of(seleccionado));
        }
        return mision;
    }

    /**
     * Crea la descripción del dron aplicando el Decorator solamente cuando la
     * batería adicional está seleccionada.
     *
     * @param dron entidad que se describe
     * @param incluirBateria estado seleccionado en la interfaz
     * @return componente base o decorado según la selección
     */
    private DescripcionDron crearDescripcionDecorada(Dron dron, boolean incluirBateria) {
        DescripcionDron base = new DronDescripcionBase(dron);
        return BateriaAdicionalDecorator.aplicarSi(base, incluirBateria);
    }

    /**
     * Obtiene una entidad válida para las demostraciones académicas.
     *
     * <p>Se prioriza la selección actual de la tabla. Si no existe selección,
     * se construye temporalmente el formulario mediante el Builder existente.</p>
     *
     * @return dron disponible para ejecutar un patrón en memoria
     * @throws ValidacionException si el formulario debe utilizarse y es inválido
     */
    private Dron obtenerDronParaDemostracion() throws ValidacionException {
        Dron seleccionado = tablaDrones.getSelectionModel().getSelectedItem();
        return seleccionado != null ? seleccionado : construirDesdeFormulario();
    }

    /**
     * Lee y valida los controles y utiliza el Builder del tipo seleccionado.
     *
     * @return dron construido en memoria
     * @throws ValidacionException si algún control contiene datos inválidos
     */
    private Dron construirDesdeFormulario() throws ValidacionException {
        TipoDron tipo = cmbTipo.getValue();
        if (tipo == null) {
            throw new ValidacionException("Seleccione el tipo de dron.");
        }
        String id = obligatorio(txtId.getText(), "El ID es obligatorio.");
        String serial = obligatorio(txtSerial.getText(), "El serial es obligatorio.");
        String modelo = obligatorio(txtModelo.getText(), "El modelo es obligatorio.");
        String fabricante = obligatorio(txtFabricante.getText(), "El fabricante es obligatorio.");
        double peso = numeroPositivo(txtPeso.getText(), "peso");

        if (tipo == TipoDron.AGRICULTURA) {
            return new AgriculturaBuilder()
                    .setId(id)
                    .setSerial(serial)
                    .setModelo(modelo)
                    .setFabricante(fabricante)
                    .setPeso(peso)
                    .setCapacidadTanque(numeroPositivo(txtCapacidad.getText(), "capacidad del tanque"))
                    .build();
        }
        return new VigilanciaBuilder()
                .setId(id)
                .setSerial(serial)
                .setModelo(modelo)
                .setFabricante(fabricante)
                .setPeso(peso)
                .setDeteccionTermica(chkTermica.isSelected())
                .build();
    }

    /** Carga desde PostgreSQL la lista observable de la tabla. */
    private void cargarDatos() {
        try {
            datos.setAll(dronDAO.listar());
            lblEstado.setText(datos.size() + " dron(es) cargado(s) desde PostgreSQL.");
        } catch (AplicacionException e) {
            datos.clear();
            mostrarError(e.getMessage());
        }
    }

    /**
     * Muestra en los controles los datos de una entidad.
     *
     * @param dron entidad que se mostrará; si es nula no realiza cambios
     */
    private void cargarFormulario(Dron dron) {
        if (dron == null) {
            return;
        }
        txtId.setText(dron.getId());
        txtSerial.setText(dron.getSerial());
        txtModelo.setText(dron.getModelo());
        txtFabricante.setText(dron.getFabricante());
        txtPeso.setText(String.valueOf(dron.getPeso()));
        cmbTipo.setValue(dron.getTipo());
        if (dron instanceof Agricultura agricultura) {
            txtCapacidad.setText(String.valueOf(agricultura.getCapacidadTanque()));
        } else if (dron instanceof Vigilancia vigilancia) {
            chkTermica.setSelected(vigilancia.isDeteccionTermica());
        }

        try {
            chkBateriaAdicional.setSelected(dronDAO.tieneBateriaAdicional(dron.getId()));
            modoControlFormulario = ModoControl.desdeCodigo(
                    dronDAO.obtenerModoControl(dron.getId()));
        } catch (AplicacionException | IllegalArgumentException e) {
            chkBateriaAdicional.setSelected(false);
            modoControlFormulario = new ModoControl.ControlManual();
            mostrarError(e.getMessage());
        }
    }

    /**
     * Alterna el campo específico visible según el subtipo.
     *
     * @param tipo tipo seleccionado en el ComboBox
     */
    private void actualizarCamposTipo(TipoDron tipo) {
        boolean agricultura = tipo == TipoDron.AGRICULTURA;
        lblCapacidad.setVisible(agricultura);
        lblCapacidad.setManaged(agricultura);
        txtCapacidad.setVisible(agricultura);
        txtCapacidad.setManaged(agricultura);
        chkTermica.setVisible(!agricultura);
        chkTermica.setManaged(!agricultura);
    }

    /**
     * Crea el texto de la columna correspondiente al atributo específico.
     *
     * @param dron fila de la tabla
     * @return capacidad en litros o disponibilidad térmica
     */
    private String caracteristica(Dron dron) {
        if (dron instanceof Agricultura agricultura) {
            return agricultura.getCapacidadTanque() + " L";
        }
        return ((Vigilancia) dron).isDeteccionTermica() ? "Termica: si" : "Termica: no";
    }

    /**
     * Valida que una cadena no esté vacía.
     *
     * @param valor texto que se comprobará
     * @param mensaje mensaje presentado cuando la regla se incumple
     * @return texto sin espacios en los extremos
     * @throws ValidacionException si el valor es nulo o vacío
     */
    private String obligatorio(String valor, String mensaje) throws ValidacionException {
        if (valor == null || valor.isBlank()) {
            throw new ValidacionException(mensaje);
        }
        return valor.trim();
    }

    /**
     * Convierte un texto en número decimal positivo y admite coma decimal.
     *
     * @param valor texto introducido por el usuario
     * @param nombre nombre visible del campo
     * @return número validado
     * @throws ValidacionException si el texto no es numérico o no es positivo
     */
    private double numeroPositivo(String valor, String nombre) throws ValidacionException {
        try {
            double numero = Double.parseDouble(obligatorio(valor, "El campo " + nombre + " es obligatorio.")
                    .replace(',', '.'));
            if (numero <= 0) {
                throw new ValidacionException("El campo " + nombre + " debe ser mayor que cero.");
            }
            return numero;
        } catch (NumberFormatException e) {
            throw new ValidacionException("El campo " + nombre + " debe contener un numero valido.");
        }
    }

    /**
     * Presenta texto extenso en un area desplazable, util para visualizar el
     * arbol Composite sin superponer o recortar su estructura.
     *
     * @param titulo titulo de la ventana
     * @param contenido texto que se mostrara
     */
    private void mostrarTextoLargo(String titulo, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);

        TextArea area = new TextArea(contenido);
        area.setEditable(false);
        area.setWrapText(false);
        area.setPrefColumnCount(42);
        area.setPrefRowCount(18);
        area.setStyle("-fx-font-family: monospace;");

        alerta.getDialogPane().setContent(area);
        alerta.setResizable(true);
        alerta.showAndWait();
    }

    /**
     * Presenta una alerta informativa.
     *
     * @param titulo título de la ventana
     * @param mensaje contenido para el usuario
     */
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Actualiza el estado y presenta una alerta de error.
     *
     * @param mensaje descripción comprensible del problema
     */
    private void mostrarError(String mensaje) {
        lblEstado.setText(mensaje);
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText("La operacion no pudo completarse");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
