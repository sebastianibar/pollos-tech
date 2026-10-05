package com.example.crudjavafx.controller;

import com.example.crudjavafx.db.ManejadorComprasDB;
import com.example.crudjavafx.db.ManejadorProductDB;
import com.example.crudjavafx.db.ManejadorProveedorDB;
import com.example.crudjavafx.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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

public class ComprasController {

    @FXML
    private TextField txtBuscarProducto;
    @FXML
    private TableView<Producto> tablaCatalogo;
    @FXML
    private TableColumn<Producto, String> colCatImagen;
    @FXML
    private TableColumn<Producto, String> colCatDesc;
    @FXML
    private TableColumn<Producto, Double> colCatPrecio;
    @FXML
    private TableColumn<Producto, String> colCatCategoria;
    @FXML
    private TableColumn<Producto, Integer> colCatStock;

    @FXML
    private TableView<DetalleCompra> tablaCarrito;
    @FXML
    private TableColumn<DetalleCompra, String> colCartDesc;
    @FXML
    private TableColumn<DetalleCompra, Integer> colCartCant;
    @FXML
    private TableColumn<DetalleCompra, Double> colCartCosto;
    @FXML
    private TableColumn<DetalleCompra, Double> colCartTotal;

    @FXML
    private Label lblTotal;
    @FXML
    private ComboBox<Proveedor> cmbProveedor;
    @FXML
    private TextField txtCostoUnitario;

    private ObservableList<DetalleCompra> listaDetallesCompra;
    private FilteredList<Producto> listaFiltrada;

    private String dbUrl, dbUser, dbPasswd;

    @FXML
    public void initialize() {
        Session sesion = Session.getInstance();
        this.dbUrl = sesion.getDbUrl();
        this.dbUser = sesion.getDbUser();
        this.dbPasswd = sesion.getDbPassword();

        configurarTablas();
        cargarProductos();
        cargarProveedores();
        calcularTotal();
    }

    private void cargarProveedores() {
        ManejadorProveedorDB db = new ManejadorProveedorDB(dbUrl, dbUser, dbPasswd);
        List<Proveedor> proveedores = db.getProveedores();
        cmbProveedor.setItems(FXCollections.observableArrayList(proveedores));
        if (!proveedores.isEmpty()) {
            cmbProveedor.getSelectionModel().selectFirst();
        }
    }

