package com.foodbooking.rest;

import com.foodbooking.dto.ApiResponse;
import com.foodbooking.dto.OrderDTO;
import com.foodbooking.model.OrderStatus;
import com.foodbooking.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @GetMapping("/orders/{driverId}")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getAssignedOrders(@PathVariable int driverId) {
        List<OrderDTO> orders = driverService.getAssignedOrders(driverId);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/orders/available")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getAvailableOrders() {
        List<OrderDTO> orders = driverService.getAvailableOrders();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/orders/detail/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable int orderId) {
        OrderDTO order = driverService.getOrderById(orderId);
        if (order != null) {
            return ResponseEntity.ok(ApiResponse.success(order));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<ApiResponse<String>> updateOrderStatus(
            @PathVariable int orderId,
            @RequestParam String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            boolean success = driverService.updateOrderStatus(orderId, orderStatus);

            if (success) {
                return ResponseEntity.ok(
                        ApiResponse.success("Užsakymo būsena atnaujinta sėkmingai", null));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Nepavyko atnaujinti būsenos"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Neteisinga būsena: " + status));
        }
    }

    @PostMapping("/orders/{orderId}/accept")
    public ResponseEntity<ApiResponse<String>> acceptOrder(
            @PathVariable int orderId,
            @RequestParam int driverId) {
        boolean success = driverService.acceptOrder(orderId, driverId);

        if (success) {
            return ResponseEntity.ok(
                    ApiResponse.success("Užsakymas priimtas sėkmingai", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Nepavyko priimti užsakymo"));
        }
    }
}
