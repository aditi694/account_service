package com.bank.account_service.service;

import com.bank.account_service.dto.card.CreditCardIssueResponse;
import com.bank.account_service.dto.card.CreditCardResponse;

import java.util.List;
import java.util.UUID;

public interface CreditCardService {

    List<CreditCardResponse> getCards(UUID customerId);

    CreditCardIssueResponse issueCard(UUID customerId, double limit);

    void block(UUID cardId);
}
