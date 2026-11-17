package com.expensesplit.dsa;

import com.expensesplit.dto.MemberBalanceDto;
import com.expensesplit.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BalanceCalculatorTest {

    private BalanceCalculator balanceCalculator;
    private User rahul;
    private User amit;
    private User priya;

    @BeforeEach
    void setUp() {
        balanceCalculator = new BalanceCalculator();
        rahul = new User(1L, "Rahul", "rahul@example.com", "9876543210");
        amit = new User(2L, "Amit", "amit@example.com", "9876543211");
        priya = new User(3L, "Priya", "priya@example.com", "9876543212");
    }

    @Test
    @DisplayName("Verify Net Balances: Rahul pays ₹900 EQUAL among 3 people")
    void testRahulEqualExpense() {
        ExpenseGroup group = new ExpenseGroup(1L, "Goa Trip", "Trip", rahul);
        Expense expense = new Expense(1L, group, "Dinner", new BigDecimal("900.00"), rahul, SplitType.EQUAL, LocalDate.now());

        ExpenseSplit s1 = new ExpenseSplit(expense, rahul, new BigDecimal("300.00"), null, null);
        ExpenseSplit s2 = new ExpenseSplit(expense, amit, new BigDecimal("300.00"), null, null);
        ExpenseSplit s3 = new ExpenseSplit(expense, priya, new BigDecimal("300.00"), null, null);
        expense.setSplits(Arrays.asList(s1, s2, s3));

        List<User> members = Arrays.asList(rahul, amit, priya);
        List<Expense> expenses = List.of(expense);
        List<Settlement> settlements = new ArrayList<>();

        Map<Long, BigDecimal> netBalances = balanceCalculator.calculateNetBalances(members, expenses, settlements);

        assertEquals(new BigDecimal("600.00"), netBalances.get(1L));
        assertEquals(new BigDecimal("-300.00"), netBalances.get(2L));
        assertEquals(new BigDecimal("-300.00"), netBalances.get(3L));

        List<MemberBalanceDto> dtoList = balanceCalculator.calculateMemberBalances(members, expenses, settlements);
        assertEquals(3, dtoList.size());

        for (MemberBalanceDto dto : dtoList) {
            if (dto.getUserId().equals(1L)) {
                assertEquals("SHOULD_RECEIVE", dto.getStatus());
                assertEquals(new BigDecimal("600.00"), dto.getNetBalance());
            } else {
                assertEquals("OWES", dto.getStatus());
                assertEquals(new BigDecimal("-300.00"), dto.getNetBalance());
            }
        }
    }
}

