package com.bank.account_service.service.impl;

import com.bank.account_service.dto.card.CreditCardIssueResponse;
import com.bank.account_service.dto.card.CreditCardRequest;
import com.bank.account_service.dto.card.CreditCardResponse;
import com.bank.account_service.dto.client.TransactionClient;
import com.bank.account_service.entity.CreditCard;
import com.bank.account_service.enums.CardStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.CreditCardRepository;
import com.bank.account_service.repository.CreditCardRequestRepository;
import com.bank.account_service.service.CreditCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class CreditCardServiceImpl implements CreditCardService {

    private final CreditCardRepository cardRepo;
    private final CreditCardRequestRepository requestRepo;
    private final TransactionClient transactionClient;

    @Override
    public UUID applyCreditCard(UUID customerId, String cardHolderName) {

        if (!cardRepo.findByCustomerId(customerId).isEmpty()) {
            throw BusinessException.badRequest("Credit card already issued");
        }

        double totalDebit = transactionClient.getTotalDebit(customerId);

        // 🟢 AUTO APPROVAL
        if (totalDebit >= 25000) {
            double limit = calculateLimit(totalDebit);

            CreditCard card = CreditCard.builder()
                    .customerId(customerId)
                    .cardNumber("4111" + System.currentTimeMillis())
                    .creditLimit(limit)
                    .availableCredit(limit)
                    .outstandingAmount(0)
                    .status(CardStatus.ACTIVE)
                    .build();

            cardRepo.save(card);
            return null;
        }

        // 🔴 MANUAL
        CreditCardRequest req = CreditCardRequest.builder()
                .customerId(customerId)
                .cardHolderName(cardHolderName)
                .status(CardStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        requestRepo.save(req);
        return req.getId();
    }

    @Override
    public List<CreditCardResponse> getCards(UUID customerId) {
        return cardRepo.findByCustomerId(customerId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public CreditCardIssueResponse approveRequest(UUID requestId) {

        CreditCardRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> BusinessException.badRequest("Request not found"));

        double totalDebit = transactionClient.getTotalDebit(req.getCustomerId());

        if (totalDebit < 25000) {
            throw BusinessException.badRequest("Insufficient transaction history");
        }

        double limit = calculateLimit(totalDebit);

        CreditCard card = CreditCard.builder()
                .customerId(req.getCustomerId())
                .cardNumber("4111" + System.currentTimeMillis())
                .creditLimit(limit)
                .availableCredit(limit)
                .outstandingAmount(0)
                .status(CardStatus.ACTIVE)
                .build();

        cardRepo.save(card);

        req.setStatus(CardStatus.APPROVED);
        req.setApprovedLimit(limit);
        req.setDecidedAt(LocalDateTime.now());
        requestRepo.save(req);

        return CreditCardIssueResponse.builder()
                .cardNumber(mask(card.getCardNumber()))
                .creditLimit(limit)
                .status("ISSUED")
                .message("Approved based on transaction history")
                .build();
    }

    @Override
    public void rejectRequest(UUID requestId, String reason) {
        CreditCardRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> BusinessException.badRequest("Request not found"));

        req.setStatus(CardStatus.REJECTED);
        req.setRejectionReason(reason);
        req.setDecidedAt(LocalDateTime.now());
        requestRepo.save(req);
    }

    // helpers
    private double calculateLimit(double totalDebit) {
        return Math.min(totalDebit * 2, 200000);
    }

    private String mask(String n) {
        return "XXXX-XXXX-XXXX-" + n.substring(n.length() - 4);
    }

    private CreditCardResponse map(CreditCard card) {
        return CreditCardResponse.builder()
                .cardNumber(mask(card.getCardNumber()))
                .creditLimit(card.getCreditLimit())
                .availableCredit(card.getAvailableCredit())
                .outstanding(card.getOutstandingAmount())
                .status(card.getStatus().name())
                .build();
    }
}
