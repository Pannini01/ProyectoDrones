package com.proyecto.drones.controlador;

import java.util.Optional;
import java.util.UUID;

import com.proyecto.drones.dao.Crud;
import com.proyecto.drones.dao.DronDAO;
import com.proyecto.drones.excepciones.AplicacionException;
import com.proyecto.drones.excepciones.ValidacionException;
import com.proyecto.drones.modelo.Agricultura;
import com.proyecto.drones.modelo.Dron;
import com.proyecto.drones.modelo.TipoDron;
import com.proyecto.drones.modelo.Vigilancia;
import com.proyecto.drones.servicios.AgriculturaDronBuilder;
import com.proyecto.drones.servicios.PrototypeRegistry;
import com.proyecto.drones.servicios.VigilanciaDronBuilder;

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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la vista principal de gestión de drones.
 *
 * <p>Coordina el CRUD, construye entidades mediante Builder, administra
 * prototipos y convierte todas las excepciones controladas en alertas JavaFX.
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
    /** Clave usada para registrar o recuperar un prototipo. */
    @FXML private TextField txtClavePrototipo;
    /** Texto de estado mostrado al pie de la ventana. */
    @FXML private Label lblEstado;

    /** Contrato genérico utilizado para ejecutar el CRUD. */
    private final Crud<Dron, String> dronDAO = new DronDAO();
    /** Registro en memoria utilizado en la demostración de Prototype. */
    private final PrototypeRegistry<Dron> prototypeRegistry = new PrototypeRegistry<>();
    /** Colección observable que alimenta la tabla. */
    private final ObservableList<Dron> datos = FXCollections.observableArrayList();

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
        cmbTipo.setValue(TipoDron.AGRICULTURA);
        lblEstado.setText("Formulario listo para un nuevo dron.");
    }

    /** Construye con Builder y persiste el dron diligenciado. */
    @FXML
    private void guardar() {
        try {
            Dron dron = construirDesdeFormulario();
            dronDAO.crear(dron);
            cargarDatos();
            nuevo();
            mostrarInfo("Dron creado", "El dron fue construido con Builder y guardado correctamente.");
        } catch (AplicacionException e) {
            mostrarError(e.getMessage());
        } catch (RuntimeException e) {
            mostrarError("Ocurrio un error inesperado al guardar el dron.");
        }
    }

    /** Actualiza en PostgreSQL el dron identificado por el formulario. */
    @FXML
    private void actualizar() {
        try {
            Dron dron = construirDesdeFormulario();
            if (!dronDAO.actualizar(dron)) {
                throw new ValidacionException("El dron que intenta actualizar ya no existe.");
            }
            cargarDatos();
            mostrarInfo("Dron actualizado", "Los cambios fueron guardados correctamente.");
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
     * Registra como prototipo el dron seleccionado o el del formulario.
     */
    @FXML
    private void registrarPrototipo() {
        try {
            Dron origen = tablaDrones.getSelectionModel().getSelectedItem();
            if (origen == null) {
                origen = construirDesdeFormulario();
            }
            prototypeRegistry.registrarPrototipo(txtClavePrototipo.getText(), origen);
            lblEstado.setText("Prototipo registrado. El registro conserva una copia independiente.");
            mostrarInfo("Patron Prototype", "Prototipo registrado con la clave indicada.");
        } catch (AplicacionException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Obtiene un clon independiente y le asigna ID y serial nuevos.
     */
    @FXML
    private void clonarPrototipo() {
        try {
            Dron clon = prototypeRegistry.obtenerClon(txtClavePrototipo.getText());
            clon.setId(UUID.randomUUID().toString());
            String baseSerial = clon.getSerial().length() > 65 ? clon.getSerial().substring(0, 65) : clon.getSerial();
            clon.setSerial(baseSerial + "-COPIA-" + clon.getId().substring(0, 6).toUpperCase());
            tablaDrones.getSelectionModel().clearSelection();
            cargarFormulario(clon);
            lblEstado.setText("Prototype creo un clon independiente con ID y serial nuevos.");
            mostrarInfo("Patron Prototype", "Se creo un clon en memoria. Use Guardar para persistirlo.");
        } catch (AplicacionException e) {
            mostrarError(e.getMessage());
        }
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
            return new AgriculturaDronBuilder()
                    .setId(id)
                    .setSerial(serial)
                    .setModelo(modelo)
                    .setFabricante(fabricante)
                    .setPeso(peso)
                    .setCapacidadTanque(numeroPositivo(txtCapacidad.getText(), "capacidad del tanque"))
                    .build();
        }
        return new VigilanciaDronBuilder()
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
