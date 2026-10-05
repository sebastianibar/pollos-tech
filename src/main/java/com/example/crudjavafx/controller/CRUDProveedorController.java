package com.example.crudjavafx.controller;

import com.example.crudjavafx.db.ManejadorProveedorDB;
import com.example.crudjavafx.model.Proveedor;
import com.example.crudjavafx.model.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CRUDProveedorController {

    @FXML
    private TextField idText;
    @FXML
    private TextField nombreText;
    @FXML
    private TextField contactoText;
    @FXML
    private TextField telefonoText;

    @FXML
    private TableView<Proveedor> tablaProveedor;

    @FXML
    private TableColumn<Proveedor, String> columnaNombre;
    @FXML
    private TableColumn<Proveedor, String> columnaContacto;
    @FXML
    private TableColumn<Proveedor, String> columnaTelefono;

    private ManejadorProveedorDB manejadorProveedorDB;
    private ObservableList<Proveedor> listaObservable;


    @FXML
    public void initialize() {
        Session sesion = Session.getInstance();

        String dbUrl = sesion.getDbUrl();
        String dbUser = sesion.getDbUser();
        String dbPasswd = sesion.getDbPassword();

        manejadorProveedorDB = new ManejadorProveedorDB(dbUrl, dbUser, dbPasswd);
        listaObservable = FXCollections.observableArrayList(manejadorProveedorDB.getProveedores());
        tablaProveedor.setItems(listaObservable);

        tablaProveedor.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                cargarSeleccionado(newSel);
            }
        });

        limpiarForm();

        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaContacto.setCellValueFactory(new PropertyValueFactory<>("contacto"));
        columnaTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
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

                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            } catch (IOException e) {
                mostrarAlerta("No se pudo cerrar sesión.", "Error", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    protected void irAGestionStaff(ActionEvent event) {
        navegar(event, "CRUDEmpleado-view.fxml", "Personal");
    }

    @FXML
    protected void irAGestionProduct(ActionEvent event) {
        navegar(event, "CRUDProduct-view.fxml", "Productos");
    }

    @FXML
    protected void irAGestionCustomer(ActionEvent event) {
        navegar(event, "CRUDCliente-view.fxml", "Clientes");
    }

    @FXML
    protected void irAGestionProveedor(ActionEvent event) {
        navegar(event, "CRUDProveedor-view.fxml", "Proveedores");
    }

    @FXML
    protected void irAGestionVenta(ActionEvent event) {
        navegar(event, "POS-view.fxml", "Ventas");
    }

    @FXML
    protected void irAGestionCompra(ActionEvent event) {
        navegar(event, "Compras-view.fxml", "Compras");
    }


    @FXML
    protected void irADashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/crudjavafx/dashboard-view.fxml"));
            Parent root = loader.load();
            cambiarEscena(event, root, "Dashboard");
        } catch (IOException e) {
            mostrarAlerta("No se pudo abrir el Dashboard.", "Error", Alert.AlertType.ERROR);
        }
    }

    private void navegar(ActionEvent event, String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/crudjavafx/" + fxml));
            Parent root = loader.load();
            cambiarEscena(event, root, titulo);
        } catch (IOException e) {
            mostrarAlerta("No se pudo abrir " + titulo + ".", "Error", Alert.AlertType.ERROR);
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

    @FXML
    private void guardarProveedor() {
        String nombre = nombreText.getText();
        String contacto = contactoText.getText();
        String telefono = telefonoText.getText();

        if (nombre.isEmpty()) {
            mostrarAlerta("Debe ingresar un nombre", "Error", Alert.AlertType.ERROR);
            nombreText.requestFocus();
            return;
        } else if (contacto.isEmpty()) {
            mostrarAlerta("Debe ingresar un contacto", "Error", Alert.AlertType.ERROR);
            contactoText.requestFocus();
            return;
        } else if (telefono.isEmpty()) {
            mostrarAlerta("Debe ingresar un teléfono", "Error", Alert.AlertType.ERROR);
            telefonoText.requestFocus();
            return;
        }

        int idGuardar = Integer.parseInt(idText.getText());
        Proveedor proveedor = new Proveedor(idGuardar, nombre, contacto, telefono);

        try {
            if (idGuardar == 0) {
                manejadorProveedorDB.insertar(proveedor);
                mostrarAlerta("Proveedor agregado correctamente.", "Correcto", Alert.AlertType.INFORMATION);
            } else {
                manejadorProveedorDB.actualizar(proveedor);
                mostrarAlerta("Proveedor actualizado correctamente.", "Correcto", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            mostrarAlerta("Error al ejecutar la operación: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
        }

        recargarDatos();
    }

    @FXML
    private void eliminarProveedor() {
        int idEliminar = Integer.parseInt(idText.getText());

        if (idEliminar != 0) {
            manejadorProveedorDB.eliminar(idEliminar);
            mostrarAlerta("Proveedor eliminado correctamente.", "Correcto", Alert.AlertType.INFORMATION);
            recargarDatos();
        } else {
            mostrarAlerta("Seleccione un proveedor válido", "Aviso", Alert.AlertType.WARNING);
        }
    }

    private void cargarSeleccionado(Proveedor proveedor) {
        idText.setText(String.valueOf(proveedor.getId_proveedor()));
        nombreText.setText(proveedor.getNombre());
        contactoText.setText(proveedor.getContacto());
        telefonoText.setText(proveedor.getTelefono());
    }

    @FXML
    private void recargarDatos() {
        listaObservable.setAll(manejadorProveedorDB.getProveedores());
        limpiarForm();
    }

    private void limpiarForm() {
        idText.setText(String.valueOf(0));
        nombreText.clear();
        contactoText.clear();
        telefonoText.clear();
        tablaProveedor.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String mensaje, String titulo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(null);
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);

        DialogPane dialogPane = alerta.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/crudjavafx/dashboard.css")).toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        if (idText != null && idText.getScene() != null) {
            Stage stage = (Stage) idText.getScene().getWindow();
            alerta.initOwner(stage);
        }
        alerta.showAndWait();
    }
}