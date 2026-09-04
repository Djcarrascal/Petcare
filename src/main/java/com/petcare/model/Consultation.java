package com.petcare.model;

import com.petcare.enums.QueryStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Consultation {

    private int id;
    private Owner owner;
    private Pet pet;
    private User veterinarian;
    private LocalDateTime consultationDate;
    private String reason;
    private String diagnosis;
    private String treatment;
    private QueryStatus status;
    private double totalCost;
    private LocalDateTime createdAt;
    private List<ConsultationMedication> medicationsUsed;

    public Consultation() {
        this.medicationsUsed = new ArrayList<>();
    }

    public Consultation(int id, Owner owner, Pet pet, User veterinarian,
                         LocalDateTime consultationDate, String reason, String diagnosis,
                         String treatment, QueryStatus status, double totalCost,
                         LocalDateTime createdAt) {
        this.id = id;
        this.owner = owner;
        this.pet = pet;
        this.veterinarian = veterinarian;
        this.consultationDate = consultationDate;
        this.reason = reason;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.status = status;
        this.totalCost = totalCost;
        this.createdAt = createdAt;
        this.medicationsUsed = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public User getVeterinarian() {
        return veterinarian;
    }

    public void setVeterinarian(User veterinarian) {
        this.veterinarian = veterinarian;
    }

    public LocalDateTime getConsultationDate() {
        return consultationDate;
    }

    public void setConsultationDate(LocalDateTime consultationDate) {
        this.consultationDate = consultationDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public QueryStatus getStatus() {
        return status;
    }

    public void setStatus(QueryStatus status) {
        this.status = status;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ConsultationMedication> getMedicationsUsed() {
        return medicationsUsed;
    }

    public void setMedicationsUsed(List<ConsultationMedication> medicationsUsed) {
        this.medicationsUsed = medicationsUsed;
    }

    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + id +
                ", owner=" + (owner != null ? owner.getName() : "null") +
                ", pet=" + (pet != null ? pet.getName() : "null") +
                ", veterinarian=" + (veterinarian != null ? veterinarian.getName() : "null") +
                ", consultationDate=" + consultationDate +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                ", totalCost=" + totalCost +
                '}';
    }
}