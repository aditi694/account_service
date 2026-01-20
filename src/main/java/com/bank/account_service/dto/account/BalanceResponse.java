package com.bank.account_service.dto.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BalanceResponse {

    private String message;
    private UUID accountId;
    private BigDecimal oldBalance;
    private BigDecimal newBalance;
    private String currency;
    private LocalDateTime timestamp;

    public static BalanceResponse success(
            String message,
            UUID accountId,
            BigDecimal oldBalance,
            BigDecimal newBalance,
            String currency
    ) {
        return BalanceResponse.builder()
                .message(message)
                .accountId(accountId)
                .oldBalance(oldBalance)
                .newBalance(newBalance)
                .currency(currency)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
