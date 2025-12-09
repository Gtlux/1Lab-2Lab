package com.foodbooking.dto;

import com.foodbooking.model.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDTO {
    private Integer id;
    private Integer restaurantId;
    private String restaurantName;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private boolean available;
    private String imageUrl;

    public static MenuItemDTO fromMenuItem(MenuItem menuItem) {
        return new MenuItemDTO(
                menuItem.getId(),
                menuItem.getRestaurantId(),
                null,
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getCategory(),
                menuItem.isAvailable(),
                menuItem.getImageUrl()
        );
    }
}
