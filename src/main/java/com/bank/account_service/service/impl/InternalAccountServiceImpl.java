package com.bank.account_service.service.impl;

import com.bank.account_service.dto.account.request.AccountSyncRequest;
import com.bank.account_service.dto.account.request.BalanceUpdateRequest;
import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.DebitCard;
import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.enums.AccountType;
import com.bank.account_service.enums.CardStatus;
import com.bank.account_service.exception.BusinessException;
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
                .requiresPasswordChange(false)
                .openingDate(LocalDateTime.now())
                .ifscCode(request.getIfscCode())
                .build();

        Account savedAccount = accountRepo.save(account);
//        log.info("Account created successfully: {}", savedAccount.getId());

        issueDebitCardAutomatically(savedAccount);

//        log.info("Account setup completed with debit card");
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
            log.info("Debit card issued automatically: {}", debitCard.getCardNumber());

        } catch (Exception e) {
            log.error("Failed to issue debit card", e);
        }
    }
    @Override
    public void credit(String accountNumber, BigDecimal amount) {

        Account account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(BusinessException::accountNotFound);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw BusinessException.accountInactive();
        }

        account.setBalance(account.getBalance().add(amount));
        accountRepo.save(account);
    }

    @Override
    public void debit(String accountNumber, BigDecimal amount) {

        Account account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(BusinessException::accountNotFound);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw BusinessException.accountInactive();
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw BusinessException.insufficientBalance();
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepo.save(account);
    }

    @Override
    public void transfer(String fromAccount,
                         String toAccount,
                         BigDecimal amount,
                         BigDecimal charges) {

        Account sender = accountRepo.findByAccountNumber(fromAccount)
                .orElseThrow(BusinessException::accountNotFound);

        Account receiver = accountRepo.findByAccountNumber(toAccount)
                .orElseThrow(BusinessException::accountNotFound);

        BigDecimal totalDebit = amount.add(charges);

        if (sender.getBalance().compareTo(totalDebit) < 0) {
            throw BusinessException.insufficientBalance();
        }

        sender.setBalance(sender.getBalance().subtract(totalDebit));
        receiver.setBalance(receiver.getBalance().add(amount));

        accountRepo.save(sender);
        accountRepo.save(receiver);
    }
    @Override
    public void updateBalance(BalanceUpdateRequest req) {

        Account account = accountRepo.findByAccountNumber(req.getAccountNumber())
                .orElseThrow(BusinessException::accountNotFound);

        if (req.getTransactionId().equals(account.getLastProcessedTransactionId())) {
            log.info("Duplicate transaction ignored: {}", req.getTransactionId());
            return;
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw BusinessException.accountInactive();
        }

        BigDecimal newBalance = account.getBalance().add(req.getDelta());

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw BusinessException.insufficientBalance();
        }

        account.setBalance(newBalance);
        account.setLastProcessedTransactionId(req.getTransactionId());

        accountRepo.save(account);
    }

    private String generateCardNumber() {
        return "4532" + System.currentTimeMillis();
    }
}