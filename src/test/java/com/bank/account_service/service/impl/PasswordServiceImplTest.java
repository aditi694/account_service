package com.bank.account_service.service.impl;
import com.bank.account_service.dto.account.ChangePasswordRequest;
import com.bank.account_service.dto.account.ChangePasswordResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;

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
class PasswordServiceImplTest {
    @Mock
    private AccountRepository accountRepo;

    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private PasswordServiceImpl service;

    @Test
    void changePassword_success() {
        UUID accountId = UUID.randomUUID();

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");

        Account account = Account.builder()
                .id(accountId)
                .passwordHash("hashedOld")
                .requiresPasswordChange(true)
                .build();

        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));

        when(passwordEncoder.matches("oldPass", "hashedOld"))
                .thenReturn(true);

        when(passwordEncoder.encode("newPass"))
                .thenReturn("hashedNew");

        ChangePasswordResponse response =
                service.changePassword(accountId, request);

        assertTrue(response.isSuccess());
        assertEquals("Password changed successfully", response.getMessage());
        assertEquals("hashedNew", account.getPasswordHash());
        assertFalse(account.isRequiresPasswordChange());

        verify(accountRepo).findById(accountId);

    }
    @Test
    void changePassword_accountNotFound() {
        UUID accountId = UUID.randomUUID();

        when(accountRepo.findById(accountId))
                .thenReturn(Optional.empty());

        ChangePasswordRequest request = new ChangePasswordRequest();

        assertThrows(BusinessException.class,
                () -> service.changePassword(accountId, request));

        verify(passwordEncoder, never()).matches(any(), any());
        verify(passwordEncoder, never()).encode(any());
    }
    @Test
    void changePassword_oldPasswordIncorrect() {
        UUID accountId = UUID.randomUUID();

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrongOld");
        request.setNewPassword("newPass");

        Account account = Account.builder()
                .id(accountId)
                .passwordHash("hashedOld")
                .requiresPasswordChange(true)
                .build();

        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));

        when(passwordEncoder.matches("wrongOld", "hashedOld"))
                .thenReturn(false);

        assertThrows(BusinessException.class,
                () -> service.changePassword(accountId, request));
        assertEquals("hashedOld", account.getPasswordHash());
        assertTrue(account.isRequiresPasswordChange());

        verify(passwordEncoder, never()).encode(any());
    }

}