package com.bank.account_service.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSyncRequest {
    private String accountNumber;
    private String customerId;
    private String accountType;
    private String passwordHash;
    private String status;
    private Double balance;
    private boolean primaryAccount;
    private String ifscCode;
}