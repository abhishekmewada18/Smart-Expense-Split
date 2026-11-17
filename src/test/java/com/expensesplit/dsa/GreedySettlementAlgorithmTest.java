package com.expensesplit.dsa;

import com.expensesplit.dto.SettlementSuggestionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GreedySettlementAlgorithmTest {

    private GreedySettlementAlgorithm algorithm;

    @BeforeEach
    void setUp() {
        algorithm = new GreedySettlementAlgorithm();
    }

    @Test
    @DisplayName("Prompt Scenario: Rahul pays ₹900 equally shared among Rahul, Amit, and Priya")
    void testRahulAmitPriyaSettlement() {
        // Rahul: +600.00
        // Amit: -300.00
        // Priya: -300.00
        Map<Long, BigDecimal> netBalances = new HashMap<>();
        netBalances.put(1L, new BigDecimal("600.00"));
        netBalances.put(2L, new BigDecimal("-300.00"));
        netBalances.put(3L, new BigDecimal("-300.00"));

        Map<Long, String> userNames = new HashMap<>();
        userNames.put(1L, "Rahul");
        userNames.put(2L, "Amit");
        userNames.put(3L, "Priya");

        List<SettlementSuggestionDto> suggestions = algorithm.computeSettlementSuggestions(netBalances, userNames);

        // Minimum transactions should be 2
        assertEquals(2, suggestions.size());

        BigDecimal totalSettled = BigDecimal.ZERO;
        for (SettlementSuggestionDto s : suggestions) {
            assertEquals(1L, s.getToUserId(), "All payments should be received by Rahul");
            assertTrue(s.getFromUserId() == 2L || s.getFromUserId() == 3L, "Payers should be Amit or Priya");
            assertEquals(new BigDecimal("300.00"), s.getAmount());
            totalSettled = totalSettled.add(s.getAmount());
        }

        assertEquals(new BigDecimal("600.00"), totalSettled, "Total settled amount should equal Rahul's credit");
    }

    @Test
    @DisplayName("Circular Debt Cancellation: Net zero balances should require 0 transactions")
    void testCircularDebtCancellation() {
        // When net balances are all zero (e.g. A->B 100, B->C 100, C->A 100)
        Map<Long, BigDecimal> netBalances = new HashMap<>();
        netBalances.put(1L, BigDecimal.ZERO);
        netBalances.put(2L, BigDecimal.ZERO);
        netBalances.put(3L, BigDecimal.ZERO);

        Map<Long, String> userNames = new HashMap<>();
        userNames.put(1L, "A");
        userNames.put(2L, "B");
        userNames.put(3L, "C");

        List<SettlementSuggestionDto> suggestions = algorithm.computeSettlementSuggestions(netBalances, userNames);
        assertTrue(suggestions.isEmpty(), "Circular debt should result in 0 settlement transactions");
    }

    @Test
    @DisplayName("Complex Multi-user Settlement with Greedy Minimization")
    void testComplexSettlement() {
        // User 1: +500
        // User 2: +200
        // User 3: -400
        // User 4: -300
        Map<Long, BigDecimal> netBalances = new HashMap<>();
        netBalances.put(1L, new BigDecimal("500.00"));
        netBalances.put(2L, new BigDecimal("200.00"));
        netBalances.put(3L, new BigDecimal("-400.00"));
        netBalances.put(4L, new BigDecimal("-300.00"));

        Map<Long, String> userNames = new HashMap<>();
        userNames.put(1L, "User 1");
        userNames.put(2L, "User 2");
        userNames.put(3L, "User 3");
        userNames.put(4L, "User 4");

        List<SettlementSuggestionDto> suggestions = algorithm.computeSettlementSuggestions(netBalances, userNames);

        // Number of transactions should be at most V - 1 = 3
        assertTrue(suggestions.size() <= 3);

        BigDecimal totalDebtorPaid = suggestions.stream()
                .map(SettlementSuggestionDto::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(new BigDecimal("700.00"), totalDebtorPaid);
    }
}

