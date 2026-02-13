package com.bank.account_service.service.impl;

import com.bank.account_service.dto.card.CreditCardIssueResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.CreditCardRequest;
import com.bank.account_service.dto.card.CreditCardResponse;
import com.bank.account_service.dto.client.TransactionClient;
import com.bank.account_service.entity.CreditCard;
import com.bank.account_service.enums.CardStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.repository.CreditCardRepository;
import com.bank.account_service.repository.CreditCardRequestRepository;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.CreditCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreditCardServiceImpl implements CreditCardService {

    private final CreditCardRepository creditCardRepo;
    private final CreditCardRequestRepository requestRepo;
    private final TransactionClient transactionClient;
    private final AccountRepository accountRepository;

    private static final double AUTO_APPROVAL_THRESHOLD = 25_000.0;
    private static final double DEFAULT_CREDIT_LIMIT = 50_000.0;

    @Override
    public UUID applyCreditCard(AuthUser user, String cardHolderName) {

        UUID customerId = user.getCustomerId();
        UUID accountId = user.getAccountId();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        BusinessException.notFound("Account not found"));

        String storedName = normalize(account.getAccountHolderName());
        String inputName = normalize(cardHolderName);

        if (!storedName.equals(inputName)) {
            throw BusinessException.badRequest(
                    "Card holder name must match account holder name"
            );
        }

        boolean hasActive = creditCardRepo.findByCustomerId(customerId)
                .stream()
                .anyMatch(c -> c.getStatus() == CardStatus.ACTIVE);

        if (hasActive) {
            throw BusinessException.conflict("Active credit card already exists");
        }

        if (requestRepo.existsByCustomerIdAndStatus(customerId, CardStatus.PENDING)) {
            throw BusinessException.conflict("Credit card request already pending");
        }

        double totalDebit = fetchTotalDebit(customerId);

        if (totalDebit >= AUTO_APPROVAL_THRESHOLD) {
            issueCard(customerId);
            return null;
        }

        CreditCardRequest request = CreditCardRequest.builder()
                .customerId(customerId)
                .cardHolderName(account.getAccountHolderName())
                .status(CardStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        return requestRepo.save(request).getId();
    }

    private String normalize(String name) {
        return name == null
                ? ""
                : name.trim()
                .replaceAll("\\s+", " ")
                .toUpperCase();
    }

    private double fetchTotalDebit(UUID customerId) {
        try {
            return transactionClient.getTotalDebit(customerId);
        } catch (Exception e) {
            log.warn("Failed to fetch transaction summary for customer={}", customerId);
            return 0.0;
        }
    }

    @Override
    public CreditCardIssueResponse approveRequest(UUID requestId) {

        CreditCardRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> BusinessException.notFound("Credit card request not found"));

        if (req.getStatus() != CardStatus.PENDING) {
            throw BusinessException.badRequest("Request is not pending");
        }

        CreditCard card = issueCard(req.getCustomerId());

        req.setStatus(CardStatus.APPROVED);
        req.setApprovedLimit(card.getCreditLimit());
        req.setDecidedAt(LocalDateTime.now());

        return CreditCardIssueResponse.builder()
                .cardNumber(maskCardNumber(card.getCardNumber()))
                .creditLimit(card.getCreditLimit())
                .status("APPROVED")
                .message("Credit card approved and issued successfully")
                .build();
    }
    private CreditCard issueCard(UUID customerId) {

        CreditCard card = CreditCard.builder()
                .customerId(customerId)
                .cardNumber(generateCardNumber())
                .creditLimit(DEFAULT_CREDIT_LIMIT)
                .availableCredit(DEFAULT_CREDIT_LIMIT)
                .outstandingAmount(0.0)
                .status(CardStatus.ACTIVE)
                .build();

        return creditCardRepo.save(card);
    }

    @Override
    public void rejectRequest(UUID requestId, String reason) {

        CreditCardRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> BusinessException.notFound("Credit card request not found"));

        if (req.getStatus() != CardStatus.PENDING) {
            throw BusinessException.badRequest("Request is not pending");
        }

        req.setStatus(CardStatus.REJECTED);
        req.setRejectionReason(reason);
        req.setDecidedAt(LocalDateTime.now());
    }

    @Override
    public CreditCardResponse getCreditCardSummary(UUID customerId) {
        return creditCardRepo.findByCustomerId(customerId)
                .stream()
                .filter(card -> card.getStatus() == CardStatus.ACTIVE)
                .findFirst()
                .map(card -> CreditCardResponse.builder()
                        .cardNumber(maskCardNumber(card.getCardNumber()))
                        .creditLimit(card.getCreditLimit())
                        .availableCredit(card.getAvailableCredit())
                        .outstanding(card.getOutstandingAmount())
                        .status("ACTIVE")
                        .message("Your credit card is active")
                        .build()
                )
                .orElseGet(() ->
                        requestRepo.findTopByCustomerIdOrderByRequestedAtDesc(customerId)
                                .map(req -> switch (req.getStatus()) {
                                    case PENDING -> CreditCardResponse.builder()
                                            .status("PENDING_APPROVAL")
                                            .message("Credit card application under review")
                                            .build();
                                    case REJECTED -> CreditCardResponse.builder()
                                            .status("REJECTED")
                                            .message("Rejected: " +
                                                    (req.getRejectionReason() != null
                                                            ? req.getRejectionReason()
                                                            : "Not specified"))
                                            .build();
                                    default -> notApplied();
                                })
                                .orElse(notApplied())
                );
    }

    private CreditCardResponse notApplied() {
        return CreditCardResponse.builder()
                .status("NOT_APPLIED")
                .message("Apply for a credit card to enjoy benefits")
                .build();
    }

    @Override
    public List<CreditCardRequest> getPendingRequests() {
        return requestRepo.findByStatus(CardStatus.PENDING);
    }

    private String generateCardNumber() {
        return "4532" + System.currentTimeMillis();
    }

    private String maskCardNumber(String cardNumber) {
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
}