package com.expensesplit.service.impl;

import com.expensesplit.dto.*;
import com.expensesplit.entity.*;
import com.expensesplit.exception.InvalidSplitException;
import com.expensesplit.exception.ResourceNotFoundException;
import com.expensesplit.repository.ExpenseRepository;
import com.expensesplit.repository.ExpenseSplitRepository;
import com.expensesplit.service.ExpenseService;
import com.expensesplit.service.GroupService;
import com.expensesplit.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final GroupService groupService;
    private final UserService userService;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository,
                              ExpenseSplitRepository expenseSplitRepository,
                              GroupService groupService,
                              UserService userService) {
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.groupService = groupService;
        this.userService = userService;
    }

    @Override
    public ExpenseResponse addExpense(Long groupId, ExpenseCreateRequest request) {
        ExpenseGroup group = groupService.getGroupEntityById(groupId);
        User paidBy = userService.getUserEntityById(request.getPaidById());

        Expense expense = new Expense();
        expense.setGroup(group);
        expense.setDescription(request.getDescription().trim());
        expense.setTotalAmount(request.getTotalAmount().setScale(2, RoundingMode.HALF_UP));
        expense.setPaidBy(paidBy);
        expense.setSplitType(request.getSplitType());
        expense.setExpenseDate(request.getExpenseDate() != null ? request.getExpenseDate() : LocalDate.now());

        // Validate and generate splits
        List<ExpenseSplit> splits = processSplits(expense, request.getSplits(), request.getSplitType(), request.getTotalAmount());
        for (ExpenseSplit split : splits) {
            expense.addSplit(split);
        }

        Expense savedExpense = expenseRepository.save(expense);
        return mapToResponse(savedExpense);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getGroupExpenses(Long groupId) {
        groupService.getGroupEntityById(groupId);
        List<Expense> expenses = expenseRepository.findByGroupIdWithSplits(groupId);
        List<ExpenseResponse> responses = new ArrayList<>();
        for (Expense expense : expenses) {
            responses.add(mapToResponse(expense));
        }
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long expenseId) {
        Expense expense = getExpenseEntityById(expenseId);
        return mapToResponse(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public Expense getExpenseEntityById(Long expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
    }

    @Override
    public ExpenseResponse updateExpense(Long expenseId, ExpenseUpdateRequest request) {
        Expense expense = getExpenseEntityById(expenseId);
        User paidBy = userService.getUserEntityById(request.getPaidById());

        expense.setDescription(request.getDescription().trim());
        expense.setTotalAmount(request.getTotalAmount().setScale(2, RoundingMode.HALF_UP));
        expense.setPaidBy(paidBy);
        expense.setSplitType(request.getSplitType());
        if (request.getExpenseDate() != null) {
            expense.setExpenseDate(request.getExpenseDate());
        }

        // Clear existing splits
        expense.getSplits().clear();

        // Process new splits
        List<ExpenseSplit> newSplits = processSplits(expense, request.getSplits(), request.getSplitType(), request.getTotalAmount());
        for (ExpenseSplit split : newSplits) {
            expense.addSplit(split);
        }

        Expense updated = expenseRepository.save(expense);
        return mapToResponse(updated);
    }

    @Override
    public void deleteExpense(Long expenseId) {
        Expense expense = getExpenseEntityById(expenseId);
        expenseRepository.delete(expense);
    }

    /**
     * Calculates and validates individual splits based on split type: EQUAL, EXACT, or PERCENTAGE.
     */
    private List<ExpenseSplit> processSplits(Expense expense, List<SplitDetailRequest> splitRequests,
                                             SplitType splitType, BigDecimal totalAmount) {

        if (splitRequests == null || splitRequests.isEmpty()) {
            throw new InvalidSplitException("At least one participant must be specified in the expense split");
        }

        List<ExpenseSplit> calculatedSplits = new ArrayList<>();
        int count = splitRequests.size();

        switch (splitType) {
            case EQUAL -> {
                BigDecimal equalShare = totalAmount.divide(BigDecimal.valueOf(count), 2, RoundingMode.DOWN);
                BigDecimal sumShares = equalShare.multiply(BigDecimal.valueOf(count));
                BigDecimal remainder = totalAmount.subtract(sumShares).setScale(2, RoundingMode.HALF_UP);

                for (int i = 0; i < count; i++) {
                    SplitDetailRequest req = splitRequests.get(i);
                    User participant = userService.getUserEntityById(req.getUserId());

                    BigDecimal share = equalShare;
                    // Distribute any rounding penny to the first participant
                    if (i == 0 && remainder.compareTo(BigDecimal.ZERO) > 0) {
                        share = share.add(remainder);
                    }

                    calculatedSplits.add(new ExpenseSplit(expense, participant, share, null, null));
                }
            }

            case EXACT -> {
                BigDecimal sumExact = BigDecimal.ZERO;
                for (SplitDetailRequest req : splitRequests) {
                    if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new InvalidSplitException("Exact amount must be specified and greater than zero for user id " + req.getUserId());
                    }
                    sumExact = sumExact.add(req.getAmount());
                }

                sumExact = sumExact.setScale(2, RoundingMode.HALF_UP);
                BigDecimal expectedTotal = totalAmount.setScale(2, RoundingMode.HALF_UP);

                if (sumExact.compareTo(expectedTotal) != 0) {
                    throw new InvalidSplitException(String.format(
                            "Exact split amounts sum (₹%s) does not match total expense amount (₹%s)",
                            sumExact, expectedTotal
                    ));
                }

                for (SplitDetailRequest req : splitRequests) {
                    User participant = userService.getUserEntityById(req.getUserId());
                    BigDecimal share = req.getAmount().setScale(2, RoundingMode.HALF_UP);
                    calculatedSplits.add(new ExpenseSplit(expense, participant, share, null, share));
                }
            }

            case PERCENTAGE -> {
                BigDecimal sumPercentage = BigDecimal.ZERO;
                for (SplitDetailRequest req : splitRequests) {
                    if (req.getPercentage() == null || req.getPercentage().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new InvalidSplitException("Percentage must be specified and greater than zero for user id " + req.getUserId());
                    }
                    sumPercentage = sumPercentage.add(req.getPercentage());
                }

                sumPercentage = sumPercentage.setScale(2, RoundingMode.HALF_UP);
                if (sumPercentage.compareTo(new BigDecimal("100.00")) != 0) {
                    throw new InvalidSplitException("Split percentages must sum up to exactly 100%. Current sum: " + sumPercentage + "%");
                }

                BigDecimal allocatedAmount = BigDecimal.ZERO;
                for (int i = 0; i < count; i++) {
                    SplitDetailRequest req = splitRequests.get(i);
                    User participant = userService.getUserEntityById(req.getUserId());
                    BigDecimal percent = req.getPercentage().setScale(2, RoundingMode.HALF_UP);

                    BigDecimal share;
                    if (i == count - 1) {
                        // Avoid rounding loss on the last participant
                        share = totalAmount.subtract(allocatedAmount).setScale(2, RoundingMode.HALF_UP);
                    } else {
                        share = totalAmount.multiply(percent).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                        allocatedAmount = allocatedAmount.add(share);
                    }

                    calculatedSplits.add(new ExpenseSplit(expense, participant, share, percent, null));
                }
            }
        }

        return calculatedSplits;
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        List<ExpenseSplitResponse> splitResponses = new ArrayList<>();
        if (expense.getSplits() != null) {
            for (ExpenseSplit split : expense.getSplits()) {
                splitResponses.add(new ExpenseSplitResponse(
                        split.getId(),
                        split.getUser().getId(),
                        split.getUser().getName(),
                        split.getUser().getEmail(),
                        split.getAmount(),
                        split.getPercentage(),
                        split.getExactAmount()
                ));
            }
        }

        return new ExpenseResponse(
                expense.getId(),
                expense.getGroup().getId(),
                expense.getGroup().getName(),
                expense.getDescription(),
                expense.getTotalAmount(),
                expense.getPaidBy().getId(),
                expense.getPaidBy().getName(),
                expense.getSplitType(),
                expense.getExpenseDate(),
                splitResponses,
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }
}

