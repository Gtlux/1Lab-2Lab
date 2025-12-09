package com.foodbooking.dto;

import com.foodbooking.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private String message;
    private UserDTO user;

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
