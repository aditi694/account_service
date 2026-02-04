package com.bank.account_service.service;

import com.bank.account_service.dto.card.DebitCardResponse;

import java.util.UUID;

public interface CardService {

    DebitCardResponse getDebitCard(UUID accountId);
    void blockDebitCard(UUID accountId);

    void unblockDebitCard(UUID accountId);
}
