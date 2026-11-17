package com.expensesplit.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SettlementGraphDto {

    private List<UserNodeDto> vertices = new ArrayList<>();
    private List<PaymentEdgeDto> edges = new ArrayList<>();

    public SettlementGraphDto() {
    }

    public SettlementGraphDto(List<UserNodeDto> vertices, List<PaymentEdgeDto> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    public static class UserNodeDto {
        private Long userId;
        private String userName;
        private BigDecimal netBalance;
        private String role; // "DEBTOR", "CREDITOR", "SETTLED"

        public UserNodeDto() {
        }

        public UserNodeDto(Long userId, String userName, BigDecimal netBalance, String role) {
            this.userId = userId;
            this.userName = userName;
            this.netBalance = netBalance;
            this.role = role;
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

        public BigDecimal getNetBalance() {
            return netBalance;
        }

        public void setNetBalance(BigDecimal netBalance) {
            this.netBalance = netBalance;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class PaymentEdgeDto {
        private Long fromUserId;
        private String fromUserName;
        private Long toUserId;
        private String toUserName;
        private BigDecimal amount;

        public PaymentEdgeDto() {
        }

        public PaymentEdgeDto(Long fromUserId, String fromUserName, Long toUserId, String toUserName, BigDecimal amount) {
            this.fromUserId = fromUserId;
            this.fromUserName = fromUserName;
            this.toUserId = toUserId;
            this.toUserName = toUserName;
            this.amount = amount;
        }

        public Long getFromUserId() {
            return fromUserId;
        }

        public void setFromUserId(Long fromUserId) {
            this.fromUserId = fromUserId;
        }

        public String getFromUserName() {
            return fromUserName;
        }

        public void setFromUserName(String fromUserName) {
            this.fromUserName = fromUserName;
        }

        public Long getToUserId() {
            return toUserId;
        }

        public void setToUserId(Long toUserId) {
            this.toUserId = toUserId;
        }

        public String getToUserName() {
            return toUserName;
        }

        public void setToUserName(String toUserName) {
            this.toUserName = toUserName;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    public List<UserNodeDto> getVertices() {
        return vertices;
    }

    public void setVertices(List<UserNodeDto> vertices) {
        this.vertices = vertices;
    }

    public List<PaymentEdgeDto> getEdges() {
        return edges;
    }

    public void setEdges(List<PaymentEdgeDto> edges) {
        this.edges = edges;
    }
}

