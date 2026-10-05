package com.example.crudjavafx.db;

import com.example.crudjavafx.model.Proveedor;

import java.sql.*;
import java.util.ArrayList;

public class ManejadorProveedorDB {

    private final String url;
    private final String user;
    private final String password;

    public ManejadorProveedorDB(String url, String user, String password) {
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


    // R - Read (SELECT) - CallableStatement
    public ArrayList<Proveedor> getProveedores() {
        ArrayList<Proveedor> proveedorList = new ArrayList<>();
        String query = "select * from obtener_proveedores()";

        try (Connection conn = abrirConexion(); CallableStatement cs = conn.prepareCall(query); ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                proveedorList.add(new Proveedor(rs.getInt("id_proveedor"), rs.getString("nombre"), rs.getString("contacto"), rs.getString("telefono")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return proveedorList;
    }

    // C - Create (INSERT) - CallableStatement
    public void insertar(Proveedor proveedor) {
        String query = "CALL insertar_proveedor(?,?,?)";

        try (Connection conn = abrirConexion(); CallableStatement cs = conn.prepareCall(query)) {

            cs.setString(1, proveedor.getNombre());
            cs.setString(2, proveedor.getContacto());
            cs.setString(3, proveedor.getTelefono());

            cs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // U - Update (UPDATE) - CallableStatement
    public void actualizar(Proveedor proveedor) {
        String query = "CALL actualizar_proveedor(?,?,?,?)";

        try (Connection conn = abrirConexion(); CallableStatement cs = conn.prepareCall(query)) {

            cs.setInt(1, proveedor.getId_proveedor());
            cs.setString(2, proveedor.getNombre());
            cs.setString(3, proveedor.getContacto());
            cs.setString(4, proveedor.getTelefono());

            cs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // D - Delete (DELETE) - CallableStatement
    public int eliminar(int idEliminar) {
        String query = "CALL eliminar_proveedor(?)";

        try (Connection conn = abrirConexion(); CallableStatement cs = conn.prepareCall(query)) {

            cs.setInt(1, idEliminar);
            return cs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}