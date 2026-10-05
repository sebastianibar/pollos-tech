package com.example.crudjavafx.db;

import com.example.crudjavafx.model.Cliente;

import java.sql.*;
import java.util.ArrayList;

public class ManejadorClienteDB {

    private final String url;
    private final String user;
    private final String password;

    public ManejadorClienteDB(String url, String user, String password) {
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

    public ArrayList<Cliente> getClienteCS() {
        ArrayList<Cliente> CustomerList = new ArrayList<>();
        String query = "select * from obtener_clientes()";
        try (Connection conn = abrirConexion(); CallableStatement cs = conn.prepareCall(query); ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                CustomerList.add(new Cliente(rs.getInt("id_cliente"), rs.getString("nombre"), rs.getString("telefono")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return CustomerList;
    }


    // C - Create (INSERT) - CallableStatement
    public void insertarCS(Cliente cliente) {
        String query = "CALL insertar_cliente(?,?)";
        try (Connection conn = abrirConexion(); CallableStatement cs = conn.prepareCall(query)) {
            cs.setString(1, cliente.getNombre());
            cs.setString(2, cliente.getTelefono());
            cs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    // U - Update (UPDATE) - CallableStatement
    public void actualizarCS(Cliente cliente) {
        String query = "CALL actualizar_cliente(?,?,?)";
        try (Connection conn = abrirConexion(); CallableStatement cs = conn.prepareCall(query)) {
            cs.setInt(1, cliente.getId_cliente());
            cs.setString(2, cliente.getNombre());
            cs.setString(3, cliente.getTelefono());
            cs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    //D - Delete (DELETE) - CallableStatement
    public void eliminarCS(int idEliminar) {
        String query = "CALL eliminar_cliente(?)";
        try (Connection conn = abrirConexion(); CallableStatement cs = conn.prepareCall(query)) {
            cs.setInt(1, idEliminar);
            cs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}



