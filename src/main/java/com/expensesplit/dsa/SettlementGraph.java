package com.expensesplit.dsa;

import com.expensesplit.dto.SettlementGraphDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * =========================================================================================
 * DSA IMPLEMENTATION: DIRECTED SETTLEMENT GRAPH
 * =========================================================================================
 *
 * Structure:
 * - Vertices (V): Users in the group (represented by UserVertex).
 * - Directed Edges (E): A directed edge from Vertex A to Vertex B represents that User A owes User B
 *   a specified weight (amount of money).
 * - Adjacency List: Map<Long, List<DirectedEdge>> where key is userId.
 *
 * Graph Representation:
 * - When visualising debts before settlement: directed edges show who owes whom.
 * - When visualising optimal settlement: directed edges show the minimal transactions required.
 *
 * Complexity Analysis:
 * -----------------------------------------------------------------------------------------
 * Space Complexity:
 * - Vertices: O(V) space.
 * - Adjacency List & Edges: O(V + E) space.
 *   In a settled plan, E <= V - 1, hence O(V) total space.
 *
 * Time Complexity:
 * - Adding a vertex: O(1) amortized using HashMap.
 * - Adding an edge: O(1) appending to an ArrayList.
 * - Graph construction from suggestions: O(V + E).
 * =========================================================================================
 */
@Component
public class SettlementGraph {

    public static class UserVertex {
        private final Long userId;
        private final String userName;
        private final BigDecimal netBalance;
        private final String role; // "DEBTOR", "CREDITOR", "SETTLED"

        public UserVertex(Long userId, String userName, BigDecimal netBalance) {
            this.userId = userId;
            this.userName = userName;
            this.netBalance = netBalance;
            if (netBalance.compareTo(BigDecimal.ZERO) > 0) {
                this.role = "CREDITOR";
            } else if (netBalance.compareTo(BigDecimal.ZERO) < 0) {
                this.role = "DEBTOR";
            } else {
                this.role = "SETTLED";
            }
        }

        public Long getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public BigDecimal getNetBalance() {
            return netBalance;
        }

        public String getRole() {
            return role;
        }
    }

    public static class DirectedEdge {
        private final Long fromUserId;
        private final String fromUserName;
        private final Long toUserId;
        private final String toUserName;
        private final BigDecimal amount;

        public DirectedEdge(Long fromUserId, String fromUserName, Long toUserId, String toUserName, BigDecimal amount) {
            this.fromUserId = fromUserId;
            this.fromUserName = fromUserName;
            this.toUserId = toUserId;
            this.toUserName = toUserName;
            this.amount = amount;
        }

        public Long getFromUserId() {
            return fromUserId;
        }

        public String getFromUserName() {
            return fromUserName;
        }

        public Long getToUserId() {
            return toUserId;
        }

        public String getToUserName() {
            return toUserName;
        }

        public BigDecimal getAmount() {
            return amount;
        }
    }

    private final Map<Long, UserVertex> vertices = new LinkedHashMap<>();
    private final Map<Long, List<DirectedEdge>> adjacencyList = new LinkedHashMap<>();
    private final List<DirectedEdge> allEdges = new ArrayList<>();

    public void clear() {
        vertices.clear();
        adjacencyList.clear();
        allEdges.clear();
    }

    public void addVertex(Long userId, String userName, BigDecimal netBalance) {
        UserVertex vertex = new UserVertex(userId, userName, netBalance);
        vertices.put(userId, vertex);
        adjacencyList.putIfAbsent(userId, new ArrayList<>());
    }

    public void addEdge(Long fromUserId, String fromUserName, Long toUserId, String toUserName, BigDecimal amount) {
        DirectedEdge edge = new DirectedEdge(fromUserId, fromUserName, toUserId, toUserName, amount);
        adjacencyList.computeIfAbsent(fromUserId, k -> new ArrayList<>()).add(edge);
        allEdges.add(edge);
    }

    public Map<Long, UserVertex> getVertices() {
        return Collections.unmodifiableMap(vertices);
    }

    public Map<Long, List<DirectedEdge>> getAdjacencyList() {
        return Collections.unmodifiableMap(adjacencyList);
    }

    public List<DirectedEdge> getAllEdges() {
        return Collections.unmodifiableList(allEdges);
    }

    /**
     * Converts graph into DTO representation for REST API and front-end rendering.
     */
    public SettlementGraphDto toDto() {
        List<SettlementGraphDto.UserNodeDto> vertexDtos = new ArrayList<>();
        for (UserVertex vertex : vertices.values()) {
            vertexDtos.add(new SettlementGraphDto.UserNodeDto(
                    vertex.getUserId(),
                    vertex.getUserName(),
                    vertex.getNetBalance(),
                    vertex.getRole()
            ));
        }

        List<SettlementGraphDto.PaymentEdgeDto> edgeDtos = new ArrayList<>();
        for (DirectedEdge edge : allEdges) {
            edgeDtos.add(new SettlementGraphDto.PaymentEdgeDto(
                    edge.getFromUserId(),
                    edge.getFromUserName(),
                    edge.getToUserId(),
                    edge.getToUserName(),
                    edge.getAmount()
            ));
        }

        return new SettlementGraphDto(vertexDtos, edgeDtos);
    }
}

