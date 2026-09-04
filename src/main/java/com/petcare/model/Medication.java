package com.petcare.model;

import java.time.LocalDateTime;

public class Medication {

    private int id;
    private String medicationCode;
    private String name;
    private String category;
    private String laboratory;
    private int totalStock;
    private int availableStock;
    private double unitPrice;
    private boolean active;
    private LocalDateTime createdAt;

    public Medication() {
    }

    public Medication(int id, String medicationCode, String name, String category,
                       String laboratory, int totalStock, int availableStock,
                       double unitPrice, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.medicationCode = medicationCode;
        this.name = name;
        this.category = category;
        this.laboratory = laboratory;
        this.totalStock = totalStock;
        this.availableStock = availableStock;
        this.unitPrice = unitPrice;
        this.active = active;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMedicationCode() {
        return medicationCode;
    }

    public void setMedicationCode(String medicationCode) {
        this.medicationCode = medicationCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLaboratory() {
        return laboratory;
    }

    public void setLaboratory(String laboratory) {
        this.laboratory = laboratory;
    }

    public int getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Medication{" +
                "id=" + id +
                ", medicationCode='" + medicationCode + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", laboratory='" + laboratory + '\'' +
                ", totalStock=" + totalStock +
                ", availableStock=" + availableStock +
                ", unitPrice=" + unitPrice +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}