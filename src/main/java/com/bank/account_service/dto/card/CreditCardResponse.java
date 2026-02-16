package com.bank.account_service.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardResponse {
    private String cardNumber;
    private double creditLimit;
    private double availableCredit;
    private double outstanding;
    private String status;
    private String message;
}
