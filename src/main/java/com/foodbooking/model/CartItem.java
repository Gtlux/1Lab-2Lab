package com.foodbooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private MenuItem menuItem;
    private Integer quantity;

    public BigDecimal getSubtotal() {
        if (menuItem == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return menuItem.getPrice().multiply(new BigDecimal(quantity));
    }

    public String getMenuItemName() {
        return menuItem != null ? menuItem.getName() : "";
    }

    public BigDecimal getPrice() {
        return menuItem != null ? menuItem.getPrice() : BigDecimal.ZERO;
    }
}
