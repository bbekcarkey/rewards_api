package com.retailer.rewards_api.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRewardsResponse {

    private Long customerId;
    private String customerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<MonthlyRewardResponse> monthlyRewards;
    private int totalPoints;
}
