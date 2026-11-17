package com.expensesplit.controller;

import com.expensesplit.dto.ApiResponse;
import com.expensesplit.dto.ExpenseCreateRequest;
import com.expensesplit.dto.ExpenseResponse;
import com.expensesplit.dto.ExpenseUpdateRequest;
import com.expensesplit.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/api/groups/{groupId}/expenses")
    public ResponseEntity<ApiResponse<ExpenseResponse>> addExpense(@PathVariable Long groupId,
                                                                   @Valid @RequestBody ExpenseCreateRequest request) {
        ExpenseResponse expense = expenseService.addExpense(groupId, request);
        return new ResponseEntity<>(ApiResponse.success("Expense added successfully", expense), HttpStatus.CREATED);
    }

    @GetMapping("/api/groups/{groupId}/expenses")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getGroupExpenses(@PathVariable Long groupId) {
        List<ExpenseResponse> expenses = expenseService.getGroupExpenses(groupId);
        return ResponseEntity.ok(ApiResponse.success("Group expenses retrieved successfully", expenses));
    }

    @GetMapping("/api/expenses/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(@PathVariable Long expenseId) {
        ExpenseResponse expense = expenseService.getExpenseById(expenseId);
        return ResponseEntity.ok(ApiResponse.success("Expense retrieved successfully", expense));
    }

    @PutMapping("/api/expenses/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(@PathVariable Long expenseId,
                                                                      @Valid @RequestBody ExpenseUpdateRequest request) {
        ExpenseResponse updated = expenseService.updateExpense(expenseId, request);
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", updated));
    }

    @DeleteMapping("/api/expenses/{expenseId}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully", null));
    }
}

