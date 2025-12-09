package com.foodbooking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Driver extends User {
    private String vehicleType;
    private String vehiclePlateNumber;
    private String driverLicense;
    private boolean available;
    private BigDecimal rating;
    private Integer completedDeliveries;
    private BigDecimal totalEarnings;

    public Driver(String username, String password, String email, String fullName, String phoneNumber) {
        super(username, password, email, fullName, phoneNumber, UserRole.DRIVER);
        this.available = true;
        this.rating = new BigDecimal("5.0");
        this.completedDeliveries = 0;
        this.totalEarnings = BigDecimal.ZERO;
    }

    public void completeDelivery(BigDecimal earnings) {
        if (this.completedDeliveries == null) {
            this.completedDeliveries = 0;
        }
        if (this.totalEarnings == null) {
            this.totalEarnings = BigDecimal.ZERO;
        }
        this.completedDeliveries++;
        this.totalEarnings = this.totalEarnings.add(earnings);
    }

    public void updateRating(BigDecimal newRating) {
        if (this.rating == null) {
            this.rating = newRating;
        } else {
            this.rating = this.rating.add(newRating).divide(new BigDecimal("2"));
        }
    }

    public String getAvailabilityStatus() {
        return available ? "Prieinamas" : "Užimtas";
    }
}
