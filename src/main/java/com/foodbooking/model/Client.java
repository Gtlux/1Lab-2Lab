package com.foodbooking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Client extends User {
    private String deliveryAddress;
    private String preferredPaymentMethod;
    private Integer loyaltyPoints;
    private BigDecimal totalSpent;

    public Client(String username, String password, String email, String fullName, String phoneNumber) {
        super(username, password, email, fullName, phoneNumber, UserRole.CLIENT);
        this.loyaltyPoints = 0;
        this.totalSpent = BigDecimal.ZERO;
    }

    public void addLoyaltyPoints(int points) {
        if (this.loyaltyPoints == null) {
            this.loyaltyPoints = 0;
        }
        this.loyaltyPoints += points;
    }

    public void addToTotalSpent(BigDecimal amount) {
        if (this.totalSpent == null) {
            this.totalSpent = BigDecimal.ZERO;
        }
        this.totalSpent = this.totalSpent.add(amount);
    }

    public boolean canRedeemPoints(int points) {
        return this.loyaltyPoints != null && this.loyaltyPoints >= points;
    }
}
