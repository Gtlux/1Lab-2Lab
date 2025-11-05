package com.foodbooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Integer id;
    private Integer orderId;
    private Integer menuItemId;
    private String menuItemName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    private String notes;

    private String menuItemDescription;

    public OrderItem(Integer orderId, Integer menuItemId, String menuItemName, BigDecimal price, Integer quantity) {
        this.orderId = orderId;
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.price = price;
        this.quantity = quantity;
        calculateSubtotal();
    }

    public void calculateSubtotal() {
        if (price != null && quantity != null) {
            subtotal = price.multiply(BigDecimal.valueOf(quantity));
        } else {
            subtotal = BigDecimal.ZERO;
        }
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        calculateSubtotal();
    }
}
