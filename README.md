# Smart Expense Split & Settlement System

An industry-standard backend application built with **Spring Boot 3**, **Spring Data JPA**, **Hibernate**, and **MySQL/H2**, featuring real-time expense splitting and an optimal debt settlement engine using **Data Structures and Algorithms (DSA)**.

---

## 1. Project Overview

The **Smart Expense Split & Settlement System** (Splitwise architecture) allows groups of users to track shared expenses, calculate net balances dynamically, and settle dues with the **minimum number of cash transactions** possible.

### Key Highlights
- **Full CRUD for Users, Groups, and Expenses**.
- **Three Split Strategies**: `EQUAL`, `EXACT`, and `PERCENTAGE` splits with rounding protection.
- **Real-Time Balance Engine**: Uses `HashMap` for $O(1)$ net balance updates.
- **Greedy Min-Cash-Flow Algorithm**: Uses two `PriorityQueue`s (Max-Heaps) to minimize settlement transactions.
- **Directed Graph Representation**: Vertices = Users, Directed Edges = Optimized payments.
- **Dual Database Support**: Ready for **MySQL** (production) and embedded **H2** (zero-config local dev).
- **Interactive Web Dashboard**: Built-in visual UI at `http://localhost:8080/`.
- **Swagger / OpenAPI Documentation**: Available at `http://localhost:8080/swagger-ui.html`.
- **Postman Collection Included**: Complete `Smart_Expense_Split_Settlement.postman_collection.json`.

---

## 2. Technology Stack

- **Language**: Java 17+
- **Framework**: Spring Boot 3.3.5
- **Web**: Spring MVC (RESTful API Architecture)
- **Persistence**: Spring Data JPA, Hibernate ORM
- **Database**: MySQL 8.x / H2 Database Engine
- **Validation**: Jakarta Bean Validation (`@Valid`, `@NotNull`, `@Size`, etc.)
- **Documentation**: SpringDoc OpenAPI 3 (Swagger UI)
- **Build Tool**: Apache Maven 3.9+
- **Testing**: JUnit 5, Mockito

---

## 3. Database Design & Entity Relationships

The relational schema is composed of 6 entities:

```
+------------+       1:N       +-------------------+       1:N       +---------------+
|    User    | <-------------- |   GroupMember     | --------------> |  ExpenseGroup |
+------------+                 +-------------------+                 +---------------+
      ^                                                                      ^
      |                                                                      |
      | 1:N (paidBy)                                                         | 1:N
      |                                                                      |
+------------+       1:N       +-------------------+                         |
|  Expense   | --------------> |   ExpenseSplit    |                         |
+------------+                 +-------------------+                         |
      ^                                                                      |
      |                                                                      |
      +----------------------------------------------------------------------+
                                         |
                                         | 1:N
                                         v
                                  +--------------+
                                  |  Settlement  |
                                  +--------------+
```

### Entities

1. **User (`users`)**:
   - `id` (PK, BIGINT, AUTO_INCREMENT)
   - `name` (VARCHAR(100), NOT NULL)
   - `email` (VARCHAR(150), UNIQUE, NOT NULL)
   - `phone` (VARCHAR(20))
   - `createdAt`, `updatedAt` (TIMESTAMP)

2. **ExpenseGroup (`expense_groups`)**:
   - `id` (PK, BIGINT, AUTO_INCREMENT)
   - `name` (VARCHAR(100), NOT NULL)
   - `description` (VARCHAR(255))
   - `createdBy` (FK -> users.id)
   - `createdAt`, `updatedAt` (TIMESTAMP)

3. **GroupMember (`group_members`)**:
   - `id` (PK, BIGINT, AUTO_INCREMENT)
   - `group_id` (FK -> expense_groups.id)
   - `user_id` (FK -> users.id)
   - `joinedAt` (TIMESTAMP)
   - **Constraint**: `UNIQUE(group_id, user_id)`

4. **Expense (`expenses`)**:
   - `id` (PK, BIGINT, AUTO_INCREMENT)
   - `group_id` (FK -> expense_groups.id)
   - `description` (VARCHAR(200), NOT NULL)
   - `totalAmount` (DECIMAL(12, 2), NOT NULL)
   - `paidBy` (FK -> users.id)
   - `splitType` (ENUM: `EQUAL`, `EXACT`, `PERCENTAGE`)
   - `expenseDate` (DATE)
   - `createdAt`, `updatedAt` (TIMESTAMP)

