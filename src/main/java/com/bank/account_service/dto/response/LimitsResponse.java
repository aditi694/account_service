package com.bank.account_service.dto.response;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class LimitsResponse {
    private double dailyTransactionLimit;
    private double perTransactionLimit;
}
