package com.expensesplit.controller;

import com.expensesplit.dto.ApiResponse;
import com.expensesplit.dto.GroupBalanceSummaryResponse;
import com.expensesplit.service.BalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups/{groupId}/balances")
@CrossOrigin(origins = "*")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<GroupBalanceSummaryResponse>> getGroupBalances(@PathVariable Long groupId) {
        GroupBalanceSummaryResponse balances = balanceService.getGroupBalances(groupId);
        return ResponseEntity.ok(ApiResponse.success("Group balances calculated successfully", balances));
    }
}

