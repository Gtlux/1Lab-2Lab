package com.foodbooking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Administrator extends User {
    private String adminLevel;
    private String department;
    private boolean superAdmin;

    public Administrator(String username, String password, String email, String fullName, String phoneNumber) {
        super(username, password, email, fullName, phoneNumber, UserRole.ADMINISTRATOR);
        this.adminLevel = "STANDARD";
        this.superAdmin = false;
    }

    public boolean canDeleteUsers() {
        return superAdmin || "SUPER".equals(adminLevel);
    }

    public boolean canModifySystemSettings() {
        return superAdmin;
    }

    public String getFullTitle() {
        return superAdmin ? "Super Administratorius" : "Administratorius (" + adminLevel + ")";
    }
}
