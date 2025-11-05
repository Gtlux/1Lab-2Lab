package com.foodbooking.dao;

import com.foodbooking.model.Message;
import com.foodbooking.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public boolean createMessage(Message message) {
        String sql = "INSERT INTO messages (order_id, sender_id, receiver_id, content, is_read) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, message.getOrderId());
            pstmt.setInt(2, message.getSenderId());
            pstmt.setInt(3, message.getReceiverId());
            pstmt.setString(4, message.getContent());
            pstmt.setBoolean(5, message.isRead());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        message.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating message: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Message getMessageById(int id) {
        String sql = "SELECT m.*, " +
                     "u1.full_name as sender_name, " +
                     "u2.full_name as receiver_name " +
                     "FROM messages m " +
                     "LEFT JOIN users u1 ON m.sender_id = u1.id " +
                     "LEFT JOIN users u2 ON m.receiver_id = u2.id " +
                     "WHERE m.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractMessageFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting message by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Message> getMessagesByOrderId(int orderId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, " +
                     "u1.full_name as sender_name, " +
                     "u2.full_name as receiver_name " +
                     "FROM messages m " +
                     "LEFT JOIN users u1 ON m.sender_id = u1.id " +
                     "LEFT JOIN users u2 ON m.receiver_id = u2.id " +
                     "WHERE m.order_id = ? " +
                     "ORDER BY m.sent_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                messages.add(extractMessageFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting messages by order: " + e.getMessage());
            e.printStackTrace();
        }
        return messages;
    }

    public List<Message> getMessagesByUserId(int userId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, " +
                     "u1.full_name as sender_name, " +
                     "u2.full_name as receiver_name " +
                     "FROM messages m " +
                     "LEFT JOIN users u1 ON m.sender_id = u1.id " +
                     "LEFT JOIN users u2 ON m.receiver_id = u2.id " +
                     "WHERE m.sender_id = ? OR m.receiver_id = ? " +
                     "ORDER BY m.sent_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                messages.add(extractMessageFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting messages by user: " + e.getMessage());
            e.printStackTrace();
        }
        return messages;
    }

    public boolean updateMessage(Message message) {
        String sql = "UPDATE messages SET order_id = ?, sender_id = ?, receiver_id = ?, " +
                     "content = ?, is_read = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, message.getOrderId());
            pstmt.setInt(2, message.getSenderId());
            pstmt.setInt(3, message.getReceiverId());
            pstmt.setString(4, message.getContent());
            pstmt.setBoolean(5, message.isRead());
            pstmt.setInt(6, message.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating message: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean markAsRead(int messageId) {
        String sql = "UPDATE messages SET is_read = true WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, messageId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error marking message as read: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteMessage(int id) {
        String sql = "DELETE FROM messages WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting message: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private Message extractMessageFromResultSet(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("id"));
        message.setOrderId(rs.getInt("order_id"));
        message.setSenderId(rs.getInt("sender_id"));
        message.setReceiverId(rs.getInt("receiver_id"));
        message.setContent(rs.getString("content"));
        message.setSentAt(rs.getTimestamp("sent_at").toLocalDateTime());
        message.setRead(rs.getBoolean("is_read"));

        message.setSenderName(rs.getString("sender_name"));
        message.setReceiverName(rs.getString("receiver_name"));

        return message;
    }
}
