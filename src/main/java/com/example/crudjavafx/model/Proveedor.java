package com.example.crudjavafx.model;

public class Proveedor {

    private final int id_proveedor;
    private final String nombre;
    private final String contacto;
    private final String telefono;

    public Proveedor(int id_proveedor, String nombre, String contacto, String telefono) {
        this.id_proveedor = id_proveedor;
        this.nombre = nombre;
        this.contacto = contacto;
        this.telefono = telefono;
    }

    public int getId_proveedor() {
        return id_proveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContacto() {
        return contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    @Override
    public String toString() {
        return nombre;
    }
}