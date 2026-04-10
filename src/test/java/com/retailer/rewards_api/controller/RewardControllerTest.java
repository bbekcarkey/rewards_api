package com.retailer.rewards_api.controller;

import com.retailer.rewards_api.dto.CustomerRewardsResponse;
import com.retailer.rewards_api.dto.MonthlyRewardResponse;
import com.retailer.rewards_api.exception.InvalidDateRangeException;
import com.retailer.rewards_api.exception.ResourceNotFoundException;
import com.retailer.rewards_api.service.RewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardController.class)
class RewardControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean RewardService rewardService;

    @Test
    void getRewardsForAllCustomers() throws Exception {
        when(rewardService.getRewardsForAllCustomers(any(), any()))
                .thenReturn(List.of(
                        CustomerRewardsResponse.builder()
                                .customerId(1L).customerName("Customer 1")
                                .startDate(LocalDate.of(2026, 1, 1))
                                .endDate(LocalDate.of(2026, 3, 31))
                                .monthlyRewards(List.of(
                                        MonthlyRewardResponse.builder()
                                                .month("2026-01").points(365)
                                                .transactionCount(3).totalSpend(new BigDecimal("395.50")).build()))
                                .totalPoints(567).build()
                ));

        mockMvc.perform(get("/api/v1/rewards")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-03-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[0].totalPoints").value(567))
                .andExpect(jsonPath("$[0].monthlyRewards[0].month").value("2026-01"));
    }

    @Test
    void getRewardsForOneCustomer() throws Exception {
        when(rewardService.getRewardsForCustomer(eq(1L), any(), any()))
                .thenReturn(CustomerRewardsResponse.builder()
                        .customerId(1L).customerName("Customer 1")
                        .startDate(LocalDate.of(2026, 1, 1))
                        .endDate(LocalDate.of(2026, 1, 31))
                        .monthlyRewards(List.of()).totalPoints(365).build());

        mockMvc.perform(get("/api/v1/customers/1/rewards")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.totalPoints").value(365));
    }

    @Test
    void notFoundForUnknownCustomer() throws Exception {
        when(rewardService.getRewardsForCustomer(eq(999L), any(), any()))
                .thenThrow(new ResourceNotFoundException("Customer not found with id: 999"));

        mockMvc.perform(get("/api/v1/customers/999/rewards")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-03-31"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found with id: 999"));
    }

    @Test
    void badRequestForInvalidDateRange() throws Exception {
        when(rewardService.getRewardsForAllCustomers(any(), any()))
                .thenThrow(new InvalidDateRangeException("startDate must not be after endDate"));

        mockMvc.perform(get("/api/v1/rewards")
                        .param("startDate", "2026-03-31")
                        .param("endDate", "2026-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void badRequestWhenMissingRequiredParam() throws Exception {
        mockMvc.perform(get("/api/v1/rewards")
                        .param("startDate", "2026-01-01"))
                .andExpect(status().isBadRequest());
    }
}
