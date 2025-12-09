package com.foodbooking.service;

import com.foodbooking.dao.OrderDAO;
import com.foodbooking.dto.OrderDTO;
import com.foodbooking.model.Order;
import com.foodbooking.model.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService {
    private final OrderDAO orderDAO;

    public DriverService() {
        this.orderDAO = new OrderDAO();
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
        return orderDAO.updateOrder(order);
    }

    public boolean acceptOrder(int orderId, int driverId) {
        return orderDAO.assignDriver(orderId, driverId);
    }
}
