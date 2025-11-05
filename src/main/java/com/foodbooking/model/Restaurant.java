package com.foodbooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    private Integer id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String description;
    private Integer ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    private List<MenuItem> menuItems = new ArrayList<>();

    public Restaurant(String name, String address, String phoneNumber, String email, String description, Integer ownerId) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.description = description;
        this.ownerId = ownerId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
        this.menuItems = new ArrayList<>();
    }

    public void addMenuItem(MenuItem item) {
        if (menuItems == null) {
            menuItems = new ArrayList<>();
        }
        menuItems.add(item);
    }

    public void removeMenuItem(MenuItem item) {
        if (menuItems != null) {
            menuItems.remove(item);
        }
    }
}
