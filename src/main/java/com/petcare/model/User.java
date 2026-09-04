package com.petcare.model;

import com.petcare.enums.StateUser;
import com.petcare.enums.RoleUser;
import java.time.LocalDateTime;

public class User {

    private int id;
    private String username;
    private String password;
    private String name;
    private RoleUser role;
    private StateUser status;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(int id, String username, String password, String name,
                RoleUser role, StateUser status, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RoleUser getRole() {
        return role;
    }

    public void setRole(RoleUser role) {
        this.role = role;
    }

    public StateUser getStatus() {
        return status;
    }

    public void setStatus(StateUser status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}