package com.foodbooking.rest;

import com.foodbooking.dto.*;
import com.foodbooking.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/restaurants")
    public ResponseEntity<ApiResponse<List<RestaurantDTO>>> getAllRestaurants() {
        List<RestaurantDTO> restaurants = clientService.getAllRestaurants();
        return ResponseEntity.ok(ApiResponse.success(restaurants));
    }

    @GetMapping("/restaurants/{id}")
    public ResponseEntity<ApiResponse<RestaurantDTO>> getRestaurantById(@PathVariable("id") int id) {
        RestaurantDTO restaurant = clientService.getRestaurantById(id);
        if (restaurant != null) {
            return ResponseEntity.ok(ApiResponse.success(restaurant));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/menu")
    public ResponseEntity<ApiResponse<List<MenuItemDTO>>> getAllMenuItems() {
        List<MenuItemDTO> menuItems = clientService.getAllAvailableMenuItems();
        return ResponseEntity.ok(ApiResponse.success(menuItems));
    }

    @GetMapping("/menu/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<List<MenuItemDTO>>> getMenuByRestaurant(
            @PathVariable("restaurantId") int restaurantId) {
        List<MenuItemDTO> menuItems = clientService.getMenuItemsByRestaurant(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(menuItems));
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(@RequestBody CreateOrderRequest request) {
        OrderDTO order = clientService.createOrder(request);
        if (order != null) {
            return ResponseEntity.ok(ApiResponse.success("Užsakymas sukurtas sėkmingai", order));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Nepavyko sukurti užsakymo"));
        }
    }

    @GetMapping("/orders/{clientId}")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getClientOrders(@PathVariable("clientId") int clientId) {
        List<OrderDTO> orders = clientService.getClientOrders(clientId);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/orders/detail/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable("orderId") int orderId) {
        OrderDTO order = clientService.getOrderById(orderId);
        if (order != null) {
            return ResponseEntity.ok(ApiResponse.success(order));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<ApiResponse<CancellationRequestDTO>> requestCancellation(
            @PathVariable("orderId") int orderId,
            @RequestParam("clientId") int clientId,
            @RequestParam("reason") String reason) {
        CancellationRequestDTO request = clientService.requestCancellation(orderId, clientId, reason);
        if (request != null) {
            return ResponseEntity.ok(
                    ApiResponse.success("Atšaukimo užklausa pateikta sėkmingai", request));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Užklausa jau pateikta arba įvyko klaida"));
        }
    }
}
