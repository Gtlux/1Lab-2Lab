package com.foodbooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Integer id;
    private Integer reviewerId;
    private ReviewedEntityType reviewedEntityType;
    private Integer reviewedEntityId;
    private Integer orderId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String reviewerName;
    private String reviewedEntityName;

    public Review(Integer reviewerId, ReviewedEntityType reviewedEntityType,
                  Integer reviewedEntityId, Integer rating, String comment) {
        this.reviewerId = reviewerId;
        this.reviewedEntityType = reviewedEntityType;
        this.reviewedEntityId = reviewedEntityId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public enum ReviewedEntityType {
        RESTAURANT("Restoranas"),
        DRIVER("Vairuotojas"),
        CLIENT("Klientas");

        private final String displayName;

        ReviewedEntityType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
