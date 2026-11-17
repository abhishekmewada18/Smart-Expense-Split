package com.expensesplit.dsa;

import com.expensesplit.dto.SettlementSuggestionDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * =========================================================================================
 * DSA IMPLEMENTATION: GREEDY SETTLEMENT ALGORITHM (MIN-CASH-FLOW PROBLEM)
 * =========================================================================================
 *
 * Problem:
 * Given a group of N members where some members owe money (debtors) and some are owed money (creditors),
 * minimize the total number of transactions required to settle all debts.
 *
 * Algorithm Strategy (Greedy with Dual Max-Heaps):
 * 1. Filter net balances into two categories:
 *    - Creditors: netBalance > 0
 *    - Debtors: netBalance < 0 (stored as positive debt amount: |netBalance|)
 *
 * 2. Maintain two PriorityQueues (Max Heaps):
 *    - maxCreditors: Max Heap ordered descending by credit amount.
 *    - maxDebtors: Max Heap ordered descending by absolute debt amount.
 *
 * 3. At each step:
 *    - Extract maximum creditor (C) and maximum debtor (D).
 *    - Settle amount = min(C.amount, D.amount).
 *    - Record transaction: Debtor (D) pays Creditor (C) 'amount'.
 *    - Deduct settled amount from both C and D.
 *    - If C still has remaining credit (> 0.009), re-insert C into maxCreditors heap.
 *    - If D still has remaining debt (> 0.009), re-insert D into maxDebtors heap.
 *
 * Complexity Analysis:
 * -----------------------------------------------------------------------------------------
 * Time Complexity:
 * - Building heaps: For V members, inserting into PriorityQueue takes O(V * log V).
 * - Settle loop: In every iteration, at least one member's balance becomes 0 (either C or D is fully settled).
 *   Therefore, the loop executes at most (V - 1) times.
 * - Each heap poll and offer takes O(log V).
 * - Total Time Complexity: O(V * log V), where V is the number of members with non-zero balances.
 *
 * Space Complexity:
 * - maxCreditors heap: at most O(V) nodes.
 * - maxDebtors heap: at most O(V) nodes.
 * - ArrayList of suggestions: at most (V - 1) transactions.
 * - Total Space Complexity: O(V).
 * =========================================================================================
 */
@Component
public class GreedySettlementAlgorithm {

    private static final BigDecimal EPSILON = new BigDecimal("0.01");

    /**
     * Minimizes transactions and computes settlement suggestions.
     *
     * @param netBalances Map of userId -> net balance (positive = creditor, negative = debtor)
     * @param userNames   Map of userId -> display name
     * @return ArrayList of SettlementSuggestionDto
     */
    public List<SettlementSuggestionDto> computeSettlementSuggestions(
            Map<Long, BigDecimal> netBalances,
            Map<Long, String> userNames) {

        List<SettlementSuggestionDto> suggestions = new ArrayList<>();

        if (netBalances == null || netBalances.isEmpty()) {
            return suggestions;
        }

        // Max-Heap for Creditors: sorted descending by credit amount
        PriorityQueue<BalanceNode> maxCreditors = new PriorityQueue<>(
                (a, b) -> b.getAmount().compareTo(a.getAmount())
        );

        // Max-Heap for Debtors: sorted descending by absolute debt amount
        PriorityQueue<BalanceNode> maxDebtors = new PriorityQueue<>(
                (a, b) -> b.getAmount().compareTo(a.getAmount())
        );

        // Partition users into Creditors and Debtors
        for (Map.Entry<Long, BigDecimal> entry : netBalances.entrySet()) {
            Long userId = entry.getKey();
            BigDecimal balance = entry.getValue().setScale(2, RoundingMode.HALF_UP);
            String name = userNames.getOrDefault(userId, "User #" + userId);

            if (balance.compareTo(EPSILON) >= 0) {
                maxCreditors.offer(new BalanceNode(userId, name, balance));
            } else if (balance.compareTo(EPSILON.negate()) <= 0) {
                // Store absolute debt amount
                maxDebtors.offer(new BalanceNode(userId, name, balance.abs()));
            }
        }

        // Greedy matching
        while (!maxCreditors.isEmpty() && !maxDebtors.isEmpty()) {
            BalanceNode maxCreditor = maxCreditors.poll();
            BalanceNode maxDebtor = maxDebtors.poll();

            BigDecimal credit = maxCreditor.getAmount();
            BigDecimal debt = maxDebtor.getAmount();

            // Transaction amount is min(credit, debt)
            BigDecimal settleAmount = credit.min(debt).setScale(2, RoundingMode.HALF_UP);

            if (settleAmount.compareTo(BigDecimal.ZERO) > 0) {
                suggestions.add(new SettlementSuggestionDto(
                        maxDebtor.getUserId(),
                        maxDebtor.getUserName(),
                        maxCreditor.getUserId(),
                        maxCreditor.getUserName(),
                        settleAmount
                ));
            }

            BigDecimal remainingCredit = credit.subtract(settleAmount).setScale(2, RoundingMode.HALF_UP);
            BigDecimal remainingDebt = debt.subtract(settleAmount).setScale(2, RoundingMode.HALF_UP);

            if (remainingCredit.compareTo(EPSILON) >= 0) {
                maxCreditor.setAmount(remainingCredit);
                maxCreditors.offer(maxCreditor);
            }

            if (remainingDebt.compareTo(EPSILON) >= 0) {
                maxDebtor.setAmount(remainingDebt);
                maxDebtors.offer(maxDebtor);
            }
        }

        return suggestions;
    }
}

