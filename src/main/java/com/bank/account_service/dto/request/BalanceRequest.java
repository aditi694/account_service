package com.bank.account_service.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class BalanceRequest {
    private BigDecimal amount;
}
