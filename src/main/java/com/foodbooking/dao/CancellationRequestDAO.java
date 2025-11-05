package com.foodbooking.dao;

import com.foodbooking.model.CancellationRequest;
import com.foodbooking.model.CancellationRequest.CancellationStatus;
import com.foodbooking.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CancellationRequestDAO {

    public int createCancellationRequest(CancellationRequest request) {
        String sql = "INSERT INTO cancellation_requests (order_id, client_id, reason, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, request.getOrderId());
            pstmt.setInt(2, request.getClientId());
            pstmt.setString(3, request.getReason());
            pstmt.setString(4, request.getStatus().name());
            pstmt.setTimestamp(5, Timestamp.valueOf(request.getCreatedAt()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public CancellationRequest getCancellationRequestByOrderId(int orderId) {
        String sql = "SELECT cr.*, " +
                     "u1.full_name as client_name, " +
                     "u2.full_name as reviewer_name, " +
                     "o.restaurant_id, " +
                     "r.name as restaurant_name " +
                     "FROM cancellation_requests cr " +
                     "JOIN users u1 ON cr.client_id = u1.id " +
                     "LEFT JOIN users u2 ON cr.reviewed_by = u2.id " +
                     "JOIN orders o ON cr.order_id = o.id " +
                     "JOIN restaurants r ON o.restaurant_id = r.id " +
                     "WHERE cr.order_id = ? " +
                     "ORDER BY cr.created_at DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractCancellationRequestFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<CancellationRequest> getPendingRequestsByRestaurantId(int restaurantId) {
        String sql = "SELECT cr.*, " +
                     "u1.full_name as client_name, " +
                     "u2.full_name as reviewer_name, " +
                     "o.restaurant_id, " +
                     "r.name as restaurant_name " +
                     "FROM cancellation_requests cr " +
                     "JOIN users u1 ON cr.client_id = u1.id " +
                     "LEFT JOIN users u2 ON cr.reviewed_by = u2.id " +
                     "JOIN orders o ON cr.order_id = o.id " +
                     "JOIN restaurants r ON o.restaurant_id = r.id " +
                     "WHERE o.restaurant_id = ? AND cr.status = 'PENDING' " +
                     "ORDER BY cr.created_at DESC";

        List<CancellationRequest> requests = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, restaurantId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                requests.add(extractCancellationRequestFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public List<CancellationRequest> getAllPendingRequests() {
        String sql = "SELECT cr.*, " +
                     "u1.full_name as client_name, " +
                     "u2.full_name as reviewer_name, " +
                     "o.restaurant_id, " +
                     "r.name as restaurant_name " +
                     "FROM cancellation_requests cr " +
                     "JOIN users u1 ON cr.client_id = u1.id " +
                     "LEFT JOIN users u2 ON cr.reviewed_by = u2.id " +
                     "JOIN orders o ON cr.order_id = o.id " +
                     "JOIN restaurants r ON o.restaurant_id = r.id " +
                     "WHERE cr.status = 'PENDING' " +
                     "ORDER BY cr.created_at DESC";

        List<CancellationRequest> requests = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                requests.add(extractCancellationRequestFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public boolean approveCancellationRequest(int requestId, int reviewedBy, String reviewNote) {
        String sql = "UPDATE cancellation_requests SET status = 'APPROVED', " +
                     "reviewed_by = ?, review_note = ?, reviewed_at = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reviewedBy);
            pstmt.setString(2, reviewNote);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(4, requestId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean rejectCancellationRequest(int requestId, int reviewedBy, String reviewNote) {
        String sql = "UPDATE cancellation_requests SET status = 'REJECTED', " +
                     "reviewed_by = ?, review_note = ?, reviewed_at = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reviewedBy);
            pstmt.setString(2, reviewNote);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(4, requestId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private CancellationRequest extractCancellationRequestFromResultSet(ResultSet rs) throws SQLException {
        CancellationRequest request = new CancellationRequest();
        request.setId(rs.getInt("id"));
        request.setOrderId(rs.getInt("order_id"));
        request.setClientId(rs.getInt("client_id"));
        request.setReason(rs.getString("reason"));
        request.setStatus(CancellationStatus.valueOf(rs.getString("status")));

        int reviewedBy = rs.getInt("reviewed_by");
        request.setReviewedBy(rs.wasNull() ? null : reviewedBy);

        request.setReviewNote(rs.getString("review_note"));
        request.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        Timestamp reviewedAt = rs.getTimestamp("reviewed_at");
        request.setReviewedAt(reviewedAt != null ? reviewedAt.toLocalDateTime() : null);

        request.setClientName(rs.getString("client_name"));
        request.setReviewerName(rs.getString("reviewer_name"));
        request.setRestaurantId(rs.getInt("restaurant_id"));
        request.setRestaurantName(rs.getString("restaurant_name"));

        return request;
    }
}
