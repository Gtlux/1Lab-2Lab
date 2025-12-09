package com.foodbooking.service;

import com.foodbooking.dao.*;
import com.foodbooking.dto.*;
import com.foodbooking.model.*;
import org.springframework.stereotype.Service;

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
        return menuItemDAO.getMenuItemsByRestaurantId(restaurantId).stream()
                .map(MenuItemDTO::fromMenuItem)
                .collect(Collectors.toList());
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

        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            MenuItem menuItem = menuItemDAO.getMenuItemById(itemRequest.getMenuItemId());
            if (menuItem != null) {
                OrderItem orderItem = new OrderItem(
                        orderId,
                        menuItem.getId(),
                        menuItem.getName(),
                        menuItem.getPrice(),
                        itemRequest.getQuantity()
                );
                orderItemDAO.createOrderItem(orderItem);
            }
        }

        order = orderDAO.getOrderById(orderId);
        return OrderDTO.fromOrder(order);
    }

    public List<OrderDTO> getClientOrders(int clientId) {
        return orderDAO.getOrdersByClientId(clientId).stream()
                .map(OrderDTO::fromOrder)
                .collect(Collectors.toList());
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
