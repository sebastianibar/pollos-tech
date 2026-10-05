package com.example.crudjavafx.db;

import com.example.crudjavafx.model.Empleado;
import java.sql.*;
import java.util.ArrayList;

public class ManejadorEmpleadoDB {


    private final String url;
    private final String user;
    private final String password;

    public ManejadorEmpleadoDB(String url, String user, String password) {
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


    public void cerrarConexion(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean probarConexion() {
        Connection conn = abrirConexion();
        if (conn != null) {
            cerrarConexion(conn);
            return true;
        } else {
            return false;
        }
    }

    public int getCredentials(String user, String password) {
        String query = "select id_empleado from empleado where usuario=? and contraseña=? ";

        try (Connection conn = abrirConexion();
             PreparedStatement ps = conn.prepareStatement(query)
        ) {

            ps.setString(1, user);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            int empleado_id = 0;
            while (rs.next()) {
                empleado_id = rs.getInt("id_empleado");
            }
            return empleado_id;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public String getName(int empleado_id) {
        String query = "select nombre from empleado where id_empleado=?  ";

        try (Connection conn = abrirConexion();
             PreparedStatement ps = conn.prepareStatement(query)
        ) {

            ps.setInt(1, empleado_id);
            ResultSet rs = ps.executeQuery();
            String empleado_name = "";
            while (rs.next()) {
                empleado_name = rs.getString("nombre");
            }
            return empleado_name;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public ArrayList<Empleado> getEmpleadoCS() {
        ArrayList<Empleado> empleadoList = new ArrayList<>();
        String query = "select * from obtiene_empleados()";

        try (Connection conn = abrirConexion();
             CallableStatement cs = conn.prepareCall(query);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                empleadoList.add(new Empleado(
                        rs.getInt("id_empleado"),
                        rs.getString("nombre"),
                        rs.getString("puesto"),
                        rs.getDouble("salario"),
                        rs.getString("usuario"),
                        rs.getString("contraseña")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return empleadoList;
    }


    // C - Create (INSERT) - CallableStatement
    public void insertarCS(Empleado empleado) {
        String query = "CALL insertar_empleado(?,?,?,?,?)";

        try (Connection conn = abrirConexion();
             CallableStatement cs = conn.prepareCall(query)
        ) {
            cs.setString(1, empleado.getNombre());
            cs.setString(2, empleado.getPuesto());
            cs.setDouble(3, empleado.getSalario());
            cs.setString(4, empleado.getUsuario());
            cs.setString(5, empleado.getContraseña());
            cs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    // U - Update (UPDATE) - CallableStatement
    public void actualizarCS(Empleado empleado) {
        String query = "CALL actualizar_empleado(?,?,?,?,?,?)";
        try (Connection conn = abrirConexion();
             CallableStatement cs = conn.prepareCall(query)
        ) {

            cs.setInt(1, empleado.getId_empleado());
            cs.setString(2, empleado.getNombre());
            cs.setString(3, empleado.getPuesto());
            cs.setDouble(4, empleado.getSalario());
            cs.setString(5, empleado.getUsuario());
            cs.setString(6, empleado.getContraseña());

            cs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    //D - Delete (DELETE) - CallableStatement
    public void eliminarCS(int idEliminar) {
        String query = "CALL eliminar_empleado(?)";
        try (Connection conn = abrirConexion();
             CallableStatement cs = conn.prepareCall(query)
        ) {
            cs.setInt(1, idEliminar);
            cs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}



