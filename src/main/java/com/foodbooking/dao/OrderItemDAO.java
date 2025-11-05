package com.foodbooking.dao;

import com.foodbooking.model.OrderItem;
import com.foodbooking.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {

    public boolean createOrderItem(OrderItem orderItem) {
        String sql = "INSERT INTO order_items (order_id, menu_item_id, menu_item_name, price, quantity, subtotal, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, orderItem.getOrderId());
            pstmt.setInt(2, orderItem.getMenuItemId());
            pstmt.setString(3, orderItem.getMenuItemName());
            pstmt.setBigDecimal(4, orderItem.getPrice());
            pstmt.setInt(5, orderItem.getQuantity());
            pstmt.setBigDecimal(6, orderItem.getSubtotal());
            pstmt.setString(7, orderItem.getNotes());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderItem.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating order item: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public OrderItem getOrderItemById(int id) {
        String sql = "SELECT * FROM order_items WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractOrderItemFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting order item by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                orderItems.add(extractOrderItemFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting order items by order ID: " + e.getMessage());
            e.printStackTrace();
        }
        return orderItems;
    }

    public boolean updateOrderItem(OrderItem orderItem) {
        String sql = "UPDATE order_items SET order_id = ?, menu_item_id = ?, menu_item_name = ?, " +
                     "price = ?, quantity = ?, subtotal = ?, notes = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderItem.getOrderId());
            pstmt.setInt(2, orderItem.getMenuItemId());
            pstmt.setString(3, orderItem.getMenuItemName());
            pstmt.setBigDecimal(4, orderItem.getPrice());
            pstmt.setInt(5, orderItem.getQuantity());
            pstmt.setBigDecimal(6, orderItem.getSubtotal());
            pstmt.setString(7, orderItem.getNotes());
            pstmt.setInt(8, orderItem.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order item: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteOrderItem(int id) {
        String sql = "DELETE FROM order_items WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting order item: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteOrderItemsByOrderId(int orderId) {
        String sql = "DELETE FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            return pstmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            System.err.println("Error deleting order items: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private OrderItem extractOrderItemFromResultSet(ResultSet rs) throws SQLException {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(rs.getInt("id"));
        orderItem.setOrderId(rs.getInt("order_id"));
        orderItem.setMenuItemId(rs.getInt("menu_item_id"));
        orderItem.setMenuItemName(rs.getString("menu_item_name"));
        orderItem.setPrice(rs.getBigDecimal("price"));
        orderItem.setQuantity(rs.getInt("quantity"));
        orderItem.setSubtotal(rs.getBigDecimal("subtotal"));
        orderItem.setNotes(rs.getString("notes"));
        return orderItem;
    }
}
