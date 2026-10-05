package com.example.crudjavafx.model;

public class Empleado {

    private int id_empleado;
    private String nombre;
    private String puesto;
    private String usuario;
    private String contraseña;
    private double salario;

    public Empleado() {

    }

    public Empleado(int id_empleado, String nombre, String puesto, double salario, String usuario, String contraseña) {
        this.id_empleado = id_empleado;
        this.nombre = nombre;
        this.puesto = puesto;
        this.salario = salario;
        this.usuario = usuario;
        this.contraseña = contraseña;
    }


    public int getId_empleado() {
        return id_empleado;
    }

    public void setId_empleado(int id_empleado) {
        this.id_empleado = id_empleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPuesto() {
        return puesto;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public double getSalario() {
        return salario;
    }

}