package com.expensesplit.controller;

import com.expensesplit.dto.ApiResponse;
import com.expensesplit.dto.RecordSettlementRequest;
import com.expensesplit.dto.SettlementGraphDto;
import com.expensesplit.dto.SettlementResponse;
import com.expensesplit.dto.SettlementSuggestionDto;
import com.expensesplit.service.SettlementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/settlements")
@CrossOrigin(origins = "*")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<SettlementSuggestionDto>>> getSettlementSuggestions(@PathVariable Long groupId) {
        List<SettlementSuggestionDto> suggestions = settlementService.getSettlementSuggestions(groupId);
        return ResponseEntity.ok(ApiResponse.success("Optimal settlement suggestions calculated using Greedy Algorithm", suggestions));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SettlementResponse>> recordSettlement(@PathVariable Long groupId,
                                                                            @Valid @RequestBody RecordSettlementRequest request) {
        SettlementResponse recorded = settlementService.recordSettlement(groupId, request);
        return new ResponseEntity<>(ApiResponse.success("Settlement payment recorded successfully", recorded), HttpStatus.CREATED);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<SettlementResponse>>> getSettlementHistory(@PathVariable Long groupId) {
        List<SettlementResponse> history = settlementService.getSettlementHistory(groupId);
        return ResponseEntity.ok(ApiResponse.success("Settlement history retrieved successfully", history));
    }

    @GetMapping("/graph")
    public ResponseEntity<ApiResponse<SettlementGraphDto>> getSettlementGraph(@PathVariable Long groupId) {
        SettlementGraphDto graph = settlementService.getSettlementGraph(groupId);
        return ResponseEntity.ok(ApiResponse.success("Settlement graph retrieved successfully", graph));
    }
}

