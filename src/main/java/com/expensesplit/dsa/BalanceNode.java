package com.expensesplit.dsa;

import java.math.BigDecimal;

/**
 * Data structure representing a participant's net balance in the settlement heap.
 */
public class BalanceNode {
    private final Long userId;
    private final String userName;
    private BigDecimal amount;

    public BalanceNode(Long userId, String userName, BigDecimal amount) {
        this.userId = userId;
        this.userName = userName;
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "BalanceNode{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", amount=" + amount +
                '}';
    }
}

