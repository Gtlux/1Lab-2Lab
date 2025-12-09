package com.foodbooking.service;

import com.foodbooking.dao.LoyaltyTransactionDAO;
import com.foodbooking.dao.OrderDAO;
import com.foodbooking.dto.OrderDTO;
import com.foodbooking.model.LoyaltyTransaction;
import com.foodbooking.model.Order;
import com.foodbooking.model.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService {
    private final OrderDAO orderDAO;
    private final LoyaltyTransactionDAO loyaltyTransactionDAO;

    public DriverService() {
        this.orderDAO = new OrderDAO();
        this.loyaltyTransactionDAO = new LoyaltyTransactionDAO();
    }

    public List<OrderDTO> getAssignedOrders(int driverId) {
        return orderDAO.getOrdersByDriverId(driverId).stream()
                .map(OrderDTO::fromOrder)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getAvailableOrders() {
        return orderDAO.getAvailableOrdersForDrivers().stream()
                .map(OrderDTO::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(int orderId) {
        Order order = orderDAO.getOrderById(orderId);
        return order != null ? OrderDTO.fromOrder(order) : null;
    }

    public boolean updateOrderStatus(int orderId, OrderStatus newStatus) {
        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            return false;
        }

        order.updateStatus(newStatus);
        boolean updated = orderDAO.updateOrder(order);

        if (updated && newStatus == OrderStatus.DELIVERED) {
            awardLoyaltyPoints(order);
        }

        return updated;
    }

    private void awardLoyaltyPoints(Order order) {
        if (order.getClientId() == null || order.getTotalAmount() == null) {
            return;
        }

        LoyaltyTransaction transaction = LoyaltyTransaction.createEarningTransaction(
                order.getClientId(),
                order.getId(),
                order.getTotalAmount()
        );

        int transactionId = loyaltyTransactionDAO.createTransaction(transaction);
        if (transactionId > 0) {
            int pointsEarned = transaction.getPointsEarned();
            loyaltyTransactionDAO.updateClientLoyaltyPoints(order.getClientId(), pointsEarned);
        }
    }

    public boolean acceptOrder(int orderId, int driverId) {
        return orderDAO.assignDriver(orderId, driverId);
    }
}
