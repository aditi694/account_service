package com.bank.account_service.dto.card.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DebitCardResponse {
    private String cardNumber;
    private String expiry;
    private int dailyLimit;
    private String status;
    private String message;
}