    private void configurarTablas() {

        colCatImagen.setCellValueFactory(new PropertyValueFactory<>("imagenUrl"));
        colCatImagen.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitHeight(90);
                imageView.setFitWidth(90);
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    try {
                        Image image = new Image(item, true); // true para cargar en background
                        imageView.setImage(image);
                        setGraphic(imageView);
                        setText(null);
                    } catch (Exception e) {
                        imageView.setImage(null);
                        setGraphic(null);
                        setText("❌");
                    }
                }
            }
        });

        colCatDesc.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colCatCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colCatPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCatStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        colCartDesc.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCartCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCartCosto.setCellValueFactory(new PropertyValueFactory<>("costoUnitario"));
        colCartTotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        listaDetallesCompra = FXCollections.observableArrayList();
        tablaCarrito.setItems(listaDetallesCompra);

        txtBuscarProducto.textProperty().addListener((obs, oldV, newV) -> {
            if (listaFiltrada != null) {
                listaFiltrada.setPredicate(producto -> {
                    if (newV == null || newV.isEmpty()) return true;
                    String q = newV.toLowerCase();
                    return producto.getDescripcion().toLowerCase().contains(q) || String.valueOf(producto.getId_producto()).contains(q) || producto.getCategoria().toLowerCase().contains(q);
                });
            }
        });
    }


    private void cargarProductos() {
        ManejadorProductDB db = new ManejadorProductDB(dbUrl, dbUser, dbPasswd);
        ObservableList<Producto> listaProductosMaster = FXCollections.observableArrayList(db.getProductCS());

        for (Producto p : listaProductosMaster) {
            String desc = p.getDescripcion();
            String resourcePath = switch (desc) {
                case "Pollo Entero" -> "/imagenes/logo-bachoco.jpg";
                case "Medio Pollo" -> "/imagenes/logo-bachoco-medio.jpg";
                case "Orden de Papas" -> "/imagenes/logo-papas.jpeg";
                case "Bebida Grande" -> "/imagenes/logo-cocacola.jpeg";
                case "Ensalada Chica" -> "/imagenes/logo-ensalada.jpg";
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

        if (seleccionado == null) {
            mostrarAlerta("Seleccione un producto.", "Aviso", Alert.AlertType.WARNING);
            return;
        }

        String costoTexto = txtCostoUnitario.getText().trim();
        if (costoTexto.isEmpty()) {
            mostrarAlerta("Ingrese el costo unitario del producto.", "Falta Dato", Alert.AlertType.WARNING);
            txtCostoUnitario.requestFocus();
            return;
        }

        double costoUnitario;
        try {
            costoUnitario = Double.parseDouble(costoTexto);
            if (costoUnitario <= 0) {
                mostrarAlerta("El costo debe ser mayor a cero.", "Error", Alert.AlertType.ERROR);
                txtCostoUnitario.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Ingrese un costo válido.", "Error", Alert.AlertType.ERROR);
            txtCostoUnitario.requestFocus();
            return;
        }

        boolean existe = false;
        for (DetalleCompra detalle : listaDetallesCompra) {
            if (detalle.getProducto().getId_producto() == seleccionado.getId_producto()) {
                detalle.addQuantity(1);
                existe = true;
                break;
            }
        }

        if (!existe) {
            listaDetallesCompra.add(new DetalleCompra(seleccionado, 1, costoUnitario));
        }

        tablaCarrito.refresh();
        calcularTotal();
        txtCostoUnitario.clear();

        tablaCatalogo.getSelectionModel().select(indiceSeleccionado);
        tablaCatalogo.requestFocus();
    }

    @FXML
    private void quitarDelCarrito() {
        DetalleCompra seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un elemento del carrito.", "Aviso", Alert.AlertType.WARNING);
            return;
        }

        listaDetallesCompra.remove(seleccionado);
        tablaCarrito.refresh();
        calcularTotal();
    }

    private void calcularTotal() {
        double total = 0.0;
        if (listaDetallesCompra != null) {
            for (DetalleCompra detalle : listaDetallesCompra) {
                total += detalle.getSubtotal();
            }
        }
        lblTotal.setText(String.format("$%,.2f", total));
    }

    @FXML
    private void finalizarCompra() {
        if (listaDetallesCompra == null || listaDetallesCompra.isEmpty()) {
            mostrarAlerta("El carrito está vacío.", "Error", Alert.AlertType.ERROR);
            return;
        }

        Proveedor proveedorSeleccionado = cmbProveedor.getValue();
        if (proveedorSeleccionado == null) {
            mostrarAlerta("Seleccione un proveedor.", "Falta Dato", Alert.AlertType.WARNING);
            cmbProveedor.requestFocus();
            return;
        }

        double totalCompra = 0.0;
        for (DetalleCompra detalle : listaDetallesCompra) {
            totalCompra += detalle.getSubtotal();
        }

        Compra nuevaCompra = new Compra(proveedorSeleccionado);
        nuevaCompra.setCosto_total(totalCompra);
        nuevaCompra.setDetalles(listaDetallesCompra);

        ManejadorComprasDB dbCompras = new ManejadorComprasDB(dbUrl, dbUser, dbPasswd);
        boolean exito = dbCompras.registrarCompraTransaction(nuevaCompra);

        if (exito) {
            mostrarAlerta("¡Compra registrada con éxito!\nProveedor: " + proveedorSeleccionado.getNombre(), "Compra Completada", Alert.AlertType.INFORMATION);
            listaDetallesCompra.clear();
            calcularTotal();
            cargarProductos(); // Recargar para mostrar el stock actualizado
            tablaCarrito.refresh();
        } else {
            mostrarAlerta("Ocurrió un error al guardar la compra.", "Error DB", Alert.AlertType.ERROR);
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