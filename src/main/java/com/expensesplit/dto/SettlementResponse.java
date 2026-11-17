package com.expensesplit.dto;

import com.expensesplit.entity.SettlementStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SettlementResponse {
    private Long id;
    private Long groupId;
    private String groupName;
    private Long paidById;
    private String paidByName;
    private Long paidToId;
    private String paidToName;
    private BigDecimal amount;
    private LocalDate settlementDate;
    private String notes;
    private SettlementStatus status;
    private LocalDateTime createdAt;

    public SettlementResponse() {
    }

    public SettlementResponse(Long id, Long groupId, String groupName, Long paidById, String paidByName,
                              Long paidToId, String paidToName, BigDecimal amount, LocalDate settlementDate,
                              String notes, SettlementStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.groupId = groupId;
        this.groupName = groupName;
        this.paidById = paidById;
        this.paidByName = paidByName;
        this.paidToId = paidToId;
        this.paidToName = paidToName;
        this.amount = amount;
        this.settlementDate = settlementDate;
        this.notes = notes;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getPaidById() {
        return paidById;
    }

    public void setPaidById(Long paidById) {
        this.paidById = paidById;
    }

    public String getPaidByName() {
        return paidByName;
    }

    public void setPaidByName(String paidByName) {
        this.paidByName = paidByName;
    }

    public Long getPaidToId() {
        return paidToId;
    }

    public void setPaidToId(Long paidToId) {
        this.paidToId = paidToId;
    }

    public String getPaidToName() {
        return paidToName;
    }

    public void setPaidToName(String paidToName) {
        this.paidToName = paidToName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(LocalDate settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public SettlementStatus getStatus() {
        return status;
    }

    public void setStatus(SettlementStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

