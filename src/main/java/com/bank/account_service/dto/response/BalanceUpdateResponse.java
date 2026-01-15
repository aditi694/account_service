package com.bank.account_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BalanceUpdateResponse {

    private String message;
    private UUID accountId;
    private BigDecimal oldBalance;
    private BigDecimal newBalance;
    private String currency;
    private LocalDateTime timestamp;
}
