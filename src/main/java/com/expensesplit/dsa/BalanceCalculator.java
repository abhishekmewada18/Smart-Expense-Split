package com.expensesplit.dsa;

import com.expensesplit.dto.MemberBalanceDto;
import com.expensesplit.entity.Expense;
import com.expensesplit.entity.ExpenseSplit;
import com.expensesplit.entity.Settlement;
import com.expensesplit.entity.SettlementStatus;
import com.expensesplit.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * =========================================================================================
 * DSA IMPLEMENTATION: HASHMAP-BASED BALANCE CALCULATOR
 * =========================================================================================
 *
 * Core Concept:
 * - Net Balance of User U = Total Amount Paid by U - Total Amount Owed by U + Settlements Adjusted.
 *   - If Net Balance > 0: User is a Creditor (should receive money).
 *   - If Net Balance < 0: User is a Debtor (owes money).
 *   - If Net Balance == 0: User is fully settled.
 *
 * Data Structures Used:
 * 1. HashMap<Long, BigDecimal> netBalances:
 *    Maps userId -> net balance for O(1) amortized updates and lookups.
 * 2. HashMap<Long, BigDecimal> totalPaidMap:
 *    Tracks cumulative amount paid by each user.
 * 3. HashMap<Long, BigDecimal> totalOwedMap:
 *    Tracks cumulative amount owed (shares) by each user.
 * 4. ArrayList<MemberBalanceDto>:
 *    Maintains the final list of calculated balances.
 *
 * Complexity Analysis:
 * -----------------------------------------------------------------------------------------
 * Time Complexity:
 * - Processing E expenses with an average of S splits per expense:
 *   For each expense, we update paidBy (1 HashMap update) and each split (S HashMap updates).
 *   Total expense computation: O(E * S).
 * - Processing K settlements:
 *   Each settlement does 2 HashMap updates: O(K).
 * - Aggregating final balances for V members: O(V).
 * - Overall Time Complexity: O(E * S + K + V), which is linear in the number of records.
 *
 * Space Complexity:
 * - HashMaps store entries for V members: O(V).
 * - ArrayList holds V member balance objects: O(V).
 * - Overall Space Complexity: O(V).
 * =========================================================================================
 */
@Component
public class BalanceCalculator {

    private static final BigDecimal EPSILON = new BigDecimal("0.01");

    public Map<Long, BigDecimal> calculateNetBalances(
            List<User> members,
            List<Expense> expenses,
            List<Settlement> settlements) {

        Map<Long, BigDecimal> netBalances = new HashMap<>();

        // Initialize all group members with zero balance
        if (members != null) {
            for (User member : members) {
                netBalances.put(member.getId(), BigDecimal.ZERO);
            }
        }

        // 1. Process Expenses
        if (expenses != null) {
            for (Expense expense : expenses) {
                Long payerId = expense.getPaidBy().getId();
                BigDecimal totalAmount = expense.getTotalAmount();

                // Payer receives credit for paying the total amount
                netBalances.put(payerId, netBalances.getOrDefault(payerId, BigDecimal.ZERO).add(totalAmount));

                // Each participant owes their individual split share
                for (ExpenseSplit split : expense.getSplits()) {
                    Long participantId = split.getUser().getId();
                    BigDecimal splitAmount = split.getAmount();

                    netBalances.put(participantId, netBalances.getOrDefault(participantId, BigDecimal.ZERO).subtract(splitAmount));
                }
            }
        }

        // 2. Adjust for Completed Settlements
        if (settlements != null) {
            for (Settlement settlement : settlements) {
                if (settlement.getStatus() == SettlementStatus.COMPLETED) {
                    Long debtorId = settlement.getPaidBy().getId();
                    Long creditorId = settlement.getPaidTo().getId();
                    BigDecimal amount = settlement.getAmount();

                    // Debtor paid money, so their debt decreases (net balance increases)
                    netBalances.put(debtorId, netBalances.getOrDefault(debtorId, BigDecimal.ZERO).add(amount));

                    // Creditor received money, so their credit decreases (net balance decreases)
                    netBalances.put(creditorId, netBalances.getOrDefault(creditorId, BigDecimal.ZERO).subtract(amount));
                }
            }
        }

        // Round all balances to 2 decimal places
        for (Map.Entry<Long, BigDecimal> entry : netBalances.entrySet()) {
            entry.setValue(entry.getValue().setScale(2, RoundingMode.HALF_UP));
        }

        return netBalances;
    }

    public List<MemberBalanceDto> calculateMemberBalances(
            List<User> members,
            List<Expense> expenses,
            List<Settlement> settlements) {

        Map<Long, BigDecimal> totalPaidMap = new HashMap<>();
        Map<Long, BigDecimal> totalOwedMap = new HashMap<>();
        Map<Long, User> userMap = new LinkedHashMap<>();

        if (members != null) {
            for (User member : members) {
                userMap.put(member.getId(), member);
                totalPaidMap.put(member.getId(), BigDecimal.ZERO);
                totalOwedMap.put(member.getId(), BigDecimal.ZERO);
            }
        }

        // Aggregate total paid and total owed from expenses
        if (expenses != null) {
            for (Expense expense : expenses) {
                Long payerId = expense.getPaidBy().getId();
                userMap.putIfAbsent(payerId, expense.getPaidBy());
                totalPaidMap.put(payerId, totalPaidMap.getOrDefault(payerId, BigDecimal.ZERO).add(expense.getTotalAmount()));

                for (ExpenseSplit split : expense.getSplits()) {
                    Long participantId = split.getUser().getId();
                    userMap.putIfAbsent(participantId, split.getUser());
                    totalOwedMap.put(participantId, totalOwedMap.getOrDefault(participantId, BigDecimal.ZERO).add(split.getAmount()));
                }
            }
        }

        // Adjust for completed settlements
        if (settlements != null) {
            for (Settlement settlement : settlements) {
                if (settlement.getStatus() == SettlementStatus.COMPLETED) {
                    Long payerId = settlement.getPaidBy().getId();
                    Long receiverId = settlement.getPaidTo().getId();
                    userMap.putIfAbsent(payerId, settlement.getPaidBy());
                    userMap.putIfAbsent(receiverId, settlement.getPaidTo());

                    // Debtor's payment increases their effective paid amount
                    totalPaidMap.put(payerId, totalPaidMap.getOrDefault(payerId, BigDecimal.ZERO).add(settlement.getAmount()));
                    // Receiver's owed amount increases effectively as credit received
                    totalOwedMap.put(receiverId, totalOwedMap.getOrDefault(receiverId, BigDecimal.ZERO).add(settlement.getAmount()));
                }
            }
        }

        List<MemberBalanceDto> resultList = new ArrayList<>();

        for (Map.Entry<Long, User> entry : userMap.entrySet()) {
            Long userId = entry.getKey();
            User user = entry.getValue();

            BigDecimal paid = totalPaidMap.getOrDefault(userId, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            BigDecimal owed = totalOwedMap.getOrDefault(userId, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            BigDecimal net = paid.subtract(owed).setScale(2, RoundingMode.HALF_UP);

            String status;
            if (net.compareTo(EPSILON) >= 0) {
                status = "SHOULD_RECEIVE";
            } else if (net.compareTo(EPSILON.negate()) <= 0) {
                status = "OWES";
            } else {
                status = "SETTLED";
                net = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            }

            resultList.add(new MemberBalanceDto(
                    userId,
                    user.getName(),
                    user.getEmail(),
                    paid,
                    owed,
                    net,
                    status
            ));
        }

        return resultList;
    }
}

