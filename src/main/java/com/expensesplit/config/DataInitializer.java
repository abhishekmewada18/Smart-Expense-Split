package com.expensesplit.config;

import com.expensesplit.dto.ExpenseCreateRequest;
import com.expensesplit.dto.GroupCreateRequest;
import com.expensesplit.dto.SplitDetailRequest;
import com.expensesplit.dto.UserCreateRequest;
import com.expensesplit.dto.UserResponse;
import com.expensesplit.dto.GroupResponse;
import com.expensesplit.entity.SplitType;
import com.expensesplit.service.ExpenseService;
import com.expensesplit.service.GroupService;
import com.expensesplit.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserService userService;
    private final GroupService groupService;
    private final ExpenseService expenseService;

    public DataInitializer(UserService userService, GroupService groupService, ExpenseService expenseService) {
        this.userService = userService;
        this.groupService = groupService;
        this.expenseService = expenseService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("--- Initializing Smart Expense Split & Settlement Seed Data ---");

        // 1. Create Users
        UserResponse rahul = userService.createUser(new UserCreateRequest("Rahul Sharma", "rahul@example.com", "9876543210"));
        UserResponse amit = userService.createUser(new UserCreateRequest("Amit Verma", "amit@example.com", "9876543211"));
        UserResponse priya = userService.createUser(new UserCreateRequest("Priya Patel", "priya@example.com", "9876543212"));
        UserResponse vikram = userService.createUser(new UserCreateRequest("Vikram Singh", "vikram@example.com", "9876543213"));
        UserResponse sneha = userService.createUser(new UserCreateRequest("Sneha Rao", "sneha@example.com", "9876543214"));

        log.info("Created Users: Rahul (ID: {}), Amit (ID: {}), Priya (ID: {}), Vikram (ID: {}), Sneha (ID: {})",
                rahul.getId(), amit.getId(), priya.getId(), vikram.getId(), sneha.getId());

        // 2. Create Group 1: Goa Weekend Trip (Rahul, Amit, Priya)
        GroupResponse goaTrip = groupService.createGroup(new GroupCreateRequest(
                "Goa Weekend Trip",
                "Beach, dinner, and adventure activities",
                rahul.getId(),
                Arrays.asList(amit.getId(), priya.getId())
        ));
        log.info("Created Group 1: {} (ID: {})", goaTrip.getName(), goaTrip.getId());

        // 3. Add Expense to Goa Trip (Prompt Example: Rahul pays ₹900 split equally)
        List<SplitDetailRequest> goaSplits = Arrays.asList(
                new SplitDetailRequest(rahul.getId()),
                new SplitDetailRequest(amit.getId()),
                new SplitDetailRequest(priya.getId())
        );
        expenseService.addExpense(goaTrip.getId(), new ExpenseCreateRequest(
                "Seafood Dinner at Beachside Shack",
                new BigDecimal("900.00"),
                rahul.getId(),
                SplitType.EQUAL,
                LocalDate.now(),
                goaSplits
        ));
        log.info("Added ₹900 EQUAL Expense to Goa Trip paid by Rahul");

        // 4. Create Group 2: Flat 402 Roommates (Rahul, Amit, Vikram, Sneha)
        GroupResponse flatGroup = groupService.createGroup(new GroupCreateRequest(
                "Flat 402 Roommates",
                "Monthly apartment shared expenses",
                amit.getId(),
                Arrays.asList(rahul.getId(), vikram.getId(), sneha.getId())
        ));

        // Flat Expense 1: Groceries - Amit pays ₹2400 (EQUAL)
        expenseService.addExpense(flatGroup.getId(), new ExpenseCreateRequest(
                "Monthly Supermarket Groceries",
                new BigDecimal("2400.00"),
                amit.getId(),
                SplitType.EQUAL,
                LocalDate.now().minusDays(5),
                Arrays.asList(
                        new SplitDetailRequest(rahul.getId()),
                        new SplitDetailRequest(amit.getId()),
                        new SplitDetailRequest(vikram.getId()),
                        new SplitDetailRequest(sneha.getId())
                )
        ));

        // Flat Expense 2: WiFi bill - Vikram pays ₹1200 (EXACT)
        expenseService.addExpense(flatGroup.getId(), new ExpenseCreateRequest(
                "High Speed Fiber Internet",
                new BigDecimal("1200.00"),
                vikram.getId(),
                SplitType.EXACT,
                LocalDate.now().minusDays(3),
                Arrays.asList(
                        new SplitDetailRequest(rahul.getId(), new BigDecimal("300.00"), null),
                        new SplitDetailRequest(amit.getId(), new BigDecimal("300.00"), null),
                        new SplitDetailRequest(vikram.getId(), new BigDecimal("300.00"), null),
                        new SplitDetailRequest(sneha.getId(), new BigDecimal("300.00"), null)
                )
        ));

        // Flat Expense 3: Party Snacks - Sneha pays ₹1000 (PERCENTAGE)
        expenseService.addExpense(flatGroup.getId(), new ExpenseCreateRequest(
                "Weekend Party Snacks & Beverages",
                new BigDecimal("1000.00"),
                sneha.getId(),
                SplitType.PERCENTAGE,
                LocalDate.now().minusDays(1),
                Arrays.asList(
                        new SplitDetailRequest(rahul.getId(), null, new BigDecimal("40.00")),
                        new SplitDetailRequest(amit.getId(), null, new BigDecimal("30.00")),
                        new SplitDetailRequest(vikram.getId(), null, new BigDecimal("20.00")),
                        new SplitDetailRequest(sneha.getId(), null, new BigDecimal("10.00"))
                )
        ));

        log.info("--- Data Initialization Complete! Ready on http://localhost:8080 ---");
    }
}

