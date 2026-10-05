package com.example.crudjavafx.db;

import com.example.crudjavafx.model.Producto;

import java.sql.*;
import java.util.ArrayList;

public class ManejadorProductDB {

    private final String url;
    private final String user;
    private final String password;

    public ManejadorProductDB(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection abrirConexion() {
        try {
            DriverManager.setLoginTimeout(10);
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // R - Read (SELECT)
    public ArrayList<Producto> getProductCS() {
        ArrayList<Producto> productoList = new ArrayList<>();
        String query = "SELECT * FROM obtener_productos()";

        try (Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query); // Usamos PreparedStatement para selects directos
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                productoList.add(new Producto(rs.getInt("id_producto"),   // Nombres coinciden con la BD
                        rs.getString("descripcion"), rs.getString("categoria"), rs.getInt("stock"), rs.getDouble("precio")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return productoList;
    }

    // C - Create (INSERT)
    public void insertarCS(Producto producto) {
        String query = "CALL insertar_producto(?, ?, ?, ?)";

        try (Connection conn = abrirConexion(); CallableStatement cs = conn.prepareCall(query)) {
            cs.setString(1, producto.getDescripcion());
            cs.setString(2, producto.getCategoria());
            cs.setInt(3, producto.getStock());
            cs.setDouble(4, producto.getPrecio());

            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // U - Update (UPDATE)
    public void actualizarCS(Producto producto) {
        String query = "CALL actualizar_producto(?, ?, ?, ?, ?)";

        try (Connection conn = abrirConexion(); CallableStatement cs = conn.prepareCall(query)) {

            cs.setInt(1, producto.getId_producto());
            cs.setString(2, producto.getDescripcion());
            cs.setString(3, producto.getCategoria());
            cs.setInt(4, producto.getStock());
            cs.setDouble(5, producto.getPrecio());

            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // D - Delete (DELETE)
    public void eliminarCS(int idEliminar) {
        String query = "CALL eliminar_producto(?)";

        try (Connection conn = abrirConexion(); CallableStatement cs = conn.prepareCall(query)) {
            cs.setInt(1, idEliminar);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}