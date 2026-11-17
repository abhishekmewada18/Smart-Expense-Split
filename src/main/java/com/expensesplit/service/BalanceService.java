package com.expensesplit.service;

import com.expensesplit.dto.GroupBalanceSummaryResponse;

import java.math.BigDecimal;
import java.util.Map;

public interface BalanceService {
    GroupBalanceSummaryResponse getGroupBalances(Long groupId);
    Map<Long, BigDecimal> getGroupNetBalances(Long groupId);
}

