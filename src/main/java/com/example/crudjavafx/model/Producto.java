package com.example.crudjavafx.model;

public class Producto {

    private final int id_producto;
    private final String descripcion;
    private final String categoria;
    private final double precio;
    private int stock;
    private String imagenUrl;


    public Producto(int id_producto, String descripcion, String categoria, int stock, double precio, String imagenUrl) {
        this.id_producto = id_producto;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.stock = stock;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
    }

    public Producto(int id_producto, String descripcion, String categoria, int stock, double precio) {
        this(id_producto, descripcion, categoria, stock, precio, null);
    }

    public int getId_producto() {
        return id_producto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrecio() {
        return precio;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}
