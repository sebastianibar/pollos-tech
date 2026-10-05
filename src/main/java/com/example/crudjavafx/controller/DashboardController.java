package com.example.crudjavafx.controller;

import com.example.crudjavafx.db.ManejadorReportesDB;
import com.example.crudjavafx.model.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class DashboardController {

    @FXML
    private Label bienvenidaText;

    @FXML private VBox containerPendientes;
    @FXML private VBox containerCompletados;
    @FXML
    public void initialize() {
        try {
            Session sesion = Session.getInstance();


            if (bienvenidaText != null) {
                bienvenidaText.setText("Hola, " + sesion.getStaffName() + "!");
            }

            // cargar datos de reportes
            actualizarTablero(sesion);

        } catch (IllegalStateException e) {
            System.err.println("Error de sesión: " + e.getMessage());
        }
    }

    private void actualizarTablero(Session sesion) {
        ManejadorReportesDB db = new ManejadorReportesDB(sesion.getDbUrl(), sesion.getDbUser(), sesion.getDbPassword());
        if (containerPendientes != null) containerPendientes.getChildren().clear();
        if (containerCompletados != null) containerCompletados.getChildren().clear();

        //muestra los últimos 5
        List<ManejadorReportesDB.PedidoResumen> pendientes = db.getPedidosPorEstatus("Pagado", 5);
        for (ManejadorReportesDB.PedidoResumen p : pendientes) {
            crearTarjetaPendiente(p, sesion);
        }

        List<ManejadorReportesDB.PedidoResumen> completados = db.getPedidosPorEstatus("Entregado", 0);
        for (ManejadorReportesDB.PedidoResumen p : completados) {
            crearTarjetaCompletado(p);
        }
    }

    private void crearTarjetaPendiente(ManejadorReportesDB.PedidoResumen p, Session sesion) {
        if (containerPendientes == null) return;

        HBox tarjeta = new HBox();
        tarjeta.setStyle("-fx-background-color: #dcdcdc; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        tarjeta.setPadding(new Insets(15));
        tarjeta.setSpacing(20);
        tarjeta.setAlignment(Pos.CENTER_LEFT);

        Label lblDetalles = new Label(p.getDetalles());
        lblDetalles.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-font-weight: bold;");
        lblDetalles.setWrapText(true);

        VBox textBox = new VBox(lblDetalles);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Button btnEntregar = new Button("Entregar");
        btnEntregar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnEntregar.setOnAction(e -> {
            ManejadorReportesDB db = new ManejadorReportesDB(sesion.getDbUrl(), sesion.getDbUser(), sesion.getDbPassword());
            db.marcarPedidoEntregado(p.getId());
            actualizarTablero(sesion); // recargar pantalla
        });

        tarjeta.getChildren().addAll(textBox, btnEntregar);
        containerPendientes.getChildren().add(tarjeta);
    }

    private void crearTarjetaCompletado(ManejadorReportesDB.PedidoResumen p) {
        if (containerCompletados == null) return;

        VBox tarjeta = new VBox();
        tarjeta.setStyle("-fx-background-color: #dcdcdc; -fx-background-radius: 10;");
        tarjeta.setPadding(new Insets(10));
        tarjeta.setSpacing(5);

        Label lblResumen = new Label(p.getDetalles().replace("\n", ", ")); // En una linea o pocas
        lblResumen.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        lblResumen.setWrapText(true);

        Label lblTotal = new Label(String.format("TOTAL: $%,.2f", p.getTotal()));
        lblTotal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #000;");

        tarjeta.getChildren().addAll(lblResumen, lblTotal);
        containerCompletados.getChildren().add(tarjeta);
    }

    @FXML
    public void cerrarSesion(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de cerrar sesión?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/crudjavafx/dashboard.css")).toExternalForm());
        alert.getDialogPane().getStyleClass().add("custom-alert");

        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                Session.cerrarSesion();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/crudjavafx/login-view.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Sistema POS - Login");

                stage.setScene(new Scene(root));
                stage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/crudjavafx/dashboard.css")).toExternalForm());
                ((LoginController) loader.getController()).setPrimaryStage(stage);
                stage.show();
                //cerrar ventana
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            } catch (IOException e) {
                mostrarAlerta("No se pudo cerrar sesión.");
            }
        }
    }


    @FXML protected void irAGestionStaff(ActionEvent event) {navegar(event, "CRUDEmpleado-view.fxml", "Personal");}
    @FXML protected void irAGestionProduct(ActionEvent event) {navegar(event, "CRUDProduct-view.fxml", "Productos");}
    @FXML protected void irAGestionCustomer(ActionEvent event) {navegar(event, "CRUDCliente-view.fxml", "Clientes");}
    @FXML protected void irAGestionProveedor(ActionEvent event) { navegar(event, "CRUDProveedor-view.fxml", "Proveedores");}
    @FXML protected void irAGestionVenta(ActionEvent event) {navegar(event, "POS-view.fxml", "Ventas");}
    @FXML protected void irAGestionCompra(ActionEvent event) {navegar(event, "Compras-view.fxml", "Compras");}


    private void navegar(ActionEvent event, String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/crudjavafx/" + fxml));
            Parent root = loader.load();
            cambiarEscena(event, root, titulo);
        } catch (IOException e) {
            mostrarAlerta("No se pudo abrir " + titulo + ".");
        }
    }

    private void cambiarEscena(ActionEvent event, Parent root, String titulo) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene currentScene = stage.getScene();
        currentScene.setRoot(root);
        stage.setTitle(titulo);
        if (!stage.isFullScreen()) {
            stage.setFullScreen(true);
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(null);
        alerta.setHeaderText("Error");
        alerta.setContentText(mensaje);
        DialogPane dialogPane = alerta.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/crudjavafx/dashboard.css")).toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alerta.showAndWait();
    }


}