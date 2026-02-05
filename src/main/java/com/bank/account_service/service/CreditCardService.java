package com.bank.account_service.service;

import com.bank.account_service.dto.card.CreditCardIssueResponse;
import com.bank.account_service.dto.card.CreditCardResponse;
import com.bank.account_service.entity.CreditCardRequest;
import com.bank.account_service.security.AuthUser;

import java.util.List;
import java.util.UUID;

public interface CreditCardService {

    UUID applyCreditCard(AuthUser user, String cardHolderName);

    CreditCardIssueResponse approveRequest(UUID requestId);

    void rejectRequest(UUID requestId, String reason);

    CreditCardResponse getCreditCardSummary(UUID customerId);

    List<CreditCardRequest> getPendingRequests();
}