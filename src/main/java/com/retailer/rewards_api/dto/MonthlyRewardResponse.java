package com.retailer.rewards_api.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyRewardResponse {

    private String month;
    private int points;
    private int transactionCount;
    private BigDecimal totalSpend;
}
