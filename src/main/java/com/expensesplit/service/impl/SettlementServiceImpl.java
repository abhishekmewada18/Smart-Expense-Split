package com.expensesplit.service.impl;

import com.expensesplit.dsa.GreedySettlementAlgorithm;
import com.expensesplit.dsa.SettlementGraph;
import com.expensesplit.dto.RecordSettlementRequest;
import com.expensesplit.dto.SettlementGraphDto;
import com.expensesplit.dto.SettlementResponse;
import com.expensesplit.dto.SettlementSuggestionDto;
import com.expensesplit.entity.ExpenseGroup;
import com.expensesplit.entity.Settlement;
import com.expensesplit.entity.SettlementStatus;
import com.expensesplit.entity.User;
import com.expensesplit.exception.BadRequestException;
import com.expensesplit.repository.SettlementRepository;
import com.expensesplit.service.BalanceService;
import com.expensesplit.service.GroupService;
import com.expensesplit.service.SettlementService;
import com.expensesplit.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class SettlementServiceImpl implements SettlementService {

    private final SettlementRepository settlementRepository;
    private final GroupService groupService;
    private final UserService userService;
    private final BalanceService balanceService;
    private final GreedySettlementAlgorithm greedyAlgorithm;

    public SettlementServiceImpl(SettlementRepository settlementRepository,
                                 GroupService groupService,
                                 UserService userService,
                                 BalanceService balanceService,
                                 GreedySettlementAlgorithm greedyAlgorithm) {
        this.settlementRepository = settlementRepository;
        this.groupService = groupService;
        this.userService = userService;
        this.balanceService = balanceService;
        this.greedyAlgorithm = greedyAlgorithm;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SettlementSuggestionDto> getSettlementSuggestions(Long groupId) {
        groupService.getGroupEntityById(groupId);

        Map<Long, BigDecimal> netBalances = balanceService.getGroupNetBalances(groupId);
        List<User> members = groupService.getGroupMemberEntities(groupId);

        Map<Long, String> userNames = new HashMap<>();
        for (User user : members) {
            userNames.put(user.getId(), user.getName());
        }

        return greedyAlgorithm.computeSettlementSuggestions(netBalances, userNames);
    }

    @Override
    public SettlementResponse recordSettlement(Long groupId, RecordSettlementRequest request) {
        ExpenseGroup group = groupService.getGroupEntityById(groupId);
        User payer = userService.getUserEntityById(request.getPaidById());
        User receiver = userService.getUserEntityById(request.getPaidToId());

        if (payer.getId().equals(receiver.getId())) {
            throw new BadRequestException("Payer and receiver cannot be the same user in a settlement");
        }

        Settlement settlement = new Settlement();
        settlement.setGroup(group);
        settlement.setPaidBy(payer);
        settlement.setPaidTo(receiver);
        settlement.setAmount(request.getAmount().setScale(2, RoundingMode.HALF_UP));
        settlement.setSettlementDate(request.getSettlementDate() != null ? request.getSettlementDate() : LocalDate.now());
        settlement.setNotes(request.getNotes() != null ? request.getNotes().trim() : null);
        settlement.setStatus(SettlementStatus.COMPLETED);

        Settlement saved = settlementRepository.save(settlement);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SettlementResponse> getSettlementHistory(Long groupId) {
        groupService.getGroupEntityById(groupId);
        List<Settlement> history = settlementRepository.findByGroupIdOrderBySettlementDateDesc(groupId);
        List<SettlementResponse> responses = new ArrayList<>();
        for (Settlement s : history) {
            responses.add(mapToResponse(s));
        }
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public SettlementGraphDto getSettlementGraph(Long groupId) {
        groupService.getGroupEntityById(groupId);

        Map<Long, BigDecimal> netBalances = balanceService.getGroupNetBalances(groupId);
        List<User> members = groupService.getGroupMemberEntities(groupId);

        Map<Long, String> userNames = new HashMap<>();
        for (User u : members) {
            userNames.put(u.getId(), u.getName());
        }

        // 1. Build graph vertices
        SettlementGraph graph = new SettlementGraph();
        for (User member : members) {
            BigDecimal balance = netBalances.getOrDefault(member.getId(), BigDecimal.ZERO);
            graph.addVertex(member.getId(), member.getName(), balance);
        }

        // 2. Compute optimal settlement suggestions and add as directed edges
        List<SettlementSuggestionDto> suggestions = greedyAlgorithm.computeSettlementSuggestions(netBalances, userNames);
        for (SettlementSuggestionDto suggestion : suggestions) {
            graph.addEdge(
                    suggestion.getFromUserId(),
                    suggestion.getFromUserName(),
                    suggestion.getToUserId(),
                    suggestion.getToUserName(),
                    suggestion.getAmount()
            );
        }

        return graph.toDto();
    }

    private SettlementResponse mapToResponse(Settlement s) {
        return new SettlementResponse(
                s.getId(),
                s.getGroup().getId(),
                s.getGroup().getName(),
                s.getPaidBy().getId(),
                s.getPaidBy().getName(),
                s.getPaidTo().getId(),
                s.getPaidTo().getName(),
                s.getAmount(),
                s.getSettlementDate(),
                s.getNotes(),
                s.getStatus(),
                s.getCreatedAt()
        );
    }
}

