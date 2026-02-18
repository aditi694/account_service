package com.bank.account_service.dto.card.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardIssueResponse {
    private String cardNumber;
    private double creditLimit;
    private String status;
    private String message;
}
