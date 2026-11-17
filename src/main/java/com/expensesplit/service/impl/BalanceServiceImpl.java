package com.expensesplit.service.impl;

import com.expensesplit.dsa.BalanceCalculator;
import com.expensesplit.dto.GroupBalanceSummaryResponse;
import com.expensesplit.dto.MemberBalanceDto;
import com.expensesplit.entity.Expense;
import com.expensesplit.entity.ExpenseGroup;
import com.expensesplit.entity.Settlement;
import com.expensesplit.entity.User;
import com.expensesplit.repository.ExpenseRepository;
import com.expensesplit.repository.SettlementRepository;
import com.expensesplit.service.BalanceService;
import com.expensesplit.service.GroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class BalanceServiceImpl implements BalanceService {

    private final GroupService groupService;
    private final ExpenseRepository expenseRepository;
    private final SettlementRepository settlementRepository;
    private final BalanceCalculator balanceCalculator;

    public BalanceServiceImpl(GroupService groupService,
                              ExpenseRepository expenseRepository,
                              SettlementRepository settlementRepository,
                              BalanceCalculator balanceCalculator) {
        this.groupService = groupService;
        this.expenseRepository = expenseRepository;
        this.settlementRepository = settlementRepository;
        this.balanceCalculator = balanceCalculator;
    }

    @Override
    public GroupBalanceSummaryResponse getGroupBalances(Long groupId) {
        ExpenseGroup group = groupService.getGroupEntityById(groupId);
        List<User> members = groupService.getGroupMemberEntities(groupId);
        List<Expense> expenses = expenseRepository.findByGroupIdWithSplits(groupId);
        List<Settlement> settlements = settlementRepository.findByGroupIdOrderBySettlementDateDesc(groupId);

        List<MemberBalanceDto> memberBalances = balanceCalculator.calculateMemberBalances(members, expenses, settlements);

        BigDecimal totalGroupExpense = BigDecimal.ZERO;
        for (Expense expense : expenses) {
            totalGroupExpense = totalGroupExpense.add(expense.getTotalAmount());
        }
        totalGroupExpense = totalGroupExpense.setScale(2, RoundingMode.HALF_UP);

        boolean isSettled = true;
        for (MemberBalanceDto mb : memberBalances) {
            if (!"SETTLED".equals(mb.getStatus())) {
                isSettled = false;
                break;
            }
        }

        return new GroupBalanceSummaryResponse(
                group.getId(),
                group.getName(),
                totalGroupExpense,
                memberBalances,
                isSettled
        );
    }

    @Override
    public Map<Long, BigDecimal> getGroupNetBalances(Long groupId) {
        List<User> members = groupService.getGroupMemberEntities(groupId);
        List<Expense> expenses = expenseRepository.findByGroupIdWithSplits(groupId);
        List<Settlement> settlements = settlementRepository.findByGroupIdOrderBySettlementDateDesc(groupId);

        return balanceCalculator.calculateNetBalances(members, expenses, settlements);
    }
}

