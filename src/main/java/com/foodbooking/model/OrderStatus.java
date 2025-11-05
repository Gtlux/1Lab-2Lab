package com.foodbooking.model;

public enum OrderStatus {
    PENDING("Laukiama patvirtinimo"),
    CONFIRMED("Patvirtinta"),
    PREPARING("Ruošiama"),
    READY("Paruošta"),
    PICKED_UP("Paimta vairuotojo"),
    DELIVERING("Pristatoma"),
    DELIVERED("Pristatyta"),
    CANCELLED("Atšaukta");

    private final String displayName;

    OrderStatus(String displayName) {
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
