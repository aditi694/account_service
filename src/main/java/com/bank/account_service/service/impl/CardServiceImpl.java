package com.bank.account_service.service.impl;

import com.bank.account_service.dto.card.DebitCardResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.DebitCard;
import com.bank.account_service.enums.CardStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.repository.DebitCardRepository;
import com.bank.account_service.service.CardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;
@Service
public class CardServiceImpl implements CardService {

    private final DebitCardRepository debitRepo;
    private final AccountRepository accountRepo;

    public CardServiceImpl(
            DebitCardRepository debitRepo,
            AccountRepository accountRepo
    ) {
        this.debitRepo = debitRepo;
        this.accountRepo = accountRepo;
    }

    @Override
    public DebitCardResponse getDebitCard(UUID accountId) {

        Account acc = accountRepo.findById(accountId)
                .orElseThrow(BusinessException::accountNotFound);

        DebitCard card = debitRepo.findByAccountNumber(acc.getAccountNumber())
                .orElseGet(() -> {
                    DebitCard newCard = DebitCard.builder()
                            .accountNumber(acc.getAccountNumber())
                            .cardNumber("5123" + System.currentTimeMillis())
                            .expiryDate(LocalDate.now().plusYears(5))
                            .dailyLimit(50000)
                            .usedToday(0)
                            .status(CardStatus.ACTIVE)
                            .issuedDate(LocalDate.now())
                            .build();

                    debitRepo.save(newCard);
                    return newCard;
                });

        return map(card);
    }

    @Override
    public void blockDebitCard(UUID accountId) {
        DebitCard card = getCard(accountId);
        card.setStatus(CardStatus.BLOCKED);
    }

    @Override
    public void unblockDebitCard(UUID accountId) {
        DebitCard card = getCard(accountId);
        card.setStatus(CardStatus.ACTIVE);
    }

    private DebitCard getCard(UUID accountId) {
        Account acc = accountRepo.findById(accountId)
                .orElseThrow(BusinessException::accountNotFound);

        return debitRepo.findByAccountNumber(acc.getAccountNumber())
                .orElseThrow(() -> BusinessException.badRequest("Debit card not issued"));
    }

    private DebitCardResponse map(DebitCard c) {
        return DebitCardResponse.builder()
                .cardNumber("XXXX-XXXX-XXXX-" + c.getCardNumber().substring(c.getCardNumber().length() - 4))
                .expiry(c.getExpiryDate().toString())
                .dailyLimit(c.getDailyLimit())
                .status(c.getStatus().name())
                .build();
    }
}
