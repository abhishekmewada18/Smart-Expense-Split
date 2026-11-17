package com.expensesplit.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SplitDetailRequest {

    @NotNull(message = "Participant user ID is required")
    private Long userId;

    /**
     * Required for EXACT split type. Optional for EQUAL / PERCENTAGE.
     */
    private BigDecimal amount;

    /**
     * Required for PERCENTAGE split type. Optional for EQUAL / EXACT.
     */
    private BigDecimal percentage;

    public SplitDetailRequest() {
    }

    public SplitDetailRequest(Long userId) {
        this.userId = userId;
    }

    public SplitDetailRequest(Long userId, BigDecimal amount, BigDecimal percentage) {
        this.userId = userId;
        this.amount = amount;
        this.percentage = percentage;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}
