package com.example.crudjavafx.db;

import com.example.crudjavafx.model.DetallePedido;
import com.example.crudjavafx.model.Pedido;
import java.sql.*;
import java.time.LocalDate;

public class ManejadorVentasDB {

    private final String url;
    private final String user;
    private final String password;

    public ManejadorVentasDB(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection abrirConexion() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }


    public boolean registrarPedidoTransaction(Pedido pedido) {

        String sqlPedido = "INSERT INTO Pedido (fecha, total, estatus, metodo_pago, id_empleado, id_cliente) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO Detalle_Pedido (id_pedido, id_producto, cantidad, subtotal) VALUES (?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE Producto SET stock = stock - ? WHERE id_producto = ?";

        String estatus = "Pagado";

        try (Connection conn = abrirConexion()) {
            conn.setAutoCommit(false);

            try {
                PreparedStatement psPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);

                psPedido.setDate(1, Date.valueOf(LocalDate.now()));
                psPedido.setDouble(2, pedido.getTotal());
                psPedido.setString(3, estatus);
                psPedido.setString(4, pedido.getMetodo_pago());
                psPedido.setInt(5, pedido.getEmpleado().getId_empleado());
                psPedido.setInt(6, pedido.getCliente().getId_cliente());

                int filasAfectadas = psPedido.executeUpdate();

                if (filasAfectadas == 0) {
                    throw new SQLException("No se pudo insertar el pedido.");
                }

                ResultSet rsKeys = psPedido.getGeneratedKeys();
                int idPedido;
                if (rsKeys.next()) {
                    idPedido = rsKeys.getInt(1);
                } else {
                    throw new SQLException("No se generó el ID del pedido.");
                }

                PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
                PreparedStatement psStock = conn.prepareStatement(sqlUpdateStock);

                for (DetallePedido item : pedido.getDetalles()) {

                    psDetalle.setInt(1, idPedido);
                    psDetalle.setInt(2, item.getProducto().getId_producto());
                    psDetalle.setInt(3, item.getCantidad());
                    psDetalle.setDouble(4, item.getSubtotal());
                    psDetalle.addBatch();

                    psStock.setInt(1, item.getCantidad());
                    psStock.setInt(2, item.getProducto().getId_producto());
                    psStock.addBatch();
                }

                psDetalle.executeBatch();
                psStock.executeBatch();

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();

                return false;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
             return false;
        }
    }
}