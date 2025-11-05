package com.foodbooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Integer id;
    private Integer orderId; // Foreign key to Order
    private Integer senderId; // Foreign key to User
    private Integer receiverId; // Foreign key to User
    private String content;
    private LocalDateTime sentAt;
    private boolean isRead;

    // Additional fields for display
    private String senderName;
    private String receiverName;

    public Message(Integer orderId, Integer senderId, Integer receiverId, String content) {
        this.orderId = orderId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.sentAt = LocalDateTime.now();
        this.isRead = false;
    }
}
