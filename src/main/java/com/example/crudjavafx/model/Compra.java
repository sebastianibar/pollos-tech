package com.example.crudjavafx.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Compra {


    private double costo_total;
    private Proveedor proveedor;

    private ObservableList<DetalleCompra> detalles = FXCollections.observableArrayList();

    public Compra() {
        this.costo_total = 0.0;
    }

    public Compra(Proveedor proveedor) {
        this();
        this.proveedor = proveedor;
    }

    public void obtenerTotal() {
        double nuevoTotal = 0.0;
        for (DetalleCompra detalle : detalles) {
            nuevoTotal += detalle.getSubtotal();
        }
        this.costo_total = nuevoTotal;
    }

    // --- Getters and Setters ---


    public double getCosto_total() {
        return costo_total;
    }

    public void setCosto_total(double costo_total) {
        this.costo_total = costo_total;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public ObservableList<DetalleCompra> getDetalles() {
        return detalles;
    }

    public void setDetalles(ObservableList<DetalleCompra> detalles) {
        this.detalles = detalles;
        obtenerTotal();
    }
}