package com.bank.account_service.service.impl;

import com.bank.account_service.dto.account.AccountSyncRequest;
import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.DebitCard;
import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.enums.AccountType;
import com.bank.account_service.enums.CardStatus;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.repository.DebitCardRepository;
import com.bank.account_service.service.InternalAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InternalAccountServiceImpl implements InternalAccountService {

    private final AccountRepository accountRepo;
    private final DebitCardRepository debitCardRepo;

    @Override
    public void createAccount(AccountSyncRequest request) {

        log.info("=== CREATING ACCOUNT IN ACCOUNT SERVICE ===");
        log.info("Account Number: {}", request.getAccountNumber());
        log.info("Customer ID: {}", request.getCustomerId());

        if (accountRepo.findByAccountNumber(request.getAccountNumber()).isPresent()) {
            log.warn("Account already exists, skipping creation");
            return;
        }

        Account account = Account.builder()
                .accountNumber(request.getAccountNumber())
                .customerId(UUID.fromString(request.getCustomerId()))
                .accountType(AccountType.valueOf(request.getAccountType().toUpperCase()))
                .balance(BigDecimal.valueOf(request.getBalance()))
                .primaryAccount(request.isPrimaryAccount())
                .status(AccountStatus.valueOf(request.getStatus().toUpperCase()))
                .passwordHash(request.getPasswordHash())
                .requiresPasswordChange(false) // Customer set their own password
                .openingDate(LocalDateTime.now())
                .ifscCode(request.getIfscCode())
                .build();

        Account savedAccount = accountRepo.save(account);
        log.info("✅ Account created successfully: {}", savedAccount.getId());

        issueDebitCardAutomatically(savedAccount);

        log.info("✅ Account setup completed with debit card");
    }

    private void issueDebitCardAutomatically(Account account) {
        try {
            DebitCard debitCard = DebitCard.builder()
                    .accountNumber(account.getAccountNumber())
                    .cardNumber(generateCardNumber())
                    .expiryDate(LocalDate.now().plusYears(5))
                    .dailyLimit(50000)
                    .usedToday(0)
                    .status(CardStatus.ACTIVE)
                    .issuedDate(LocalDate.now())
                    .build();

            debitCardRepo.save(debitCard);
            log.info("✅ Debit card issued automatically: {}", debitCard.getCardNumber());

        } catch (Exception e) {
            log.error("Failed to issue debit card", e);
        }
    }

    private String generateCardNumber() {
        return "4532" + System.currentTimeMillis();
    }
}