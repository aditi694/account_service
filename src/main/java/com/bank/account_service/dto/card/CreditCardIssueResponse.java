package com.bank.account_service.dto.card;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditCardIssueResponse {
    private String cardNumber;
    private double creditLimit;
    private String status;
    private String message;
}
