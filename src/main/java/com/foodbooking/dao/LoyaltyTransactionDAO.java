package com.foodbooking.dao;

import com.foodbooking.model.LoyaltyTransaction;
import com.foodbooking.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoyaltyTransactionDAO {

    public int createTransaction(LoyaltyTransaction transaction) {
        String query = "INSERT INTO loyalty_transactions (client_id, order_id, points_earned, " +
                      "points_redeemed, order_amount, transaction_type, description, created_at) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, transaction.getClientId());
            stmt.setInt(2, transaction.getOrderId());
            stmt.setInt(3, transaction.getPointsEarned() != null ? transaction.getPointsEarned() : 0);
            stmt.setInt(4, transaction.getPointsRedeemed() != null ? transaction.getPointsRedeemed() : 0);
            stmt.setBigDecimal(5, transaction.getOrderAmount());
            stmt.setString(6, transaction.getTransactionType());
            stmt.setString(7, transaction.getDescription());
            stmt.setTimestamp(8, Timestamp.valueOf(transaction.getCreatedAt()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<LoyaltyTransaction> getTransactionsByClientId(int clientId) {
        String query = "SELECT * FROM loyalty_transactions WHERE client_id = ? ORDER BY created_at DESC";
        List<LoyaltyTransaction> transactions = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public int getTotalPointsByClientId(int clientId) {
        String query = "SELECT " +
                      "(COALESCE(SUM(points_earned), 0) - COALESCE(SUM(points_redeemed), 0)) as total_points " +
                      "FROM loyalty_transactions WHERE client_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total_points");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean updateClientLoyaltyPoints(int clientId, int points) {
        String query = "UPDATE users SET loyalty_points = COALESCE(loyalty_points, 0) + ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, points);
            stmt.setInt(2, clientId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private LoyaltyTransaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        LoyaltyTransaction transaction = new LoyaltyTransaction();
        transaction.setId(rs.getInt("id"));
        transaction.setClientId(rs.getInt("client_id"));
        transaction.setOrderId(rs.getInt("order_id"));
        transaction.setPointsEarned(rs.getInt("points_earned"));
        transaction.setPointsRedeemed(rs.getInt("points_redeemed"));
        transaction.setOrderAmount(rs.getBigDecimal("order_amount"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setDescription(rs.getString("description"));
        transaction.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return transaction;
    }
}
