package com.bank.account_service.service;

import com.bank.account_service.dto.card.CreditCardIssueResponse;
import com.bank.account_service.dto.card.CreditCardResponse;

import java.util.List;
import java.util.UUID;

public interface CreditCardService {

    List<CreditCardResponse> getCards(UUID customerId);

    UUID applyCreditCard(UUID customerId, String cardHolderName);

    CreditCardIssueResponse approveRequest(UUID requestId);

    void rejectRequest(UUID requestId, String reason);
}
