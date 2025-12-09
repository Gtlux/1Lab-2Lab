package com.foodbooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserActivity {
    protected Integer id;
    protected Integer userId;
    protected String userName;
    protected LocalDateTime createdAt;
    protected String activityType;

    public UserActivity(Integer userId, String activityType) {
        this.userId = userId;
        this.activityType = activityType;
        this.createdAt = LocalDateTime.now();
    }

    public abstract String getActivityDescription();

    public abstract int getPointsAwarded();

    public boolean isRecent() {
        return createdAt.isAfter(LocalDateTime.now().minusDays(30));
    }

    public String getFormattedDate() {
        return createdAt != null ? createdAt.toString() : "";
    }
}
