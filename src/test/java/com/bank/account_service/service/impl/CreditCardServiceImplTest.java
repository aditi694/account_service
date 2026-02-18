package com.bank.account_service.service.impl;

import com.bank.account_service.dto.card.response.CreditCardIssueResponse;
import com.bank.account_service.dto.card.response.CreditCardResponse;
import com.bank.account_service.dto.client.TransactionClient;
import com.bank.account_service.entity.*;
import com.bank.account_service.enums.CardStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.*;
import com.bank.account_service.security.AuthUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditCardServiceImplTest {

    @Mock
    private CreditCardRepository creditCardRepo;
    @Mock
    private CreditCardRequestRepository requestRepo;
    @Mock
    private TransactionClient transactionClient;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CreditCardServiceImpl service;

    private UUID customerId;
    private UUID accountId;
    private AuthUser user;
    private Account account;

    @BeforeEach
    void setUp() {

        customerId = UUID.randomUUID();
        accountId = UUID.randomUUID();

        user = AuthUser.builder()
                .customerId(customerId)
                .accountId(accountId)
                .role("ROLE_CUSTOMER")
                .build();

        account = Account.builder()
                .id(accountId)
                .accountHolderName("Aditi Goel")
                .build();
    }

    @Test
    void applyCreditCard_accountNotFound_shouldHaveCorrectErrorCode() {

        when(accountRepository.findById(any()))
                .thenReturn(Optional.empty());

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.applyCreditCard(user, "Aditi")
        );

        assertEquals("NOT_FOUND", ex.getErrorCode());
    }


    @Test
    void applyCreditCard_nameMismatch() {
        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        assertThrows(BusinessException.class,
                () -> service.applyCreditCard(user, "Wrong Name"));
    }

    @Test
    void applyCreditCard_nullName() {
        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        assertThrows(BusinessException.class,
                () -> service.applyCreditCard(user, null));
    }

    @Test
    void applyCreditCard_activeCardExists() {
        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        CreditCard active = CreditCard.builder()
                .status(CardStatus.ACTIVE)
                .build();

        when(creditCardRepo.findByCustomerId(customerId))
                .thenReturn(List.of(active));

        assertThrows(BusinessException.class,
                () -> service.applyCreditCard(user, "Aditi Goel"));
    }

    @Test
    void applyCreditCard_pendingRequestExists() {
        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        when(creditCardRepo.findByCustomerId(customerId))
                .thenReturn(Collections.emptyList());

        when(requestRepo.existsByCustomerIdAndStatus(customerId, CardStatus.PENDING))
                .thenReturn(true);

        assertThrows(BusinessException.class,
                () -> service.applyCreditCard(user, "Aditi Goel"));
    }

