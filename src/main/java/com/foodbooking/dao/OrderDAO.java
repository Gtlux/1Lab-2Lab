package com.foodbooking.dao;

import com.foodbooking.model.Order;
import com.foodbooking.model.OrderStatus;
import com.foodbooking.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public boolean createOrder(Order order) {
        String sql = "INSERT INTO orders (client_id, restaurant_id, driver_id, status, total_amount, " +
                     "delivery_address, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, order.getClientId());
            pstmt.setInt(2, order.getRestaurantId());

            if (order.getDriverId() != null) {
                pstmt.setInt(3, order.getDriverId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setString(4, order.getStatus().name());
            pstmt.setBigDecimal(5, order.getTotalAmount());
            pstmt.setString(6, order.getDeliveryAddress());
            pstmt.setString(7, order.getNotes());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Order getOrderById(int id) {
        String sql = "SELECT o.*, " +
                     "u1.full_name as client_name, " +
                     "r.name as restaurant_name, " +
                     "u2.full_name as driver_name " +
                     "FROM orders o " +
                     "LEFT JOIN users u1 ON o.client_id = u1.id " +
                     "LEFT JOIN restaurants r ON o.restaurant_id = r.id " +
                     "LEFT JOIN users u2 ON o.driver_id = u2.id " +
                     "WHERE o.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractOrderFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting order by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, " +
                     "u1.full_name as client_name, " +
                     "r.name as restaurant_name, " +
                     "u2.full_name as driver_name " +
                     "FROM orders o " +
                     "LEFT JOIN users u1 ON o.client_id = u1.id " +
                     "LEFT JOIN restaurants r ON o.restaurant_id = r.id " +
                     "LEFT JOIN users u2 ON o.driver_id = u2.id " +
                     "ORDER BY o.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all orders: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    public List<Order> getOrdersByClientId(int clientId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, " +
                     "u1.full_name as client_name, " +
                     "r.name as restaurant_name, " +
                     "u2.full_name as driver_name " +
                     "FROM orders o " +
                     "LEFT JOIN users u1 ON o.client_id = u1.id " +
                     "LEFT JOIN restaurants r ON o.restaurant_id = r.id " +
                     "LEFT JOIN users u2 ON o.driver_id = u2.id " +
                     "WHERE o.client_id = ? " +
                     "ORDER BY o.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by client: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    public List<Order> getOrdersByRestaurantId(int restaurantId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, " +
                     "u1.full_name as client_name, " +
                     "r.name as restaurant_name, " +
                     "u2.full_name as driver_name " +
                     "FROM orders o " +
                     "LEFT JOIN users u1 ON o.client_id = u1.id " +
                     "LEFT JOIN restaurants r ON o.restaurant_id = r.id " +
                     "LEFT JOIN users u2 ON o.driver_id = u2.id " +
                     "WHERE o.restaurant_id = ? " +
                     "ORDER BY o.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, restaurantId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by restaurant: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    public List<Order> getOrdersByDriverId(int driverId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, " +
                     "u1.full_name as client_name, " +
                     "r.name as restaurant_name, " +
                     "u2.full_name as driver_name " +
                     "FROM orders o " +
                     "LEFT JOIN users u1 ON o.client_id = u1.id " +
                     "LEFT JOIN restaurants r ON o.restaurant_id = r.id " +
                     "LEFT JOIN users u2 ON o.driver_id = u2.id " +
                     "WHERE o.driver_id = ? " +
                     "ORDER BY o.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, driverId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by driver: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    public List<Order> getAvailableOrdersForDrivers() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, " +
                     "u1.full_name as client_name, " +
                     "r.name as restaurant_name, " +
                     "u2.full_name as driver_name " +
                     "FROM orders o " +
                     "LEFT JOIN users u1 ON o.client_id = u1.id " +
                     "LEFT JOIN restaurants r ON o.restaurant_id = r.id " +
                     "LEFT JOIN users u2 ON o.driver_id = u2.id " +
                     "WHERE o.status = 'READY' AND o.driver_id IS NULL " +
                     "ORDER BY o.created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting available orders: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    public boolean updateOrder(Order order) {
        String sql = "UPDATE orders SET client_id = ?, restaurant_id = ?, driver_id = ?, status = ?, " +
                     "total_amount = ?, delivery_address = ?, notes = ?, delivered_at = ?, " +
                     "updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, order.getClientId());
            pstmt.setInt(2, order.getRestaurantId());

            if (order.getDriverId() != null) {
                pstmt.setInt(3, order.getDriverId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setString(4, order.getStatus().name());
            pstmt.setBigDecimal(5, order.getTotalAmount());
            pstmt.setString(6, order.getDeliveryAddress());
            pstmt.setString(7, order.getNotes());

            if (order.getDeliveredAt() != null) {
                pstmt.setTimestamp(8, Timestamp.valueOf(order.getDeliveredAt()));
            } else {
                pstmt.setNull(8, Types.TIMESTAMP);
            }

            pstmt.setInt(9, order.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean assignDriver(int orderId, int driverId) {
        String sql = "UPDATE orders SET driver_id = ?, status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, driverId);
            pstmt.setString(2, OrderStatus.PICKED_UP.name());
            pstmt.setInt(3, orderId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error assigning driver: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteOrder(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting order: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setClientId(rs.getInt("client_id"));
        order.setRestaurantId(rs.getInt("restaurant_id"));

        int driverId = rs.getInt("driver_id");
        if (!rs.wasNull()) {
            order.setDriverId(driverId);
        }

        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setDeliveryAddress(rs.getString("delivery_address"));
        order.setNotes(rs.getString("notes"));
        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        order.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        Timestamp deliveredAt = rs.getTimestamp("delivered_at");
        if (deliveredAt != null) {
            order.setDeliveredAt(deliveredAt.toLocalDateTime());
        }

        order.setClientName(rs.getString("client_name"));
        order.setRestaurantName(rs.getString("restaurant_name"));
        order.setDriverName(rs.getString("driver_name"));

        return order;
    }
}
