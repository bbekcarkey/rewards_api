package com.retailer.rewards_api.repository;

import com.retailer.rewards_api.entity.TransactionEntity;
import com.retailer.rewards_api.model.TransactionStatus;
import com.retailer.rewards_api.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Query("SELECT t FROM TransactionEntity t JOIN FETCH t.customer " +
           "WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.status = :status AND t.type = :type")
    List<TransactionEntity> findEligibleTransactions(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") TransactionStatus status,
            @Param("type") TransactionType type);

    @Query("SELECT t FROM TransactionEntity t JOIN FETCH t.customer " +
           "WHERE t.customer.id = :customerId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.status = :status AND t.type = :type")
    List<TransactionEntity> findEligibleTransactionsByCustomerId(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") TransactionStatus status,
            @Param("type") TransactionType type);
}
