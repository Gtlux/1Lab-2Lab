package com.foodbooking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class LoyaltyTransaction extends UserActivity {
    private Integer clientId;
    private Integer orderId;
    private Integer pointsEarned;
    private Integer pointsRedeemed;
    private BigDecimal orderAmount;
    private String transactionType;
    private String description;

    public LoyaltyTransaction(Integer clientId, Integer orderId, Integer points,
                             BigDecimal orderAmount, String transactionType) {
        super(clientId, "LOYALTY");
        this.clientId = clientId;
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.transactionType = transactionType;

        if ("EARNED".equals(transactionType)) {
            this.pointsEarned = points;
            this.pointsRedeemed = 0;
        } else if ("REDEEMED".equals(transactionType)) {
            this.pointsEarned = 0;
            this.pointsRedeemed = points;
        }

        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String getActivityDescription() {
        if ("EARNED".equals(transactionType)) {
            return "Uždirbta " + pointsEarned + " lojalumo taškų už užsakymą #" + orderId;
        } else if ("REDEEMED".equals(transactionType)) {
            return "Panaudota " + pointsRedeemed + " lojalumo taškų užsakyme #" + orderId;
        }
        return "Lojalumo taškų transakc ija";
    }

    @Override
    public int getPointsAwarded() {
        return pointsEarned != null ? pointsEarned : 0;
    }

    public boolean isEarning() {
        return "EARNED".equals(transactionType);
    }

    public boolean isRedemption() {
        return "REDEEMED".equals(transactionType);
    }

    public int calculatePointsFromAmount() {
        if (orderAmount == null) return 0;
        return orderAmount.divide(new BigDecimal("10")).intValue();
    }

    public static LoyaltyTransaction createEarningTransaction(Integer clientId, Integer orderId,
                                                              BigDecimal orderAmount) {
        int points = orderAmount.divide(new BigDecimal("10")).intValue();
        return new LoyaltyTransaction(clientId, orderId, points, orderAmount, "EARNED");
    }

    public static LoyaltyTransaction createRedemptionTransaction(Integer clientId, Integer orderId,
                                                                 Integer pointsToRedeem, BigDecimal orderAmount) {
        return new LoyaltyTransaction(clientId, orderId, pointsToRedeem, orderAmount, "REDEEMED");
    }
}
