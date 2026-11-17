package com.expensesplit.repository;

import com.expensesplit.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT DISTINCT e FROM Expense e LEFT JOIN FETCH e.splits LEFT JOIN FETCH e.paidBy WHERE e.group.id = :groupId ORDER BY e.expenseDate DESC, e.id DESC")
    List<Expense> findByGroupIdWithSplits(@Param("groupId") Long groupId);

    List<Expense> findByGroupIdOrderByExpenseDateDesc(Long groupId);
}
