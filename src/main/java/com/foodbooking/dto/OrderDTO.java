package com.foodbooking.dto;

import com.foodbooking.model.Order;
import com.foodbooking.model.OrderItem;
import com.foodbooking.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Integer id;
    private Integer clientId;
    private String clientName;
    private Integer restaurantId;
    private String restaurantName;
    private Integer driverId;
    private String driverName;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
    private List<OrderItemDTO> orderItems;

    public static OrderDTO fromOrder(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getClientId(),
                order.getClientName(),
                order.getRestaurantId(),
                order.getRestaurantName(),
                order.getDriverId(),
                order.getDriverName(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getDeliveryAddress(),
                order.getNotes(),
                order.getCreatedAt(),
                order.getDeliveredAt(),
                order.getOrderItems() != null ?
                    order.getOrderItems().stream()
                        .map(OrderItemDTO::fromOrderItem)
                        .collect(Collectors.toList()) : null
        );
    }
}
