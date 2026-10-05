package com.example.crudjavafx.model;

public class DetalleCompra {

    private final Producto producto;
    private int cantidad;
    private final double costoUnitario;
    private double subtotal;

    public DetalleCompra(Producto producto, int cantidad, double costoUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
        this.calcularSubtotal();
    }

    private void calcularSubtotal() {
        this.subtotal = this.costoUnitario * this.cantidad;
    }

    public void addQuantity(int qty) {
        this.cantidad += qty;
        calcularSubtotal();
    }

    public Producto getProducto() {
        return producto;
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getSubtotal() {
        return subtotal;
    }


}