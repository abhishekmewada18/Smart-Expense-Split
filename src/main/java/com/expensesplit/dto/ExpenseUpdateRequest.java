package com.expensesplit.dto;

import com.expensesplit.entity.SplitType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ExpenseUpdateRequest {

    @NotBlank(message = "Expense description is required")
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than zero")
    private BigDecimal totalAmount;

    @NotNull(message = "PaidBy user ID is required")
    private Long paidById;

    @NotNull(message = "Split type is required")
    private SplitType splitType;

    private LocalDate expenseDate;

    @NotEmpty(message = "Splits list cannot be empty")
    @Valid
    private List<SplitDetailRequest> splits;

    public ExpenseUpdateRequest() {
    }

    public ExpenseUpdateRequest(String description, BigDecimal totalAmount, Long paidById,
                                SplitType splitType, LocalDate expenseDate, List<SplitDetailRequest> splits) {
        this.description = description;
        this.totalAmount = totalAmount;
        this.paidById = paidById;
        this.splitType = splitType;
        this.expenseDate = expenseDate;
        this.splits = splits;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getPaidById() {
        return paidById;
    }

    public void setPaidById(Long paidById) {
        this.paidById = paidById;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public void setSplitType(SplitType splitType) {
        this.splitType = splitType;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public List<SplitDetailRequest> getSplits() {
        return splits;
    }

    public void setSplits(List<SplitDetailRequest> splits) {
        this.splits = splits;
    }
}
