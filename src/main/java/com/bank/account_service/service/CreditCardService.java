package com.bank.account_service.service;

import com.bank.account_service.dto.card.CreditCardApplyRequest;
import com.bank.account_service.dto.card.CreditCardIssueResponse;
import com.bank.account_service.dto.card.CreditCardRequest;
import com.bank.account_service.dto.card.CreditCardResponse;

import java.util.List;
import java.util.UUID;

public interface CreditCardService {

    UUID applyCreditCard(UUID customerId, String cardHolderName);

    CreditCardIssueResponse approveRequest(UUID requestId);

    void rejectRequest(UUID requestId, String reason);

    List<CreditCardResponse> getCards(UUID customerId);

    CreditCardResponse getCardStatus(UUID customerId);

    List<CreditCardRequest> getPendingRequests();
}