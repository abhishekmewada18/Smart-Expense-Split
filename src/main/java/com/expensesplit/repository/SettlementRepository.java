package com.expensesplit.repository;

import com.expensesplit.entity.Settlement;
import com.expensesplit.entity.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    @Query("SELECT s FROM Settlement s JOIN FETCH s.paidBy JOIN FETCH s.paidTo WHERE s.group.id = :groupId ORDER BY s.settlementDate DESC, s.id DESC")
    List<Settlement> findByGroupIdOrderBySettlementDateDesc(@Param("groupId") Long groupId);

    List<Settlement> findByGroupIdAndStatus(Long groupId, SettlementStatus status);
}
