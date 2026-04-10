package com.retailer.rewards_api.service;

import com.retailer.rewards_api.entity.CustomerEntity;
import com.retailer.rewards_api.entity.TransactionEntity;
import com.retailer.rewards_api.exception.InvalidDateRangeException;
import com.retailer.rewards_api.exception.ResourceNotFoundException;
import com.retailer.rewards_api.model.TransactionStatus;
import com.retailer.rewards_api.model.TransactionType;
import com.retailer.rewards_api.repository.CustomerRepository;
import com.retailer.rewards_api.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

    @Mock TransactionRepository transactionRepository;
    @Mock CustomerRepository customerRepository;
    @InjectMocks RewardService rewardService;

    @Test
    void pointCalculation() {
        // no points at or below 50
        assertEquals(0, rewardService.calculatePoints(new BigDecimal("30.00")));
        assertEquals(0, rewardService.calculatePoints(new BigDecimal("50.00")));

        // 1 point per dollar in the 50-100 range
        assertEquals(1, rewardService.calculatePoints(new BigDecimal("51.00")));
        assertEquals(25, rewardService.calculatePoints(new BigDecimal("75.00")));
        assertEquals(50, rewardService.calculatePoints(new BigDecimal("100.00")));

        // 2 points per dollar above 100, plus 50 from the first band
        assertEquals(52, rewardService.calculatePoints(new BigDecimal("101.00")));
        assertEquals(90, rewardService.calculatePoints(new BigDecimal("120.00")));
    }

    @Test
    void centsGetTruncated() {
        // 99.99 should be treated as 99 because cents are truncated
        assertEquals(49, rewardService.calculatePoints(new BigDecimal("99.99")));
        // 50.99 truncates to 50, so no points
        assertEquals(0, rewardService.calculatePoints(new BigDecimal("50.99")));
    }

    @Test
    void largePurchase() {
        // 500 purchase - (400 * 2) + 50 = 850
        assertEquals(850, rewardService.calculatePoints(new BigDecimal("500.00")));
    }

    @Test
    void rejectsInvalidDateRange() {
        var march = LocalDate.of(2026, 3, 31);
        var jan = LocalDate.of(2026, 1, 1);

        assertThrows(InvalidDateRangeException.class,
                () -> rewardService.getRewardsForAllCustomers(march, jan));
    }

    @Test
    void throwsForNonexistentCustomer() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> rewardService.getRewardsForCustomer(999L,
                        LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31)));
    }

    @Test
    void customerWithNoTransactions() {
        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer(1L, "Customer 1")));
        when(transactionRepository.findEligibleTransactionsByCustomerId(
                eq(1L), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        var result = rewardService.getRewardsForCustomer(1L,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31));

        assertEquals(0, result.getTotalPoints());
        assertTrue(result.getMonthlyRewards().isEmpty());
    }

    @Test
    void groupsByMonth() {
        var c1 = customer(1L, "Customer 1");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c1));
        when(transactionRepository.findEligibleTransactionsByCustomerId(
                eq(1L), any(), any(), any(), any()))
                .thenReturn(List.of(
                        txn(c1, "120.00", "2026-01-05"),
                        txn(c1, "75.00", "2026-01-15"),
                        txn(c1, "150.00", "2026-02-20")
                ));

        var result = rewardService.getRewardsForCustomer(1L,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31));

        assertEquals(2, result.getMonthlyRewards().size());

        // jan: 120 (90 pts) + 75 (25 pts) = 115
        var jan = result.getMonthlyRewards().get(0);
        assertEquals("2026-01", jan.getMonth());
        assertEquals(115, jan.getPoints());
        assertEquals(2, jan.getTransactionCount());

        // feb: 150 gives 150 points
        var feb = result.getMonthlyRewards().get(1);
        assertEquals("2026-02", feb.getMonth());
        assertEquals(150, feb.getPoints());

        assertEquals(265, result.getTotalPoints());
    }

    @Test
    void separatesByCustomer() {
        var c1 = customer(1L, "Customer 1");
        var c2 = customer(2L, "Customer 2");

        when(transactionRepository.findEligibleTransactions(any(), any(), any(), any()))
                .thenReturn(List.of(
                        txn(c1, "120.00", "2026-01-05"),
                        txn(c2, "200.00", "2026-01-10")
                ));

        var results = rewardService.getRewardsForAllCustomers(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

        assertEquals(2, results.size());
        assertEquals(90, results.get(0).getTotalPoints());  // c1
        assertEquals(250, results.get(1).getTotalPoints()); // c2
    }

    // --

    private CustomerEntity customer(Long id, String name) {
        return CustomerEntity.builder().id(id).name(name).build();
    }

    private TransactionEntity txn(CustomerEntity customer, String amount, String date) {
        return TransactionEntity.builder()
                .customer(customer)
                .amount(new BigDecimal(amount))
                .transactionDate(LocalDate.parse(date))
                .status(TransactionStatus.COMPLETED)
                .type(TransactionType.PURCHASE)
                .build();
    }
}
