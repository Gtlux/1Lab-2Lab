package com.foodbooking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Message extends UserActivity {
    private Integer orderId;
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private LocalDateTime sentAt;
    private boolean isRead;

    private String senderName;
    private String receiverName;

    public Message(Integer orderId, Integer senderId, Integer receiverId, String content) {
        super(senderId, "MESSAGE");
        this.orderId = orderId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.sentAt = LocalDateTime.now();
        this.isRead = false;
    }

    @Override
    public String getActivityDescription() {
        return "Žinutė užsakymui #" + orderId + ": " +
               (content.length() > 50 ? content.substring(0, 50) + "..." : content);
    }

    @Override
    public int getPointsAwarded() {
        return isRead ? 1 : 0;
    }

    public String getShortContent() {
        return content != null && content.length() > 100 ?
               content.substring(0, 100) + "..." : content;
    }
}
