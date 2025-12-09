package com.foodbooking.dto;

import com.foodbooking.model.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {
    private Integer id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String description;
    private Integer ownerId;
    private boolean active;

    public static RestaurantDTO fromRestaurant(Restaurant restaurant) {
        return new RestaurantDTO(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getPhoneNumber(),
                restaurant.getEmail(),
                restaurant.getDescription(),
                restaurant.getOwnerId(),
                restaurant.isActive()
        );
    }
}
