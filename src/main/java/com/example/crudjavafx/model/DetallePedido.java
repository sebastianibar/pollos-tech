package com.example.crudjavafx.model;

public class DetallePedido {

    private final Producto producto;
    private final String nombreProducto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetallePedido(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;

        this.nombreProducto = producto.getDescripcion();
        this.precioUnitario = producto.getPrecio();

        this.calcularSubtotal();
    }

    private void calcularSubtotal() {
        this.subtotal = this.precioUnitario * this.cantidad;
    }

    public void addQuantity(int qty) {
        this.cantidad += qty;
        calcularSubtotal();
    }


    public Producto getProducto() {
        return producto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    public int getCantidad() {
        return cantidad;
    }

    // --- Setters ---

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}