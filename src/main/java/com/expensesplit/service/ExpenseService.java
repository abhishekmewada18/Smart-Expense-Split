package com.expensesplit.service;

import com.expensesplit.dto.ExpenseCreateRequest;
import com.expensesplit.dto.ExpenseResponse;
import com.expensesplit.dto.ExpenseUpdateRequest;
import com.expensesplit.entity.Expense;

import java.util.List;

public interface ExpenseService {
    ExpenseResponse addExpense(Long groupId, ExpenseCreateRequest request);
    List<ExpenseResponse> getGroupExpenses(Long groupId);
    ExpenseResponse getExpenseById(Long expenseId);
    Expense getExpenseEntityById(Long expenseId);
    ExpenseResponse updateExpense(Long expenseId, ExpenseUpdateRequest request);
    void deleteExpense(Long expenseId);
}

