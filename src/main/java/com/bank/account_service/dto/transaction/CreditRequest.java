package com.bank.account_service.dto.transaction;

import lombok.Data;

@Data
public class CreditRequest {
    private double amount;
    private String transactionType;
    private String description;
}
