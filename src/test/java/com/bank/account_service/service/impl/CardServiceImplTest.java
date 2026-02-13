package com.bank.account_service.service.impl;
import com.bank.account_service.dto.card.DebitCardResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.DebitCard;
import com.bank.account_service.enums.CardStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;

import com.bank.account_service.repository.DebitCardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {
    @Mock
    private AccountRepository accountRepo;
    @Mock
    private DebitCardRepository debitRepo;
    @InjectMocks
    private CardServiceImpl service;

    @Test
    void getDebitCard_cardExists() {
        UUID accountId = UUID.randomUUID();

        Account account = Account.builder()
                .id(accountId)
                .accountNumber("ACC123")
                .build();

        DebitCard card = DebitCard.builder()
                .accountNumber("ACC123")
                .cardNumber("5123456789012345")
                .expiryDate(LocalDate.now().plusYears(5))
                .dailyLimit(50000)
                .status(CardStatus.ACTIVE)
                .build();

        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));

        when(debitRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.of(card));

        DebitCardResponse response = service.getDebitCard(accountId);

        assertNotNull(response);
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(50000, response.getDailyLimit());
        assertTrue(response.getCardNumber().endsWith("2345"));

        verify(debitRepo, never()).save(any());
    }
    @Test
    void getDebitCard_cardAutoCreated() {
        UUID accountId = UUID.randomUUID();

        Account account = Account.builder()
                .id(accountId)
                .accountNumber("ACC123")
                .build();

        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));

        when(debitRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.empty());

        DebitCardResponse response = service.getDebitCard(accountId);

        assertNotNull(response);
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(50000, response.getDailyLimit());

        verify(debitRepo, times(1)).save(any(DebitCard.class));
    }
    @Test
    void getDebitCard_accountNotFound() {
        UUID accountId = UUID.randomUUID();

        when(accountRepo.findById(accountId))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.getDebitCard(accountId));

        verify(debitRepo, never()).findByAccountNumber(any());
    }
    @Test
    void blockDebitCard_success() {
        UUID accountId = UUID.randomUUID();

        Account account = Account.builder()
                .id(accountId)
                .accountNumber("ACC123")
                .build();

        DebitCard card = DebitCard.builder()
                .accountNumber("ACC123")
                .status(CardStatus.ACTIVE)
                .build();

        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));

        when(debitRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.of(card));

        service.blockDebitCard(accountId);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
    }
    @Test
    void blockDebitCard_cardNotIssued() {
        UUID accountId = UUID.randomUUID();

        Account account = Account.builder()
                .id(accountId)
                .accountNumber("ACC123")
                .build();

        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));

        when(debitRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.blockDebitCard(accountId));
    }
    @Test
    void unblockDebitCard_success() {
        UUID accountId = UUID.randomUUID();

        Account account = Account.builder()
                .id(accountId)
                .accountNumber("ACC123")
                .build();

        DebitCard card = DebitCard.builder()
                .accountNumber("ACC123")
                .status(CardStatus.BLOCKED)
                .build();

        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));

        when(debitRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.of(card));

        service.unblockDebitCard(accountId);

        assertEquals(CardStatus.ACTIVE, card.getStatus());
    }

}