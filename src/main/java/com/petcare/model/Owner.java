package com.petcare.model;

import java.time.LocalDateTime;

public class Owner {

    private int id;
    private String name;
    private String document;
    private String phone;
    private String email;
    private String address;
    private boolean active;
    private LocalDateTime createdAt;

    public Owner() {
    }

    public Owner(int id, String name, String document, String phone,
                 String email, String address, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.document = document;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.active = active;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        return "Owner{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", document='" + document + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}