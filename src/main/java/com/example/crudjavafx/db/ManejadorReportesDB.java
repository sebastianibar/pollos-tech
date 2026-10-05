package com.example.crudjavafx.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ManejadorReportesDB {

    private final String url;
    private final String user;
    private final String password;

    public ManejadorReportesDB(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection abrirConexion() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void marcarPedidoEntregado(int idPedido) {
        String query = "UPDATE Pedido SET estatus = 'Entregado' WHERE id_pedido = ?";
        try (Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idPedido);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<PedidoResumen> getPedidosPorEstatus(String estatus, int limite) {
        List<PedidoResumen> lista = new ArrayList<>();
        String sql;
        if (limite > 0) {
            sql = "SELECT p.id_pedido, p.total, " + "STRING_AGG(CONCAT(dp.cantidad, 'x ', prod.descripcion), chr(10)) as detalles " + "FROM Pedido p " + "JOIN Detalle_Pedido dp ON p.id_pedido = dp.id_pedido " + "JOIN Producto prod ON dp.id_producto = prod.id_producto " + "WHERE p.estatus = ? " + "GROUP BY p.id_pedido, p.total " + "ORDER BY p.fecha DESC LIMIT ?";
        } else {
            sql = "SELECT p.id_pedido, p.total, " + "STRING_AGG(CONCAT(dp.cantidad, 'x ', prod.descripcion), chr(10)) as detalles " + "FROM Pedido p " + "JOIN Detalle_Pedido dp ON p.id_pedido = dp.id_pedido " + "JOIN Producto prod ON dp.id_producto = prod.id_producto " + "WHERE p.estatus = ? " + "GROUP BY p.id_pedido, p.total " + "ORDER BY p.fecha ASC";
        }

        try (Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estatus);
            if (limite > 0) ps.setInt(2, limite);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new PedidoResumen(rs.getInt("id_pedido"), rs.getString("detalles"), rs.getDouble("total")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    public static class PedidoResumen {
        int id;
        String detalles;
        double total;

        public PedidoResumen(int id, String detalles, double total) {
            this.id = id;
            this.detalles = detalles;
            this.total = total;
        }

        public int getId() {
            return id;
        }

        public String getDetalles() {
            return detalles;
        }

        public double getTotal() {
            return total;
        }
    }


}