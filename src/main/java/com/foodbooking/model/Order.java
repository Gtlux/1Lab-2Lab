package com.foodbooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Integer id;
    private Integer clientId; // Foreign key to User (client)
    private Integer restaurantId; // Foreign key to Restaurant
    private Integer driverId; // Foreign key to User (driver), nullable
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deliveredAt;

    // Composition: Order has OrderItems
    private List<OrderItem> orderItems = new ArrayList<>();

    // Additional fields for display
    private String clientName;
    private String restaurantName;
    private String driverName;

    public Order(Integer clientId, Integer restaurantId, String deliveryAddress) {
        this.clientId = clientId;
        this.restaurantId = restaurantId;
        this.deliveryAddress = deliveryAddress;
        this.status = OrderStatus.PENDING;
        this.totalAmount = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.orderItems = new ArrayList<>();
    }

    public void addOrderItem(OrderItem item) {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        orderItems.add(item);
        calculateTotal();
    }

    public void removeOrderItem(OrderItem item) {
        if (orderItems != null) {
            orderItems.remove(item);
            calculateTotal();
        }
    }

    public void calculateTotal() {
        if (orderItems == null || orderItems.isEmpty()) {
            totalAmount = BigDecimal.ZERO;
            return;
        }
        totalAmount = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
        if (newStatus == OrderStatus.DELIVERED) {
            this.deliveredAt = LocalDateTime.now();
        }
    }
}
