package com.example.crudjavafx.controller;

import com.example.crudjavafx.db.ManejadorClienteDB;
import com.example.crudjavafx.db.ManejadorProductDB;
import com.example.crudjavafx.db.ManejadorVentasDB;
import com.example.crudjavafx.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class POSController {

    @FXML
    private TextField txtBuscarProducto;
    @FXML
    private TableView<Producto> tablaCatalogo;
    @FXML
    private TableColumn<Producto, String> colCatDesc;
    @FXML
    private TableColumn<Producto, Double> colCatPrecio;
    @FXML
    private TableColumn<Producto, String> colCatCategoria;
    @FXML
    private TableColumn<Producto, Integer> colCatStock;

    @FXML
    private TableView<DetallePedido> tablaCarrito;
    @FXML
    private TableColumn<DetallePedido, String> colCartDesc;
    @FXML
    private TableColumn<DetallePedido, Integer> colCartCant;
    @FXML
    private TableColumn<DetallePedido, Double> colCartTotal;
    @FXML
    private TableColumn<DetallePedido, Double> colCartUnitario;

    @FXML
    private Label lblTotal;
    @FXML
    private ComboBox<String> cmbMetodoPago;
    @FXML
    private ComboBox<Cliente> cmbCliente;


    private ObservableList<DetallePedido> listaDetallesPedido;
    private FilteredList<Producto> listaFiltrada;


    private String dbUrl, dbUser, dbPasswd;
    private int currentStaffId;


    @FXML
    public void initialize() {
        Session sesion = Session.getInstance();
        this.dbUrl = sesion.getDbUrl();
        this.dbUser = sesion.getDbUser();
        this.dbPasswd = sesion.getDbPassword();
        this.currentStaffId = sesion.getStaffId();

        configurarTablas();
        cargarProductos();
        cargarMetodosPago();
        cargarClientes();
        calcularTotal();
    }

    private void cargarClientes() {
        ManejadorClienteDB db = new ManejadorClienteDB(dbUrl, dbUser, dbPasswd);
        List<Cliente> clientes = db.getClienteCS();
        cmbCliente.setItems(FXCollections.observableArrayList(clientes));
        if (!clientes.isEmpty()) {
            cmbCliente.getSelectionModel().selectFirst();
        }
    }

    private void cargarMetodosPago() {
        ObservableList<String> metodos = FXCollections.observableArrayList(
                "Efectivo",
                "Tarjeta de Crédito/Débito",
                "Transferencia"
        );
        cmbMetodoPago.setItems(metodos);
        if (!metodos.isEmpty()) {
            cmbMetodoPago.getSelectionModel().selectFirst();
        }
    }

    private void configurarTablas() {
        colCatDesc.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colCatCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colCatPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCatStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        TableColumn<Producto, String> colImagen = new TableColumn<>("Imagen");
        colImagen.setCellValueFactory(new PropertyValueFactory<>("imagenUrl"));
        colImagen.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);
                if (empty || url == null || url.isEmpty()) {
                    setGraphic(null);
                } else {
                    Image image = new Image(url, 120, 120, true, true);
                    if (image.isError()) {
                        setGraphic(null);
                    } else {
                        imageView.setImage(image);
                        setGraphic(imageView);
                    }
                }
            }
        });
        tablaCatalogo.getColumns().add(0, colImagen);

        // Carrito
        colCartDesc.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCartCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCartTotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        if (colCartUnitario != null) {
            colCartUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        }

        listaDetallesPedido = FXCollections.observableArrayList();
        tablaCarrito.setItems(listaDetallesPedido);

        txtBuscarProducto.textProperty().addListener((obs, oldV, newV) -> {
            if (listaFiltrada != null) {
                listaFiltrada.setPredicate(producto -> {
                    if (newV == null || newV.isEmpty()) return true;
                    String q = newV.toLowerCase();
                    return producto.getDescripcion().toLowerCase().contains(q)
                            || String.valueOf(producto.getId_producto()).contains(q)
                            || producto.getCategoria().toLowerCase().contains(q);
                });
            }
        });
    }

    private void cargarProductos() {
        ManejadorProductDB db = new ManejadorProductDB(dbUrl, dbUser, dbPasswd);
        ObservableList<Producto> listaProductosMaster = FXCollections.observableArrayList(db.getProductCS());

        for (Producto p : listaProductosMaster) {
            String desc = p.getDescripcion().trim();
            String resourcePath = switch (desc) {
                case "Pollo Entero" -> "/imagenes/pollo.png";
                case "Medio Pollo" -> "/imagenes/medio.jpg";
                case "Orden de Papas" -> "/imagenes/papas.png";
                case "Bebida Grande" -> "/imagenes/coca.png";
                case "Ensalada Chica" -> "/imagenes/ensalada.png";
                default -> "/com/example/crudjavafx/icono.png";
            };

            try {
                java.net.URL resourceUrl = getClass().getResource(resourcePath);

                if (resourceUrl != null) {
                    p.setImagenUrl(resourceUrl.toExternalForm());
                } else {
                    p.setImagenUrl(null);
                }
            } catch (Exception e) {
                p.setImagenUrl(null);
            }
        }

        listaFiltrada = new FilteredList<>(listaProductosMaster, p -> true);
        tablaCatalogo.setItems(listaFiltrada);
        tablaCatalogo.refresh();
    }

    @FXML
    private void agregarAlCarrito() {
        Producto seleccionado = tablaCatalogo.getSelectionModel().getSelectedItem();
        int indiceSeleccionado = tablaCatalogo.getSelectionModel().getSelectedIndex();
        if (seleccionado == null) return;

        if (seleccionado.getStock() <= 0) {
            mostrarAlerta("No hay stock disponible.", "Sin Stock", Alert.AlertType.WARNING);
            return;
        }

        boolean existe = false;
        for (DetallePedido detalle : listaDetallesPedido) {
            if (detalle.getProducto().getId_producto() == seleccionado.getId_producto()) {
                detalle.addQuantity(1);
                existe = true;
                break;
            }
        }
        if (!existe) {
            listaDetallesPedido.add(new DetallePedido(seleccionado, 1));
        }

        seleccionado.setStock(seleccionado.getStock() - 1);

        tablaCatalogo.refresh();
        tablaCarrito.refresh();
        calcularTotal();

        tablaCatalogo.getSelectionModel().select(indiceSeleccionado);
        tablaCatalogo.requestFocus();
    }

    @FXML
    private void quitarDelCarrito() {
        DetallePedido seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        Producto productoOriginal = seleccionado.getProducto();
        productoOriginal.setStock(productoOriginal.getStock() + seleccionado.getCantidad());

        listaDetallesPedido.remove(seleccionado);
        tablaCatalogo.refresh();
        tablaCarrito.refresh();
        calcularTotal();
    }

    private void calcularTotal() {
        double total = 0.0;
        if (listaDetallesPedido != null) {
            for (DetallePedido detalle : listaDetallesPedido) {
                total += detalle.getSubtotal();
            }
        }
        lblTotal.setText(String.format("$%,.2f", total));
    }

    @FXML
    private void finalizarVenta() {
        if (listaDetallesPedido == null || listaDetallesPedido.isEmpty()) {
            mostrarAlerta("El carrito está vacío.", "Error", Alert.AlertType.ERROR);
            return;
        }

        Cliente clienteSeleccionado = cmbCliente.getValue();
        if (clienteSeleccionado == null) {
            mostrarAlerta("Seleccione un cliente.", "Falta Dato", Alert.AlertType.WARNING);
            cmbCliente.requestFocus();
            return;
        }

        String metodoSeleccionado = cmbMetodoPago.getValue();
        if (metodoSeleccionado == null) {
            mostrarAlerta("Seleccione un método de pago.", "Falta Dato", Alert.AlertType.WARNING);
            cmbMetodoPago.requestFocus();
            return;
        }

        double totalVenta = 0.0;
        for (DetallePedido detalle : listaDetallesPedido) totalVenta += detalle.getSubtotal();

        Empleado empleadoActual = new Empleado();
        empleadoActual.setId_empleado(currentStaffId);

        Pedido nuevoPedido = new Pedido(empleadoActual, clienteSeleccionado, metodoSeleccionado);
        nuevoPedido.setTotal(totalVenta);
        nuevoPedido.setDetalles(listaDetallesPedido);

        ManejadorVentasDB dbVentas = new ManejadorVentasDB(dbUrl, dbUser, dbPasswd);
        boolean exito = dbVentas.registrarPedidoTransaction(nuevoPedido);

        if (exito) {
            mostrarAlerta("¡Venta realizada con éxito!\nPago: " + metodoSeleccionado,
                    "Venta Completada", Alert.AlertType.INFORMATION);
            listaDetallesPedido.clear();
            calcularTotal();
            cargarProductos();
            tablaCarrito.refresh();
        } else {
            mostrarAlerta("Ocurrió un error al guardar la venta.", "Error DB", Alert.AlertType.ERROR);
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
    protected void irADashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/crudjavafx/dashboard-view.fxml"));
            Parent root = loader.load();
            cambiarEscena(event, root, "Dashboard");
        } catch (IOException e) {
            mostrarAlerta("No se pudo abrir el Dashboard.", "Error", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void cerrarSesion(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Estás seguro de cerrar sesión?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass()
                .getResource("/com/example/crudjavafx/dashboard.css")).toExternalForm());
        alert.getDialogPane().getStyleClass().add("custom-alert");

        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                Session.cerrarSesion();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/crudjavafx/login-view.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Sistema POS - Login");
                stage.setScene(new Scene(root));
                stage.getScene().getStylesheets().add(Objects.requireNonNull(getClass()
                        .getResource("/com/example/crudjavafx/dashboard.css")).toExternalForm());
                ((LoginController) loader.getController()).setPrimaryStage(stage);
                stage.show();
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            } catch (IOException e) {

                mostrarAlerta("No se pudo cerrar sesión.", "Error", Alert.AlertType.ERROR);
            }
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

    private void mostrarAlerta(String mensaje, String titulo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(null);
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        DialogPane dialogPane = alerta.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/crudjavafx/dashboard.css")).toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        if (tablaCatalogo != null && tablaCatalogo.getScene() != null) {
            Stage stage = (Stage) tablaCatalogo.getScene().getWindow();
            alerta.initOwner(stage);
        }
        alerta.showAndWait();
    }
}