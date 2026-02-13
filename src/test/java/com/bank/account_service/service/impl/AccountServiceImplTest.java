package com.bank.account_service.service.impl;

import com.bank.account_service.dto.auth.LoginRequest;
import com.bank.account_service.dto.auth.LoginResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AccountServiceImpl service;

    @Test
    void login_success() {

        LoginRequest request = new LoginRequest();
        request.setAccountNumber("12345");
        request.setPassword("password");

        Account account = Account.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .passwordHash("hashed")
                .status(AccountStatus.ACTIVE)
                .requiresPasswordChange(false)
                .build();

        when(accountRepo.findByAccountNumber("12345"))
                .thenReturn(Optional.of(account));

        when(passwordEncoder.matches("password", "hashed"))
                .thenReturn(true);

        when(jwtUtil.generate(any(), any(), any()))
                .thenReturn("jwt-token");

        LoginResponse response = service.login(request);

        assertTrue(response.isSuccess());
        assertEquals("jwt-token", response.getToken());
        assertFalse(response.isRequiresPasswordChange());

        verify(accountRepo).findByAccountNumber("12345");
        verify(passwordEncoder).matches("password", "hashed");
        verify(jwtUtil).generate(any(), any(), any());
    }
    @Test
    void login_invalidPassword_ThrowException() {

        LoginRequest request = new LoginRequest();
        request.setAccountNumber("12345");
        request.setPassword("wrongPassword");

        Account account = Account.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .passwordHash("hashed")
                .status(AccountStatus.ACTIVE)
                .requiresPasswordChange(false)
                .build();

        when(accountRepo.findByAccountNumber("12345"))
                .thenReturn(Optional.of(account));

        when(passwordEncoder.matches("wrongPassword", "hashed"))
                .thenReturn(false);

        assertThrows(BusinessException.class,
                () -> service.login(request));

        verify(passwordEncoder)
                .matches("wrongPassword", "hashed");

        verify(jwtUtil, never()).generate(any(), any(), any());
    }
    @Test
    void login_accountNotFound_ThrowException() {

        LoginRequest request = new LoginRequest();
        request.setAccountNumber("12345");
        request.setPassword("password");

        when(accountRepo.findByAccountNumber("12345"))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.login(request));

        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtUtil, never()).generate(any(), any(), any());
    }
}
