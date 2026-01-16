package com.bank.account_service.dto.response;

import com.bank.account_service.entity.Account;
import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.enums.AccountType;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class AccountResponse {

    private UUID id;
    private UUID customerId;
    private String customerName;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private AccountStatus status;
    private String currency;

    public static AccountResponse from(Account a) {
        return AccountResponse.builder()
                .id(a.getId())
                .customerId(a.getCustomerId())
                .customerName(a.getCustomerName())
                .accountNumber(a.getAccountNumber())
                .accountType(a.getAccountType())
                .balance(a.getBalance())
                .status(a.getStatus())
                .currency(a.getCurrency())
                .build();
    }
}
