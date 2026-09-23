package com.proyecto.drones;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Aplicación principal JavaFX del sistema de gestión de drones.
 *
 * <p>Carga la vista FXML, aplica la hoja de estilos y crea la ventana
 * principal. Cualquier error de carga se comunica mediante una alerta.</p>
 *
 * @author Juan Sebastian Almonacid
 * @version 1.0
 * @since 1.0
 */
public class App extends Application {

    /**
     * Inicializa y muestra la ventana principal.
     *
     * @param stage escenario primario proporcionado por JavaFX
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/dron-view.fxml"));
            Scene scene = new Scene(loader.load(), 1180, 720);
            scene.getStylesheets().add(App.class.getResource("/css/app.css").toExternalForm());
            stage.setTitle("Gestion de drones");
            stage.setMinWidth(1000);
            stage.setMinHeight(650);
            stage.setScene(scene);
            stage.show();
        } catch (IOException | RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No fue posible iniciar");
            alert.setHeaderText("Error al cargar la aplicacion");
            alert.setContentText(obtenerMensajeDetallado(e));
            alert.showAndWait();
        }
    }

    /**
     * Recorre la cadena de causas para mostrar el mensaje técnico más útil.
     *
     * @param error excepción capturada durante el arranque
     * @return mensaje de la causa raíz o un texto de respaldo
     */
    private String obtenerMensajeDetallado(Throwable error) {
        Throwable causa = error;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        String mensaje = causa.getMessage();
        return mensaje == null || mensaje.isBlank()
                ? "Revise la configuracion del proyecto."
                : mensaje;
    }

    /**
     * Inicia el ciclo de vida de JavaFX.
     *
     * @param args argumentos recibidos desde la línea de comandos
     */
    public static void main(String[] args) {
        launch(args);
    }
}