//    AUTO_APPROVAL_THRESHOLD = 25_000.0;
    @Test
    void applyCreditCard_autoApproved() {
        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        when(creditCardRepo.findByCustomerId(customerId))
                .thenReturn(Collections.emptyList());

        when(requestRepo.existsByCustomerIdAndStatus(customerId, CardStatus.PENDING))
                .thenReturn(false);
//above threshold
        when(transactionClient.getTotalDebit(customerId))
                .thenReturn(30000.0);

        UUID result = service.applyCreditCard(user, "Aditi Goel");

        assertNull(result);
        verify(creditCardRepo).save(any(CreditCard.class));
    }

    @Test
    void applyCreditCard_pendingCreated() {
        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        when(creditCardRepo.findByCustomerId(customerId))
                .thenReturn(Collections.emptyList());

        when(requestRepo.existsByCustomerIdAndStatus(customerId, CardStatus.PENDING))
                .thenReturn(false);
// below threshold
        when(transactionClient.getTotalDebit(customerId))
                .thenReturn(10000.0);

        CreditCardRequest saved = CreditCardRequest.builder()
                .id(UUID.randomUUID())
                .build();

        when(requestRepo.save(any()))
                .thenReturn(saved);

        UUID id = service.applyCreditCard(user, "Aditi Goel");

        assertNotNull(id);
        verify(requestRepo).save(any(CreditCardRequest.class));
    }

    @Test
    void applyCreditCard_transactionClientFails_shouldCreatePending() {
        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        when(creditCardRepo.findByCustomerId(customerId))
                .thenReturn(Collections.emptyList());

        when(requestRepo.existsByCustomerIdAndStatus(customerId, CardStatus.PENDING))
                .thenReturn(false);

        when(transactionClient.getTotalDebit(customerId))
                .thenThrow(new RuntimeException());

        CreditCardRequest saved = CreditCardRequest.builder()
                .id(UUID.randomUUID())
                .build();

        when(requestRepo.save(any()))
                .thenReturn(saved);

        UUID id = service.applyCreditCard(user, "Aditi Goel");

        assertNotNull(id);
    }

    @Test
    void applyCreditCard_whenPreviousCardRejected_allowsNewRequest() {
        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        CreditCard rejected = CreditCard.builder()
                .status(CardStatus.REJECTED)
                .build();

        when(creditCardRepo.findByCustomerId(customerId))
                .thenReturn(List.of(rejected));

        when(requestRepo.existsByCustomerIdAndStatus(customerId, CardStatus.PENDING))
                .thenReturn(false);

        when(transactionClient.getTotalDebit(customerId))
                .thenReturn(10000.0);

        CreditCardRequest saved = CreditCardRequest.builder()
                .id(UUID.randomUUID())
                .build();

        when(requestRepo.save(any()))
                .thenReturn(saved);

        UUID id = service.applyCreditCard(user, "Aditi Goel");

        assertNotNull(id);
    }

    @Test
    void approveRequest_success() {
        UUID requestId = UUID.randomUUID();

        CreditCardRequest req = CreditCardRequest.builder()
                .id(requestId)
                .customerId(customerId)
                .status(CardStatus.PENDING)
                .build();

        when(requestRepo.findById(requestId))
                .thenReturn(Optional.of(req));

        when(creditCardRepo.save(any()))
                .thenReturn(CreditCard.builder()
                        .cardNumber("4532123412341234")
                        .creditLimit(50000.0)
                        .build());

        CreditCardIssueResponse response =
                service.approveRequest(requestId);

        assertEquals("APPROVED", response.getStatus());
    }

    @Test
    void approveRequest_notFound() {
        when(requestRepo.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.approveRequest(UUID.randomUUID()));
    }

    @Test
    void approveRequest_notPending() {
        UUID requestId = UUID.randomUUID();

        CreditCardRequest req = CreditCardRequest.builder()
                .id(requestId)
                .status(CardStatus.APPROVED)
                .build();

        when(requestRepo.findById(requestId))
                .thenReturn(Optional.of(req));

        assertThrows(BusinessException.class,
                () -> service.approveRequest(requestId));
    }

    @Test
    void rejectRequest_success() {
        UUID requestId = UUID.randomUUID();
        CreditCardRequest req = CreditCardRequest.builder()
                .id(requestId)
                .status(CardStatus.PENDING)
                .build();

        when(requestRepo.findById(requestId))
                .thenReturn(Optional.of(req));

        service.rejectRequest(requestId, "Low score");

        assertEquals(CardStatus.REJECTED, req.getStatus());
    }

    @Test
    void rejectRequest_notFound() {
        when(requestRepo.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.rejectRequest(UUID.randomUUID(), "Reason"));
    }

    @Test
    void rejectRequest_notPending() {
        UUID requestId = UUID.randomUUID();
        CreditCardRequest req = CreditCardRequest.builder()
                .id(requestId)
                .status(CardStatus.APPROVED)
                .build();

        when(requestRepo.findById(requestId))
                .thenReturn(Optional.of(req));

        assertThrows(BusinessException.class,
                () -> service.rejectRequest(requestId, "Reason"));
    }

    @Test
    void getCreditCardSummary_activeCard() {
        CreditCard active = CreditCard.builder()
                .cardNumber("4532123456789999")
                .status(CardStatus.ACTIVE)
                .build();

        CreditCard rejected = CreditCard.builder()
                .status(CardStatus.REJECTED)
                .build();

        when(creditCardRepo.findByCustomerId(customerId))
                .thenReturn(List.of(rejected, active));

        CreditCardResponse response =
                service.getCreditCardSummary(customerId);

        assertEquals("ACTIVE", response.getStatus());
    }

    @Test
    void getCreditCardSummary_pending() {
        when(creditCardRepo.findByCustomerId(customerId))
                .thenReturn(Collections.emptyList());

        CreditCardRequest req = CreditCardRequest.builder()
                .status(CardStatus.PENDING)
                .build();

        when(requestRepo.findTopByCustomerIdOrderByRequestedAtDesc(customerId))
                .thenReturn(Optional.of(req));

        CreditCardResponse response =
                service.getCreditCardSummary(customerId);

        assertEquals("PENDING_APPROVAL", response.getStatus());
    }

    @Test
    void getCreditCardSummary_rejectedWithReason() {
        when(creditCardRepo.findByCustomerId(customerId))
                .thenReturn(Collections.emptyList());

        CreditCardRequest req = CreditCardRequest.builder()
                .status(CardStatus.REJECTED)
                .rejectionReason("Low credit score")
                .build();

        when(requestRepo.findTopByCustomerIdOrderByRequestedAtDesc(customerId))
                .thenReturn(Optional.of(req));

        CreditCardResponse response =
                service.getCreditCardSummary(customerId);

        assertEquals("REJECTED", response.getStatus());
        assertTrue(response.getMessage().contains("Low credit score"));
    }

    @Test
    void getCreditCardSummary_rejectedWithoutReason() {
        when(creditCardRepo.findByCustomerId(customerId))
                .thenReturn(Collections.emptyList());

        CreditCardRequest req = CreditCardRequest.builder()
                .status(CardStatus.REJECTED)
                .rejectionReason(null)
                .build();

        when(requestRepo.findTopByCustomerIdOrderByRequestedAtDesc(customerId))
                .thenReturn(Optional.of(req));

        CreditCardResponse response =
                service.getCreditCardSummary(customerId);

        assertEquals("REJECTED", response.getStatus());
        assertTrue(response.getMessage().contains("Not specified"));
    }

    @Test
    void getCreditCardSummary_notApplied() {
        when(creditCardRepo.findByCustomerId(customerId))
                .thenReturn(Collections.emptyList());

        when(requestRepo.findTopByCustomerIdOrderByRequestedAtDesc(customerId))
                .thenReturn(Optional.empty());

        CreditCardResponse response =
                service.getCreditCardSummary(customerId);

        assertEquals("NOT_APPLIED", response.getStatus());
    }

    @Test
    void getCreditCardSummary_whenRequestApprovedButNoActiveCard_returnNotApplied() {
        when(creditCardRepo.findByCustomerId(customerId))
                .thenReturn(Collections.emptyList());

        CreditCardRequest req = CreditCardRequest.builder()
                .status(CardStatus.APPROVED)
                .build();

        when(requestRepo.findTopByCustomerIdOrderByRequestedAtDesc(customerId))
                .thenReturn(Optional.of(req));

        CreditCardResponse response =
                service.getCreditCardSummary(customerId);

        assertEquals("NOT_APPLIED", response.getStatus());
    }

    @Test
    void getPendingRequests_success() {
        when(requestRepo.findByStatus(CardStatus.PENDING))
                .thenReturn(List.of(new CreditCardRequest()));

        List<CreditCardRequest> result =
                service.getPendingRequests();

        assertEquals(1, result.size());
    }
}
