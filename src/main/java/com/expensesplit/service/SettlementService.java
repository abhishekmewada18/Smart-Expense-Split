package com.expensesplit.service;

import com.expensesplit.dto.RecordSettlementRequest;
import com.expensesplit.dto.SettlementGraphDto;
import com.expensesplit.dto.SettlementResponse;
import com.expensesplit.dto.SettlementSuggestionDto;

import java.util.List;

public interface SettlementService {
    List<SettlementSuggestionDto> getSettlementSuggestions(Long groupId);
    SettlementResponse recordSettlement(Long groupId, RecordSettlementRequest request);
    List<SettlementResponse> getSettlementHistory(Long groupId);
    SettlementGraphDto getSettlementGraph(Long groupId);
}

