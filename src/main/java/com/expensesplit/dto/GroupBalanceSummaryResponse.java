package com.expensesplit.dto;

import java.math.BigDecimal;
import java.util.List;

public class GroupBalanceSummaryResponse {
    private Long groupId;
    private String groupName;
    private BigDecimal totalGroupExpense;
    private List<MemberBalanceDto> memberBalances;
    private boolean isSettled;

    public GroupBalanceSummaryResponse() {
    }

    public GroupBalanceSummaryResponse(Long groupId, String groupName, BigDecimal totalGroupExpense,
                                       List<MemberBalanceDto> memberBalances, boolean isSettled) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.totalGroupExpense = totalGroupExpense;
        this.memberBalances = memberBalances;
        this.isSettled = isSettled;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public BigDecimal getTotalGroupExpense() {
        return totalGroupExpense;
    }

    public void setTotalGroupExpense(BigDecimal totalGroupExpense) {
        this.totalGroupExpense = totalGroupExpense;
    }

    public List<MemberBalanceDto> getMemberBalances() {
        return memberBalances;
    }

    public void setMemberBalances(List<MemberBalanceDto> memberBalances) {
        this.memberBalances = memberBalances;
    }

    public boolean isSettled() {
        return isSettled;
    }

    public void setSettled(boolean settled) {
        isSettled = settled;
    }
}

