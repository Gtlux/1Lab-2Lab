package com.foodbooking.dao;

import com.foodbooking.model.Restaurant;
import com.foodbooking.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDAO {

    // CREATE
    public boolean createRestaurant(Restaurant restaurant) {
        String sql = "INSERT INTO restaurants (name, address, phone_number, email, description, owner_id, active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, restaurant.getName());
            pstmt.setString(2, restaurant.getAddress());
            pstmt.setString(3, restaurant.getPhoneNumber());
            pstmt.setString(4, restaurant.getEmail());
            pstmt.setString(5, restaurant.getDescription());
            pstmt.setInt(6, restaurant.getOwnerId());
            pstmt.setBoolean(7, restaurant.isActive());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        restaurant.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating restaurant: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // READ - Get by ID
    public Restaurant getRestaurantById(int id) {
        String sql = "SELECT * FROM restaurants WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractRestaurantFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting restaurant by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // READ - Get all restaurants
    public List<Restaurant> getAllRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT * FROM restaurants ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                restaurants.add(extractRestaurantFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all restaurants: " + e.getMessage());
            e.printStackTrace();
        }
        return restaurants;
    }

    // READ - Get active restaurants
    public List<Restaurant> getActiveRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT * FROM restaurants WHERE active = true ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                restaurants.add(extractRestaurantFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting active restaurants: " + e.getMessage());
            e.printStackTrace();
        }
        return restaurants;
    }

    // READ - Get by owner ID
    public List<Restaurant> getRestaurantsByOwnerId(int ownerId) {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT * FROM restaurants WHERE owner_id = ? ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ownerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                restaurants.add(extractRestaurantFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting restaurants by owner: " + e.getMessage());
            e.printStackTrace();
        }
        return restaurants;
    }

    // UPDATE
    public boolean updateRestaurant(Restaurant restaurant) {
        String sql = "UPDATE restaurants SET name = ?, address = ?, phone_number = ?, email = ?, " +
                     "description = ?, active = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, restaurant.getName());
            pstmt.setString(2, restaurant.getAddress());
            pstmt.setString(3, restaurant.getPhoneNumber());
            pstmt.setString(4, restaurant.getEmail());
            pstmt.setString(5, restaurant.getDescription());
            pstmt.setBoolean(6, restaurant.isActive());
            pstmt.setInt(7, restaurant.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating restaurant: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // DELETE
    public boolean deleteRestaurant(int id) {
        String sql = "DELETE FROM restaurants WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting restaurant: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Helper method
    private Restaurant extractRestaurantFromResultSet(ResultSet rs) throws SQLException {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(rs.getInt("id"));
        restaurant.setName(rs.getString("name"));
        restaurant.setAddress(rs.getString("address"));
        restaurant.setPhoneNumber(rs.getString("phone_number"));
        restaurant.setEmail(rs.getString("email"));
        restaurant.setDescription(rs.getString("description"));
        restaurant.setOwnerId(rs.getInt("owner_id"));
        restaurant.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        restaurant.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        restaurant.setActive(rs.getBoolean("active"));
        return restaurant;
    }
}
