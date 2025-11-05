package com.foodbooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancellationRequest {
    private Integer id;
    private Integer orderId;
    private Integer clientId;
    private String reason;
    private CancellationStatus status;
    private Integer reviewedBy;
    private String reviewNote;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    private String clientName;
    private String reviewerName;
    private Integer restaurantId;
    private String restaurantName;

    public CancellationRequest(Integer orderId, Integer clientId, String reason) {
        this.orderId = orderId;
        this.clientId = clientId;
        this.reason = reason;
        this.status = CancellationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public enum CancellationStatus {
        PENDING("Laukiama"),
        APPROVED("Patvirtinta"),
        REJECTED("Atmesta");

        private final String displayName;

        CancellationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
