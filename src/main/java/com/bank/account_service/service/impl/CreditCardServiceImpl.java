package com.bank.account_service.service.impl;

import com.bank.account_service.dto.card.CreditCardIssueResponse;
import com.bank.account_service.dto.card.CreditCardResponse;
import com.bank.account_service.entity.CreditCard;
import com.bank.account_service.enums.CardStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.CreditCardRepository;
import com.bank.account_service.service.CreditCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class CreditCardServiceImpl implements CreditCardService {

    private final CreditCardRepository repo;

    @Override
    public List<CreditCardResponse> getCards(UUID customerId) {
        return repo.findByCustomerId(customerId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public CreditCardIssueResponse issueCard(UUID customerId, double limit) {

        if (!repo.findByCustomerId(customerId).isEmpty()) {
            throw BusinessException.badRequest("Credit card already issued");
        }

        CreditCard card = CreditCard.builder()
                .customerId(customerId)
                .cardNumber("4111" + System.currentTimeMillis())
                .creditLimit(limit)
                .availableCredit(limit)
                .outstandingAmount(0)
                .status(CardStatus.ACTIVE)
                .build();

        repo.save(card);

        return CreditCardIssueResponse.builder()
                .cardNumber("XXXX-XXXX-XXXX-" +
                        card.getCardNumber().substring(card.getCardNumber().length() - 4))
                .creditLimit(limit)
                .status("ISSUED")
                .message("Credit card issued successfully")
                .build();
    }


    @Override
    public void block(UUID cardId) {
        CreditCard card = repo.findById(cardId)
                .orElseThrow(() -> BusinessException.badRequest("Card not found"));
        card.setStatus(CardStatus.BLOCKED);
    }

    private CreditCardResponse map(CreditCard c) {
        return CreditCardResponse.builder()
                .cardNumber("XXXX-XXXX-XXXX-" + c.getCardNumber().substring(c.getCardNumber().length() - 4))
                .creditLimit(c.getCreditLimit())
                .status(c.getStatus().name())
                .build();
    }
}
