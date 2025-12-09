package com.foodbooking.dto;

import com.foodbooking.model.User;
import com.foodbooking.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private UserRole role;
    private Integer restaurantId;
    private boolean active;
    private LocalDateTime createdAt;

    public static UserDTO fromUser(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getRestaurantId(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}
