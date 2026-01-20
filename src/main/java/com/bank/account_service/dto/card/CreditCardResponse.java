package com.bank.account_service.dto.card;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditCardResponse {
    private String cardNumber;
    private double creditLimit;
    private double availableCredit;
    private double outstanding;
    private String status;
}
