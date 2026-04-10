package com.retailer.rewards_api.service;

import com.retailer.rewards_api.dto.CustomerRewardsResponse;
import com.retailer.rewards_api.dto.MonthlyRewardResponse;
import com.retailer.rewards_api.entity.TransactionEntity;
import com.retailer.rewards_api.exception.InvalidDateRangeException;
import com.retailer.rewards_api.exception.ResourceNotFoundException;
import com.retailer.rewards_api.model.TransactionStatus;
import com.retailer.rewards_api.model.TransactionType;
import com.retailer.rewards_api.repository.CustomerRepository;
import com.retailer.rewards_api.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RewardService {

    private static final int MIN_SPEND_FOR_REWARDS = 50;
    private static final int BONUS_THRESHOLD = 100;
    private static final int STANDARD_POINTS_RATE = 1;
    private static final int BONUS_POINTS_RATE = 2;
    private static final int NO_POINTS = 0;

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    public RewardService(TransactionRepository transactionRepository,
            CustomerRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
    }

    public List<CustomerRewardsResponse> getRewardsForAllCustomers(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("startDate must not be after endDate");
        }

        var transactions = transactionRepository.findEligibleTransactions(
                startDate, endDate, TransactionStatus.COMPLETED, TransactionType.PURCHASE);

        var byCustomer = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getCustomer().getId()));

        return byCustomer.values().stream()
                .map(txns -> buildResponse(txns, startDate, endDate))
                .sorted(Comparator.comparing(CustomerRewardsResponse::getCustomerId))
                .toList();
    }

    public CustomerRewardsResponse getRewardsForCustomer(Long customerId, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("startDate must not be after endDate");
        }

        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        var transactions = transactionRepository.findEligibleTransactionsByCustomerId(
                customerId, startDate, endDate, TransactionStatus.COMPLETED, TransactionType.PURCHASE);

        if (transactions.isEmpty()) {
            return CustomerRewardsResponse.builder()
                    .customerId(customer.getId())
                    .customerName(customer.getName())
                    .startDate(startDate)
                    .endDate(endDate)
                    .monthlyRewards(Collections.emptyList())
                    .totalPoints(0)
                    .build();
        }

        return buildResponse(transactions, startDate, endDate);
    }

    // 2 points per dollar over 100, 1 point per dollar between 50 and 100
    int calculatePoints(BigDecimal amount) {
        int dollars = amount.intValue();
        if (dollars <= MIN_SPEND_FOR_REWARDS) {
            return NO_POINTS;
        }

        if (dollars <= BONUS_THRESHOLD) {
            return (dollars - MIN_SPEND_FOR_REWARDS) * STANDARD_POINTS_RATE;
        }

        return (dollars - BONUS_THRESHOLD) * BONUS_POINTS_RATE
                + (BONUS_THRESHOLD - MIN_SPEND_FOR_REWARDS) * STANDARD_POINTS_RATE;

    }

    private CustomerRewardsResponse buildResponse(List<TransactionEntity> transactions,
            LocalDate startDate, LocalDate endDate) {
        var customer = transactions.get(0).getCustomer();

        var byMonth = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> YearMonth.from(t.getTransactionDate()),
                        TreeMap::new, Collectors.toList()));

        List<MonthlyRewardResponse> monthlyRewards = new ArrayList<>();
        int totalPoints = 0;

        for (var entry : byMonth.entrySet()) {
            int points = entry.getValue().stream().mapToInt(t -> calculatePoints(t.getAmount())).sum();
            BigDecimal spend = entry.getValue().stream()
                    .map(TransactionEntity::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            monthlyRewards.add(MonthlyRewardResponse.builder()
                    .month(entry.getKey().toString())
                    .points(points)
                    .transactionCount(entry.getValue().size())
                    .totalSpend(spend)
                    .build());

            totalPoints += points;
        }

        return CustomerRewardsResponse.builder()
                .customerId(customer.getId())
                .customerName(customer.getName())
                .startDate(startDate)
                .endDate(endDate)
                .monthlyRewards(monthlyRewards)
                .totalPoints(totalPoints)
                .build();
    }
}
