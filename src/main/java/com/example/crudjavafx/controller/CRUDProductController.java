package com.example.crudjavafx.controller;

import com.example.crudjavafx.db.ManejadorProductDB;
import com.example.crudjavafx.model.Producto;
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

public class CRUDProductController {

    @FXML
    private TextField idText;
    @FXML
    private TextField descripcionText;
    @FXML
    private TextField categoriaText;
    @FXML
    private TextField precioText;
    @FXML
    private TextField stockText;

    @FXML
    private TableView<Producto> tablaProduct;
    @FXML
    private TableColumn<Producto, String> columnaDescription;
    @FXML
    private TableColumn<Producto, String> columnaCategoria;
    @FXML
    private TableColumn<Producto, Double> columnaPrecio;
    @FXML
    private TableColumn<Producto, Integer> columnaStock;

    private ManejadorProductDB manejadorProductDB;
    private ObservableList<Producto> listaObservable;

    @FXML
    public void initialize() {
        Session sesion = Session.getInstance();
        String dbUrl = sesion.getDbUrl();
        String dbUser = sesion.getDbUser();
        String dbPasswd = sesion.getDbPassword();

        manejadorProductDB = new ManejadorProductDB(dbUrl, dbUser, dbPasswd);

        listaObservable = FXCollections.observableArrayList(manejadorProductDB.getProductCS());
        tablaProduct.setItems(listaObservable);

        tablaProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                cargarSeleccionado(newSel);
            }
        });

        limpiarForm();

        columnaDescription.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        columnaCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnaStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
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
    protected void irAGestionCompra(ActionEvent event) {
        navegar(event, "Compras-view.fxml", "Compras");
    }

    @FXML
    protected void irAGestionVenta(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/crudjavafx/POS-view.fxml"));
            Parent root = loader.load();
            cambiarEscena(event, root, "Ventas");
        } catch (IOException e) {
            mostrarAlerta("No se pudo cargar la vista de ventas.", "Error", Alert.AlertType.ERROR);
        }
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
    private void guardarProduct() {
        String descripcion = descripcionText.getText();
        String categoria = categoriaText.getText();

        if (descripcion.isEmpty() || categoria.isEmpty()) {
            mostrarAlerta("Debe ingresar descripción y categoría", "Error", Alert.AlertType.ERROR);
            return;
        }

        double precio = 0;
        int stock = 0;

        try {
            String pText = precioText.getText();
            String sText = stockText.getText();

            if (!pText.isEmpty()) precio = Double.parseDouble(pText);
            if (!sText.isEmpty()) stock = Integer.parseInt(sText);

            if (precio <= 0) {
                mostrarAlerta("El precio debe ser mayor a 0", "Error", Alert.AlertType.WARNING);
                return;
            }
            if (stock < 0) {
                mostrarAlerta("El stock no puede ser negativo", "Error", Alert.AlertType.WARNING);
                return;
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Precio y Stock deben ser números válidos.", "Error de Formato", Alert.AlertType.ERROR);
            return;
        }

        int idGuardar = Integer.parseInt(idText.getText());

        Producto producto = new Producto(idGuardar, descripcion, categoria, stock, precio);

        try {
            if (idGuardar == 0) {
                manejadorProductDB.insertarCS(producto);
                mostrarAlerta("Producto agregado correctamente.", "Correcto", Alert.AlertType.INFORMATION);
            } else {
                manejadorProductDB.actualizarCS(producto);
                mostrarAlerta("Producto actualizado correctamente.", "Correcto", Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            mostrarAlerta("Error en BD: " + e.getMessage(), "Error", Alert.AlertType.ERROR);
        }

        recargarDatos();
    }

    @FXML
    private void eliminarProduct() {
        int idEliminar = Integer.parseInt(idText.getText());

        if (idEliminar != 0) {
            manejadorProductDB.eliminarCS(idEliminar);
            mostrarAlerta("Producto eliminado correctamente.", "Correcto", Alert.AlertType.INFORMATION);
            recargarDatos();
        } else {
            mostrarAlerta("Seleccione un producto para eliminar", "Aviso", Alert.AlertType.WARNING);
        }
    }

    private void cargarSeleccionado(Producto producto) {
        idText.setText(String.valueOf(producto.getId_producto()));
        descripcionText.setText(producto.getDescripcion());
        categoriaText.setText(producto.getCategoria());
        stockText.setText(String.valueOf(producto.getStock()));
        precioText.setText(String.valueOf(producto.getPrecio()));
    }

    @FXML
    private void recargarDatos() {
        listaObservable.setAll(manejadorProductDB.getProductCS());
        limpiarForm();
    }

    private void limpiarForm() {
        idText.setText("0");
        descripcionText.clear();
        categoriaText.clear();
        stockText.clear();
        precioText.clear();
        tablaProduct.getSelectionModel().clearSelection();
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
