package com.retailer.rewards_api.controller;

import com.retailer.rewards_api.dto.CustomerRewardsResponse;
import com.retailer.rewards_api.service.RewardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("/rewards")
    public ResponseEntity<List<CustomerRewardsResponse>> getAllCustomerRewards(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(rewardService.getRewardsForAllCustomers(startDate, endDate));
    }

    @GetMapping("/customers/{customerId}/rewards")
    public ResponseEntity<CustomerRewardsResponse> getCustomerRewards(
            @PathVariable Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(rewardService.getRewardsForCustomer(customerId, startDate, endDate));
    }
}
