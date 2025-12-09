package com.foodbooking.dao;

import com.foodbooking.model.Review;
import com.foodbooking.model.Review.ReviewedEntityType;
import com.foodbooking.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public int createReview(Review review) {
        String query = "INSERT INTO reviews (reviewer_id, reviewed_entity_type, reviewed_entity_id, " +
                      "order_id, rating, comment, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, review.getReviewerId());
            stmt.setString(2, review.getReviewedEntityType().name());
            stmt.setInt(3, review.getReviewedEntityId());
            stmt.setObject(4, review.getOrderId(), Types.INTEGER);
            stmt.setInt(5, review.getRating());
            stmt.setString(6, review.getComment());
            stmt.setTimestamp(7, Timestamp.valueOf(review.getCreatedAt()));
            stmt.setTimestamp(8, Timestamp.valueOf(review.getUpdatedAt()));

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

    public List<Review> getReviewsByEntity(ReviewedEntityType entityType, int entityId) {
        String query = "SELECT r.*, " +
                      "reviewer.full_name as reviewer_name " +
                      "FROM reviews r " +
                      "JOIN users reviewer ON r.reviewer_id = reviewer.id " +
                      "WHERE r.reviewed_entity_type = ? AND r.reviewed_entity_id = ? " +
                      "ORDER BY r.created_at DESC";

        List<Review> reviews = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, entityType.name());
            stmt.setInt(2, entityId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public List<Review> getReviewsByReviewer(int reviewerId) {
        String query = "SELECT r.*, " +
                      "reviewer.full_name as reviewer_name " +
                      "FROM reviews r " +
                      "JOIN users reviewer ON r.reviewer_id = reviewer.id " +
                      "WHERE r.reviewer_id = ? " +
                      "ORDER BY r.created_at DESC";

        List<Review> reviews = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, reviewerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public Double getAverageRating(ReviewedEntityType entityType, int entityId) {
        String query = "SELECT AVG(rating) as avg_rating FROM reviews " +
                      "WHERE reviewed_entity_type = ? AND reviewed_entity_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, entityType.name());
            stmt.setInt(2, entityId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public boolean updateReview(Review review) {
        String query = "UPDATE reviews SET rating = ?, comment = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, review.getRating());
            stmt.setString(2, review.getComment());
            stmt.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setInt(4, review.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteReview(int reviewId) {
        String query = "DELETE FROM reviews WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, reviewId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Review> getAllReviews() {
        String query = "SELECT r.*, " +
                      "reviewer.full_name as reviewer_name " +
                      "FROM reviews r " +
                      "JOIN users reviewer ON r.reviewer_id = reviewer.id " +
                      "ORDER BY r.created_at DESC";

        List<Review> reviews = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setId(rs.getInt("id"));
        review.setReviewerId(rs.getInt("reviewer_id"));
        review.setReviewedEntityType(ReviewedEntityType.valueOf(rs.getString("reviewed_entity_type")));
        review.setReviewedEntityId(rs.getInt("reviewed_entity_id"));

        int orderId = rs.getInt("order_id");
        if (!rs.wasNull()) {
            review.setOrderId(orderId);
        }

        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        review.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        review.setReviewerName(rs.getString("reviewer_name"));

        return review;
    }
}
