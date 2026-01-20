package com.bank.account_service.dto.card;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DebitCardResponse {
    private String cardNumber;
    private String expiry;
    private int dailyLimit;
    private String status;
}
