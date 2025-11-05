package com.foodbooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phoneNumber;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    private Integer restaurantId;

    public User(String username, String password, String email, String fullName, String phoneNumber, UserRole role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
    }

    public boolean isClient() {
        return role == UserRole.CLIENT;
    }

    public boolean isRestaurantOwner() {
        return role == UserRole.RESTAURANT_OWNER;
    }

    public boolean isDriver() {
        return role == UserRole.DRIVER;
    }

    public boolean isAdministrator() {
        return role == UserRole.ADMINISTRATOR;
    }
}
