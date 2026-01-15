package com.bank.account_service.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
public class AccountCreateRequest {
    private UUID customerId;
    private String currency;
}
