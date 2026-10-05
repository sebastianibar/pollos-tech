package com.example.crudjavafx.controller;

import com.example.crudjavafx.db.ManejadorEmpleadoDB;
import com.example.crudjavafx.model.Empleado;
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

public class CRUDStaffController {


    @FXML
    private TextField idText;
    @FXML
    private TextField nombreText;
    @FXML
    private TextField puestoText;
    @FXML
    private TextField salarioText;
    @FXML
    private TextField usuarioText;
    @FXML
    private TextField contraseñaText;


    @FXML
    private TableView<Empleado> tablaEmpleado;


    @FXML
    private TableColumn<Empleado, String> columnaNombre;

    @FXML
    private TableColumn<Empleado, String> columnaPuesto;

    @FXML
    private TableColumn<Empleado, Double> columnaSalario;
    @FXML
    private TableColumn<Empleado, String> columnaUsuario;
    @FXML
    private TableColumn<Empleado, String> columnaContraseña;


    private ManejadorEmpleadoDB ManejadorEmpleadoDB;
    private ObservableList<Empleado> listaObservable;


    @FXML
    public void initialize() {
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaPuesto.setCellValueFactory(new PropertyValueFactory<>("puesto"));
        columnaSalario.setCellValueFactory(new PropertyValueFactory<>("salario"));

        columnaUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));

        columnaContraseña.setCellValueFactory(new PropertyValueFactory<>("contraseña"));

        Session sesion = Session.getInstance();

        String dbUrl = sesion.getDbUrl();
        String dbUser = sesion.getDbUser();
        String dbPasswd = sesion.getDbPassword();
        ManejadorEmpleadoDB = new ManejadorEmpleadoDB(dbUrl, dbUser, dbPasswd);
        listaObservable = FXCollections.observableArrayList(ManejadorEmpleadoDB.getEmpleadoCS());
        tablaEmpleado.setItems(listaObservable);
        tablaEmpleado.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {

            if (newSel != null) {
                cargarSeleccionado(newSel);
            }

        });

        limpiarForm();

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
    private void guardarStaff() {
        //trae los datos de los textbox
        String nombre = nombreText.getText();
        String puesto = puestoText.getText();
        String slr = salarioText.getText();
        String usuario = usuarioText.getText();
        String contraseña = contraseñaText.getText();
        double salario = 0;

        if (nombre.isEmpty()) {
            mostrarAlerta("Debe ingresar un nombre", "Error", Alert.AlertType.ERROR);
            nombreText.requestFocus();
            return;
        } else if (puesto.isEmpty()) {
            mostrarAlerta("Debe ingresar un puesto", "Error", Alert.AlertType.ERROR);
            puestoText.requestFocus();
            return;
        } else if (!slr.isEmpty()) {

            try {
                salario = Double.parseDouble(slr);
                if (salario <= 0) {
                    mostrarAlerta("El salario debe ser mayor a 0", "Error", Alert.AlertType.WARNING);
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("El salario debe ser un número válido (sin letras).", "Error de Formato", Alert.AlertType.ERROR);
                return;
            }

        } else if (usuario.isEmpty()) {
            mostrarAlerta("Debe ingresar un usuario", "Error", Alert.AlertType.ERROR);
            usuarioText.requestFocus();
            return;
        } else if (contraseña.isEmpty()) {
            mostrarAlerta("Debe ingresar una contraseña", "Error", Alert.AlertType.ERROR);
            contraseñaText.requestFocus();
            return;
        }


        int idGuardar = Integer.parseInt(idText.getText());
        Empleado empleado = new Empleado(idGuardar, nombre, puesto, salario, usuario, contraseña);
        try {
            if (idGuardar == 0) {

                ManejadorEmpleadoDB.insertarCS(empleado); // Llama al metodo
                mostrarAlerta("Empleado agregado correctamente.", "Correcto", Alert.AlertType.INFORMATION);

            } else {

                ManejadorEmpleadoDB.actualizarCS(empleado); // Llama al metodo
                mostrarAlerta("Empleado actualizado correctamente.", "Correcto", Alert.AlertType.INFORMATION);
            }

        } catch (Exception e) {
            mostrarAlerta("Error al ejecutar la operación: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
        }

        recargarDatos();
    }

    @FXML
    private void eliminarStaff() {
        int idEliminar = Integer.parseInt(idText.getText());

        if (idEliminar != 0) {

            ManejadorEmpleadoDB.eliminarCS(idEliminar);

            mostrarAlerta("Empleado eliminado correctamente.", "Correcto", Alert.AlertType.INFORMATION);

            recargarDatos();
        } else {
            mostrarAlerta("Seleccione un empleado válido", "Aviso", Alert.AlertType.WARNING);
        }


    }


    private void cargarSeleccionado(Empleado empleado) {

        idText.setText(String.valueOf(empleado.getId_empleado()));
        nombreText.setText(empleado.getNombre());
        puestoText.setText(empleado.getPuesto());
        salarioText.setText(String.valueOf(empleado.getSalario()));

        usuarioText.setText(empleado.getUsuario());
        contraseñaText.setText(empleado.getContraseña());

    }

    @FXML
    private void recargarDatos() {
        listaObservable.setAll(ManejadorEmpleadoDB.getEmpleadoCS());


        limpiarForm();
    }


    private void limpiarForm() {
        idText.setText(String.valueOf(0));
        nombreText.clear();
        puestoText.clear();
        salarioText.clear();
        usuarioText.clear();
        contraseñaText.clear();
        //quitar el renglón seleccionado en la tabla
        tablaEmpleado.getSelectionModel().clearSelection();

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
