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
    private Integer orderId;
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private LocalDateTime sentAt;
    private boolean isRead;

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
