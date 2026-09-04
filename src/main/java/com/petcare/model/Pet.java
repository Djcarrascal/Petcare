package com.petcare.model;

import java.time.LocalDateTime;

public class Pet {

    private int id;
    private String medicalRecordNumber;
    private String name;
    private String species;
    private String breed;
    private int age;
    private Owner owner;
    private boolean active;
    private LocalDateTime createdAt;

    public Pet() {
    }

    public Pet(int id, String medicalRecordNumber, String name, String species,
               String breed, int age, Owner owner, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.medicalRecordNumber = medicalRecordNumber;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.owner = owner;
        this.active = active;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
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
        return "Pet{" +
                "id=" + id +
                ", medicalRecordNumber='" + medicalRecordNumber + '\'' +
                ", name='" + name + '\'' +
                ", species='" + species + '\'' +
                ", breed='" + breed + '\'' +
                ", age=" + age +
                ", owner=" + (owner != null ? owner.getName() : "null") +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}