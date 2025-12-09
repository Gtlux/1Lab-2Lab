package com.foodbooking.dto;

import com.foodbooking.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Integer id;
    private Integer orderId;
    private Integer menuItemId;
    private String menuItemName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;

    public static OrderItemDTO fromOrderItem(OrderItem item) {
        return new OrderItemDTO(
                item.getId(),
                item.getOrderId(),
                item.getMenuItemId(),
                item.getMenuItemName(),
                item.getPrice(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }
}
