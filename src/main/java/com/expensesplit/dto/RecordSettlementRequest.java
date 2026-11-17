package com.expensesplit.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RecordSettlementRequest {

    @NotNull(message = "Payer (debtor) user ID is required")
    private Long paidById;

    @NotNull(message = "Recipient (creditor) user ID is required")
    private Long paidToId;

    @NotNull(message = "Settlement amount is required")
    @DecimalMin(value = "0.01", message = "Settlement amount must be greater than zero")
    private BigDecimal amount;

    private LocalDate settlementDate;

    @Size(max = 255, message = "Notes cannot exceed 255 characters")
    private String notes;

    public RecordSettlementRequest() {
    }

    public RecordSettlementRequest(Long paidById, Long paidToId, BigDecimal amount, LocalDate settlementDate, String notes) {
        this.paidById = paidById;
        this.paidToId = paidToId;
        this.amount = amount;
        this.settlementDate = settlementDate;
        this.notes = notes;
    }

    public Long getPaidById() {
        return paidById;
    }

    public void setPaidById(Long paidById) {
        this.paidById = paidById;
    }

    public Long getPaidToId() {
        return paidToId;
    }

    public void setPaidToId(Long paidToId) {
        this.paidToId = paidToId;
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
}

