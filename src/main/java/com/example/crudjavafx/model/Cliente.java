package com.example.crudjavafx.model;

public class Cliente {

    private final int id_cliente;
    private final String nombre;
    private final String telefono;

    public Cliente(int id_cliente, String nombre, String telefono) {
        this.id_cliente = id_cliente;
        this.telefono = telefono;
        this.nombre = nombre;
    }


    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    @Override
    public String toString() {
        return nombre;
    }

}