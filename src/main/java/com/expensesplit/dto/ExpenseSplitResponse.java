package com.expensesplit.dto;

import java.math.BigDecimal;

public class ExpenseSplitResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private BigDecimal amount;
    private BigDecimal percentage;
    private BigDecimal exactAmount;

    public ExpenseSplitResponse() {
    }

    public ExpenseSplitResponse(Long id, Long userId, String userName, String userEmail,
                                BigDecimal amount, BigDecimal percentage, BigDecimal exactAmount) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.amount = amount;
        this.percentage = percentage;
        this.exactAmount = exactAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public BigDecimal getExactAmount() {
        return exactAmount;
    }

    public void setExactAmount(BigDecimal exactAmount) {
        this.exactAmount = exactAmount;
    }
}
