package com.bank.account_service.service.impl;

import com.bank.account_service.dto.card.CreditCardApplyRequest;
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

    private static final double AUTO_APPROVAL_THRESHOLD = 25000.0;
    private static final double DEFAULT_CREDIT_LIMIT = 50000.0;

    @Override
    public UUID applyCreditCard(UUID customerId, String cardHolderName) {

        log.info("Credit card application started for customer: {}", customerId);

        // Check if customer already has active credit card
        boolean hasActiveCard = creditCardRepo
                .findByCustomerId(customerId)
                .stream()
                .anyMatch(card -> card.getStatus() == CardStatus.ACTIVE);

        if (hasActiveCard) {
            throw BusinessException.conflict("You already have an active credit card");
        }

        // Check if there's pending request
        boolean hasPendingRequest = requestRepo
                .existsByCustomerIdAndStatus(customerId, CardStatus.PENDING);

        if (hasPendingRequest) {
            throw BusinessException.conflict("You already have a pending credit card request");
        }

        // Fetch total transaction amount
        double totalDebit = fetchTotalTransactions(customerId);

        log.info("Customer {} transaction history: ₹{}", customerId, totalDebit);

        // Auto-approve if transaction > 25000
        if (totalDebit >= AUTO_APPROVAL_THRESHOLD) {
            log.info("Auto-approving credit card for customer: {}", customerId);
            issueCard(customerId, cardHolderName);
            return null; // No request created
        }

        // Create request for admin approval
        CreditCardRequest request = CreditCardRequest.builder()
                .customerId(customerId)
                .cardHolderName(cardHolderName)
                .status(CardStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        CreditCardRequest saved = requestRepo.save(request);

        log.info("Credit card request created: {} - requires admin approval", saved.getId());

        return saved.getId();
    }

    @Override
    public CreditCardIssueResponse approveRequest(UUID requestId) {

        log.info("Admin approving credit card request: {}", requestId);

        CreditCardRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> BusinessException.notFound("Credit card request not found"));

        if (request.getStatus() != CardStatus.PENDING) {
            throw BusinessException.badRequest("Request is not in pending status");
        }

        // Issue credit card
        CreditCard card = issueCard(request.getCustomerId(), request.getCardHolderName());

        // Update request status
        request.setStatus(CardStatus.APPROVED);
        request.setApprovedLimit(card.getCreditLimit());
        request.setDecidedAt(LocalDateTime.now());

        log.info("Credit card issued successfully for request: {}", requestId);

        return CreditCardIssueResponse.builder()
                .cardNumber(maskCardNumber(card.getCardNumber()))
                .creditLimit(card.getCreditLimit())
                .status("APPROVED")
                .message("Credit card approved and issued successfully")
                .build();
    }

    @Override
    public void rejectRequest(UUID requestId, String reason) {

        log.info("Admin rejecting credit card request: {}", requestId);

        CreditCardRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> BusinessException.notFound("Credit card request not found"));

        if (request.getStatus() != CardStatus.PENDING) {
            throw BusinessException.badRequest("Request is not in pending status");
        }

        request.setStatus(CardStatus.REJECTED);
        request.setRejectionReason(reason);
        request.setDecidedAt(LocalDateTime.now());

        log.info("Credit card request rejected: {}", requestId);
    }

    @Override
    public List<CreditCardResponse> getCards(UUID customerId) {
        return creditCardRepo.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CreditCardResponse getCardStatus(UUID customerId) {

        // Check active card
        return creditCardRepo.findByCustomerId(customerId)
                .stream()
                .filter(card -> card.getStatus() == CardStatus.ACTIVE)
                .findFirst()
                .map(this::mapToResponse)
                .orElseGet(() -> {
                    // Check latest request
                    return requestRepo.findTopByCustomerIdOrderByRequestedAtDesc(customerId)
                            .map(req -> CreditCardResponse.builder()
                                    .cardNumber("Not Issued")
                                    .creditLimit(0)
                                    .availableCredit(0)
                                    .outstanding(0)
                                    .status(buildRequestStatus(req))
                                    .build()
                            )
                            .orElse(CreditCardResponse.builder()
                                    .cardNumber("Not Issued")
                                    .creditLimit(0)
                                    .availableCredit(0)
                                    .outstanding(0)
                                    .status("NOT_APPLIED")
                                    .build()
                            );
                });
    }

    @Override
    public List<CreditCardRequest> getPendingRequests() {
        return requestRepo.findByStatus(CardStatus.PENDING);
    }

    // Helper methods

    private CreditCard issueCard(UUID customerId, String cardHolderName) {

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

    private double fetchTotalTransactions(UUID customerId) {
        try {
            return transactionClient.getTotalDebit(customerId);
        } catch (Exception e) {
            log.warn("Failed to fetch transactions for customer: {}", customerId);
            return 0.0;
        }
    }

    private String generateCardNumber() {
        return "4532" + System.currentTimeMillis();
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }

    private CreditCardResponse mapToResponse(CreditCard card) {
        return CreditCardResponse.builder()
                .cardNumber(maskCardNumber(card.getCardNumber()))
                .creditLimit(card.getCreditLimit())
                .availableCredit(card.getAvailableCredit())
                .outstanding(card.getOutstandingAmount())
                .status(card.getStatus().name())
                .build();
    }

    private String buildRequestStatus(CreditCardRequest req) {
        return switch (req.getStatus()) {
            case PENDING -> "PENDING_APPROVAL";
            case REJECTED -> "REJECTED: " + req.getRejectionReason();
            default -> req.getStatus().name();
        };
    }
}