5. **ExpenseSplit (`expense_splits`)**:
   - `id` (PK, BIGINT, AUTO_INCREMENT)
   - `expense_id` (FK -> expenses.id)
   - `user_id` (FK -> users.id)
   - `amount` (DECIMAL(12, 2), NOT NULL)
   - `percentage` (DECIMAL(5, 2), NULLABLE)
   - `exactAmount` (DECIMAL(12, 2), NULLABLE)

6. **Settlement (`settlements`)**:
   - `id` (PK, BIGINT, AUTO_INCREMENT)
   - `group_id` (FK -> expense_groups.id)
   - `paid_by_user_id` (FK -> users.id, Debtor)
   - `paid_to_user_id` (FK -> users.id, Creditor)
   - `amount` (DECIMAL(12, 2), NOT NULL)
   - `settlementDate` (DATE)
   - `notes` (VARCHAR(255))
   - `status` (ENUM: `PENDING`, `COMPLETED`)
   - `createdAt` (TIMESTAMP)

---

## 4. DSA Implementation & Complexity Analysis

### 4.1. HashMap (Net Balance Calculation)
- **Data Structure**: `Map<Long, BigDecimal> netBalances = new HashMap<>()`.
- **Logic**:
  - For each expense:
    - Payer gets credited: $\text{netBalances}[\text{payer}] \mathrel{+}= \text{totalAmount}$
    - Each split participant gets debited: $\text{netBalances}[\text{user}] \mathrel{-}= \text{splitAmount}$
  - For each completed settlement:
    - Debtor's debt decreases: $\text{netBalances}[\text{debtor}] \mathrel{+}= \text{amount}$
    - Creditor's credit decreases: $\text{netBalances}[\text{creditor}] \mathrel{-}= \text{amount}$
- **Time Complexity**: $O(E \times S + K + V)$
  - $E$: number of expenses
  - $S$: average splits per expense
  - $K$: number of settlements
  - $V$: number of group members
  - HashMap updates/lookups take $O(1)$ amortized time.
- **Space Complexity**: $O(V)$ to store entries for all members.

---

### 4.2. PriorityQueue & Greedy Settlement Algorithm
- **Problem**: Minimize the number of cash transactions required to settle all debts.
- **Data Structures**:
  1. `PriorityQueue<BalanceNode> maxCreditors`: Max-Heap ordered descending by credit amount.
  2. `PriorityQueue<BalanceNode> maxDebtors`: Max-Heap ordered descending by absolute debt amount.
- **Algorithm**:
  1. Filter users with balance $> 0$ into `maxCreditors`.
  2. Filter users with balance $< 0$ into `maxDebtors` (storing $|\text{balance}|$).
  3. While both heaps are non-empty:
     - Poll $C$ (maximum creditor) and $D$ (maximum debtor).
     - $\text{settleAmount} = \min(C.\text{amount}, D.\text{amount})$.
     - Record transaction: **$D$ pays $C$ $\text{settleAmount}$**.
     - If $C$ still has balance remaining, offer back to `maxCreditors`.
     - If $D$ still has debt remaining, offer back to `maxDebtors`.
- **Proof of Optimality**:
  In each step, at least one person's net balance reaches zero. Therefore, an input with $V$ unbalanced members will terminate in at most $V - 1$ transactions.
- **Time Complexity**: $O(V \log V)$
  - Heap insertion takes $O(V \log V)$.
  - Settle loop runs at most $V - 1$ times. Each poll and offer takes $O(\log V)$.
- **Space Complexity**: $O(V)$ for the two heaps.

---

### 4.3. Directed Graph
- **Data Structure**: Adjacency list `Map<Long, List<DirectedEdge>>`.
- **Components**:
  - **Vertex (User)**: Contains userId, name, and role (`CREDITOR`, `DEBTOR`, `SETTLED`).
  - **Directed Edge (Payment)**: Points from Debtor $\to$ Creditor with weight equal to the transaction amount.
