package com.bank.account_service.service.impl;

import com.bank.account_service.dto.account.response.BalanceResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {
    @Mock
    private AccountRepository accountRepo;
    @InjectMocks
    private  BalanceServiceImpl service;
    @Mock
    private JwtUtil jwtUtil;

    @Test
    void getBalance_success(){
        UUID accountId = UUID.randomUUID();
        Account account = Account.builder()
                .id(accountId)
                .balance(new BigDecimal("5000"))
                .build();

        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));

        BalanceResponse response = service.getBalance(accountId);

        assertNotNull(response);
        assertEquals("Balance fetched successfully", response.getMessage());
        assertEquals(accountId, response.getAccountId());
        assertEquals(new BigDecimal("5000"), response.getOldBalance());
        assertEquals(new BigDecimal("5000"), response.getNewBalance());
        assertEquals("INR", response.getCurrency());

        verify(accountRepo).findById(accountId);
    }
    @Test
    void getBalance_accountNotFound() {

        UUID accountId = UUID.randomUUID();

        when(accountRepo.findById(accountId))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.getBalance(accountId));

        verify(accountRepo).findById(accountId);
    }
}