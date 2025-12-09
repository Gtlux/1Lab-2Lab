package com.foodbooking.dto;

import com.foodbooking.model.CancellationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancellationRequestDTO {
    private Integer id;
    private Integer orderId;
    private Integer clientId;
    private String clientName;
    private String reason;
    private CancellationRequest.CancellationStatus status;
    private Integer reviewedBy;
    private String reviewerName;
    private String reviewNote;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    public static CancellationRequestDTO fromCancellationRequest(CancellationRequest request) {
        return new CancellationRequestDTO(
                request.getId(),
                request.getOrderId(),
                request.getClientId(),
                request.getClientName(),
                request.getReason(),
                request.getStatus(),
                request.getReviewedBy(),
                request.getReviewerName(),
                request.getReviewNote(),
                request.getCreatedAt(),
                request.getReviewedAt()
        );
    }
}
