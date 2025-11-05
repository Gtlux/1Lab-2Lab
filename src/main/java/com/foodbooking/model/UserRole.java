package com.foodbooking.model;

public enum UserRole {
    CLIENT("Klientas"),
    RESTAURANT_OWNER("Restorano savininkas"),
    DRIVER("Vairuotojas"),
    ADMINISTRATOR("Administratorius");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
