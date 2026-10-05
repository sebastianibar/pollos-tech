package com.example.crudjavafx.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Pedido {

    private double total;
    private String metodo_pago;

    private Empleado empleado;
    private Cliente cliente;

    private ObservableList<DetallePedido> detalles = FXCollections.observableArrayList();

    public Pedido() {
    }

    public Pedido(Empleado empleado, Cliente cliente, String metodo_pago) {
        this();
        this.empleado = empleado;
        this.cliente = cliente;
        this.metodo_pago = metodo_pago;
        this.total = 0.0;
    }

    public void obtenerTotal() {
        double nuevoTotal = 0.0;
        for (DetallePedido detalle : detalles) {
            nuevoTotal += detalle.getSubtotal();
        }
        this.total = nuevoTotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getMetodo_pago() {
        return metodo_pago;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public ObservableList<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(ObservableList<DetallePedido> detalles) {
        this.detalles = detalles;
        obtenerTotal();
    }
}