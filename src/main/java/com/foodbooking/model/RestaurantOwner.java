package com.foodbooking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RestaurantOwner extends User {
    private String businessLicense;
    private LocalTime operatingHoursStart;
    private LocalTime operatingHoursEnd;
    private String cuisineType;
    private BigDecimal commissionRate;

    public RestaurantOwner(String username, String password, String email, String fullName, String phoneNumber) {
        super(username, password, email, fullName, phoneNumber, UserRole.RESTAURANT_OWNER);
        this.commissionRate = new BigDecimal("0.15");
    }

    public boolean isOperatingNow() {
        if (operatingHoursStart == null || operatingHoursEnd == null) {
            return true;
        }
        LocalTime now = LocalTime.now();
        return now.isAfter(operatingHoursStart) && now.isBefore(operatingHoursEnd);
    }

    public boolean isPeakHour() {
        LocalTime now = LocalTime.now();
        LocalTime lunchStart = LocalTime.of(11, 0);
        LocalTime lunchEnd = LocalTime.of(14, 0);
        LocalTime dinnerStart = LocalTime.of(18, 0);
        LocalTime dinnerEnd = LocalTime.of(21, 0);

        return (now.isAfter(lunchStart) && now.isBefore(lunchEnd)) ||
               (now.isAfter(dinnerStart) && now.isBefore(dinnerEnd));
    }
}
