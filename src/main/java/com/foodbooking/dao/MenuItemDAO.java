package com.foodbooking.dao;

import com.foodbooking.model.MenuItem;
import com.foodbooking.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO {

    // CREATE
    public boolean createMenuItem(MenuItem menuItem) {
        String sql = "INSERT INTO menu_items (restaurant_id, name, description, price, category, available, image_url) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, menuItem.getRestaurantId());
            pstmt.setString(2, menuItem.getName());
            pstmt.setString(3, menuItem.getDescription());
            pstmt.setBigDecimal(4, menuItem.getPrice());
            pstmt.setString(5, menuItem.getCategory());
            pstmt.setBoolean(6, menuItem.isAvailable());
            pstmt.setString(7, menuItem.getImageUrl());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        menuItem.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating menu item: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // READ - Get by ID
    public MenuItem getMenuItemById(int id) {
        String sql = "SELECT * FROM menu_items WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractMenuItemFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting menu item by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // READ - Get all menu items
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();
        String sql = "SELECT * FROM menu_items ORDER BY restaurant_id, category, name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                menuItems.add(extractMenuItemFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all menu items: " + e.getMessage());
            e.printStackTrace();
        }
        return menuItems;
    }

    // READ - Get by restaurant ID
    public List<MenuItem> getMenuItemsByRestaurantId(int restaurantId) {
        List<MenuItem> menuItems = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE restaurant_id = ? ORDER BY category, name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, restaurantId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                menuItems.add(extractMenuItemFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting menu items by restaurant: " + e.getMessage());
            e.printStackTrace();
        }
        return menuItems;
    }

    // READ - Get available items by restaurant ID
    public List<MenuItem> getAvailableMenuItems(int restaurantId) {
        List<MenuItem> menuItems = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE restaurant_id = ? AND available = true ORDER BY category, name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, restaurantId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                menuItems.add(extractMenuItemFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting available menu items: " + e.getMessage());
            e.printStackTrace();
        }
        return menuItems;
    }

    // UPDATE
    public boolean updateMenuItem(MenuItem menuItem) {
        String sql = "UPDATE menu_items SET restaurant_id = ?, name = ?, description = ?, price = ?, " +
                     "category = ?, available = ?, image_url = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, menuItem.getRestaurantId());
            pstmt.setString(2, menuItem.getName());
            pstmt.setString(3, menuItem.getDescription());
            pstmt.setBigDecimal(4, menuItem.getPrice());
            pstmt.setString(5, menuItem.getCategory());
            pstmt.setBoolean(6, menuItem.isAvailable());
            pstmt.setString(7, menuItem.getImageUrl());
            pstmt.setInt(8, menuItem.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating menu item: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // DELETE
    public boolean deleteMenuItem(int id) {
        String sql = "DELETE FROM menu_items WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting menu item: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Helper method
    private MenuItem extractMenuItemFromResultSet(ResultSet rs) throws SQLException {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(rs.getInt("id"));
        menuItem.setRestaurantId(rs.getInt("restaurant_id"));
        menuItem.setName(rs.getString("name"));
        menuItem.setDescription(rs.getString("description"));
        menuItem.setPrice(rs.getBigDecimal("price"));
        menuItem.setCategory(rs.getString("category"));
        menuItem.setAvailable(rs.getBoolean("available"));
        menuItem.setImageUrl(rs.getString("image_url"));
        menuItem.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        menuItem.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return menuItem;
    }
}
