package com.bank.account_service.dto.response;

import com.bank.account_service.enums.AccountStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter @Builder
public class AccountResponse {
    private UUID id;
    private UUID customerId;
    private String accountNumber;
    private BigDecimal balance;
    private AccountStatus status;
}
