package com.example.crudjavafx.db;

import com.example.crudjavafx.model.Compra;
import com.example.crudjavafx.model.DetalleCompra;

import java.sql.*;
import java.time.LocalDate;

public class ManejadorComprasDB {

    private final String url;
    private final String user;
    private final String password;

    public ManejadorComprasDB(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection abrirConexion() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }


    public boolean registrarCompraTransaction(Compra compra) {

        String sqlCompra = "INSERT INTO Compra (fecha_compra, costo_total, id_proveedor) VALUES (?, ?, ?)";
        String sqlDetalle = "INSERT INTO Detalle_Compra (id_compra, id_producto, cantidad, costo_unitario) VALUES (?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE Producto SET stock = stock + ? WHERE id_producto = ?";

        try (Connection conn = abrirConexion()) {
            conn.setAutoCommit(false);

            try {
                PreparedStatement psCompra = conn.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS);

                psCompra.setDate(1, Date.valueOf(LocalDate.now()));
                psCompra.setDouble(2, compra.getCosto_total());
                psCompra.setInt(3, compra.getProveedor().getId_proveedor());

                int filasAfectadas = psCompra.executeUpdate();

                if (filasAfectadas == 0) {
                    throw new SQLException("No se pudo insertar la compra.");
                }

                ResultSet rsKeys = psCompra.getGeneratedKeys();
                int idCompra;
                if (rsKeys.next()) {
                    idCompra = rsKeys.getInt(1);
                } else {
                    throw new SQLException("No se generó el ID de la compra.");
                }

                PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
                PreparedStatement psStock = conn.prepareStatement(sqlUpdateStock);

                for (DetalleCompra item : compra.getDetalles()) {
                    psDetalle.setInt(1, idCompra);
                    psDetalle.setInt(2, item.getProducto().getId_producto());
                    psDetalle.setInt(3, item.getCantidad());
                    psDetalle.setDouble(4, item.getCostoUnitario());
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