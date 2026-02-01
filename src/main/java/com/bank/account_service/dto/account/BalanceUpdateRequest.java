package com.bank.account_service.dto.account;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceUpdateRequest {

    private String accountNumber;
    private BigDecimal delta;          // +credit, -debit
    private String transactionId;      // saga/txn id
}