- **Space Complexity**: $O(V + E)$ where $E \le V - 1$ in the optimized plan.

---

## 5. Business Logic Walkthrough (Rahul, Amit, Priya)

**Scenario**:
1. Rahul, Amit, and Priya form a group ("Goa Trip").
2. Rahul pays **₹900** for dinner, split **EQUAL** among all 3.
3. Share per person = $\frac{₹900}{3} = ₹300$.

### Net Balance Calculation:
- **Rahul**: Paid ₹900, share ₹300 $\implies$ Net Balance = $+₹600$ (Creditor / Should Receive)
- **Amit**: Paid ₹0, share ₹300 $\implies$ Net Balance = $-₹300$ (Debtor / Owes)
- **Priya**: Paid ₹0, share ₹300 $\implies$ Net Balance = $-₹300$ (Debtor / Owes)

### Greedy Settlement Output:
- `maxCreditors`: `[Rahul: 600]`
- `maxDebtors`: `[Amit: 300, Priya: 300]`
- **Step 1**: Poll Rahul (600) and Amit (300). Settle $\min(600, 300) = 300$.
  - Transaction 1: **Amit pays Rahul ₹300**.
  - Rahul remaining = 300. Re-insert Rahul into `maxCreditors`.
- **Step 2**: Poll Rahul (300) and Priya (300). Settle $\min(300, 300) = 300$.
  - Transaction 2: **Priya pays Rahul ₹300**.
  - Both settled!
- **Total Transactions**: 2 (Minimal).

---

## 6. REST API Endpoints

### 6.1. Users
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/users` | Register a new user |
| `GET` | `/api/users` | List all users |
| `GET` | `/api/users/{id}` | Get user by ID |
| `PUT` | `/api/users/{id}` | Update user details |
| `DELETE` | `/api/users/{id}` | Delete user |

### 6.2. Groups
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/groups` | Create an expense group |
| `GET` | `/api/groups` | Get all groups |
| `GET` | `/api/groups/{id}` | Get group details |
| `POST` | `/api/groups/{groupId}/members` | Add member to group |
| `DELETE` | `/api/groups/{groupId}/members/{userId}` | Remove member from group |
| `GET` | `/api/groups/{groupId}/members` | View group members |

### 6.3. Expenses
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/groups/{groupId}/expenses` | Add expense (EQUAL, EXACT, PERCENTAGE) |
| `GET` | `/api/groups/{groupId}/expenses` | View all group expenses |
| `GET` | `/api/expenses/{expenseId}` | Get expense by ID |
| `PUT` | `/api/expenses/{expenseId}` | Update expense |
| `DELETE` | `/api/expenses/{expenseId}` | Delete expense |

### 6.4. Balances & Settlements
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/groups/{groupId}/balances` | Calculate and view member balances |
| `GET` | `/api/groups/{groupId}/settlements/suggestions` | Get optimal settlement plan (DSA Greedy) |
| `POST` | `/api/groups/{groupId}/settlements` | Record a completed payment |
| `GET` | `/api/groups/{groupId}/settlements/history` | View settlement history |
| `GET` | `/api/groups/{groupId}/settlements/graph` | Directed settlement graph representation |

---

## 7. How to Run the Project

### Prerequisites
- Java 17 or higher (`java -version`)
- Maven 3.9+ (`mvn -version`)

### Quick Start (Default In-Memory Mode)
1. Open terminal in the project directory:
   ```bash
   mvn clean spring-boot:run
   ```
2. Open your browser:
   - **Interactive Web App**: [http://localhost:8080/](http://localhost:8080/)
   - **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - **H2 Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:expensedb`, User: `sa`, Password: *(empty)*)

### Switching to MySQL
1. Ensure your MySQL server is running on port 3306.
2. In `src/main/resources/application.properties`, change:
   ```properties
   spring.profiles.active=mysql
   ```
   Or launch with:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

---

## 8. Running Automated Tests

Run the full suite of unit and integration tests:
```bash
mvn test
```
Includes tests for:
- Greedy settlement algorithm with dual PriorityQueues
- Rahul-Amit-Priya test case verification
- Circular debt cancellation
- Net balance calculation engine

