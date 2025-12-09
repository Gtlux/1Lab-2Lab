package com.foodbooking.service;

import com.foodbooking.dao.*;
import com.foodbooking.dto.*;
import com.foodbooking.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {
    private final RestaurantDAO restaurantDAO;
    private final MenuItemDAO menuItemDAO;
    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final CancellationRequestDAO cancellationRequestDAO;

    public ClientService() {
        this.restaurantDAO = new RestaurantDAO();
        this.menuItemDAO = new MenuItemDAO();
        this.orderDAO = new OrderDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.cancellationRequestDAO = new CancellationRequestDAO();
    }

    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantDAO.getAllRestaurants().stream()
                .map(RestaurantDTO::fromRestaurant)
                .collect(Collectors.toList());
    }

    public RestaurantDTO getRestaurantById(int id) {
        Restaurant restaurant = restaurantDAO.getRestaurantById(id);
        return restaurant != null ? RestaurantDTO.fromRestaurant(restaurant) : null;
    }

    public List<MenuItemDTO> getMenuItemsByRestaurant(int restaurantId) {
        try {
            return menuItemDAO.getMenuItemsByRestaurantId(restaurantId).stream()
                    .map(MenuItemDTO::fromMenuItem)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting menu items for restaurant " + restaurantId + ": " + e.getMessage());
            e.printStackTrace();
            return List.of(); // Return empty list instead of null
        }
    }

    public List<MenuItemDTO> getAllAvailableMenuItems() {
        return menuItemDAO.getAllMenuItems().stream()
                .filter(MenuItem::isAvailable)
                .map(MenuItemDTO::fromMenuItem)
                .collect(Collectors.toList());
    }

    public OrderDTO createOrder(CreateOrderRequest request) {
        Order order = new Order(
                request.getClientId(),
                request.getRestaurantId(),
                request.getDeliveryAddress()
        );
        order.setNotes(request.getNotes());
        order.setStatus(OrderStatus.PENDING);

        if (!orderDAO.createOrder(order)) {
            return null;
        }

        int orderId = order.getId();
        boolean isPeakHour = isPeakHour();
        BigDecimal peakMultiplier = new BigDecimal("1.20");

        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            MenuItem menuItem = menuItemDAO.getMenuItemById(itemRequest.getMenuItemId());
            if (menuItem != null) {
                BigDecimal price = menuItem.getPrice();
                if (isPeakHour) {
                    price = price.multiply(peakMultiplier);
                }

                OrderItem orderItem = new OrderItem(
                        orderId,
                        menuItem.getId(),
                        menuItem.getName(),
                        price,
                        itemRequest.getQuantity()
                );
                orderItemDAO.createOrderItem(orderItem);
            }
        }

        order = orderDAO.getOrderById(orderId);
        return OrderDTO.fromOrder(order);
    }

    private boolean isPeakHour() {
        LocalTime now = LocalTime.now();
        LocalTime lunchStart = LocalTime.of(11, 0);
        LocalTime lunchEnd = LocalTime.of(14, 0);
        LocalTime dinnerStart = LocalTime.of(18, 0);
        LocalTime dinnerEnd = LocalTime.of(21, 0);

        return (now.isAfter(lunchStart) && now.isBefore(lunchEnd)) ||
               (now.isAfter(dinnerStart) && now.isBefore(dinnerEnd));
    }

    public List<OrderDTO> getClientOrders(int clientId) {
        try {
            List<Order> orders = orderDAO.getOrdersByClientId(clientId);
            return orders.stream()
                    .map(order -> {
                        try {
                            // Ensure orderItems is not null
                            if (order.getOrderItems() == null) {
                                order.setOrderItems(List.of());
                            }
                            return OrderDTO.fromOrder(order);
                        } catch (Exception e) {
                            System.err.println("Error converting order " + order.getId() + ": " + e.getMessage());
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting orders for client " + clientId + ": " + e.getMessage());
            e.printStackTrace();
            return List.of(); // Return empty list instead of null
        }
    }

    public OrderDTO getOrderById(int orderId) {
        Order order = orderDAO.getOrderById(orderId);
        return order != null ? OrderDTO.fromOrder(order) : null;
    }

    public CancellationRequestDTO requestCancellation(int orderId, int clientId, String reason) {
        CancellationRequest existingRequest = cancellationRequestDAO.getCancellationRequestByOrderId(orderId);

        if (existingRequest != null &&
            existingRequest.getStatus() == CancellationRequest.CancellationStatus.PENDING) {
            return null;
        }

        CancellationRequest request = new CancellationRequest(orderId, clientId, reason);
        int requestId = cancellationRequestDAO.createCancellationRequest(request);

        if (requestId > 0) {
            request.setId(requestId);
            return CancellationRequestDTO.fromCancellationRequest(request);
        }

        return null;
    }
}
