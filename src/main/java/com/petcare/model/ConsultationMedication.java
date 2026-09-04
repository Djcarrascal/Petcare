package com.petcare.model;

public class ConsultationMedication {

    private int id;
    private Medication medication;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    public ConsultationMedication() {
    }

    public ConsultationMedication(int id, Medication medication, int quantity,
                                   double unitPrice, double subtotal) {
        this.id = id;
        this.medication = medication;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "ConsultationMedication{" +
                "medication=" + (medication != null ? medication.getName() : "null") +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", subtotal=" + subtotal +
                '}';
    }
}