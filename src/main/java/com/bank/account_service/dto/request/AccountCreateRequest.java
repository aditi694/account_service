package com.bank.account_service.dto.request;

import com.bank.account_service.enums.AccountType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
public class AccountCreateRequest {

    private UUID customerId;
    private String customerName;

    private AccountType accountType;

    private String branchName;
    private String ifscCode;

    private String currency;
}
