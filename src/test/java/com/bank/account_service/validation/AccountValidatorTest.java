package com.bank.account_service.validation;

import com.bank.account_service.dto.auth.LoginRequest;
import com.bank.account_service.entity.Account;
import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountValidatorTest {
    @Test
    void validateLoginRequest_valid() {
        LoginRequest request = new LoginRequest();
        request.setAccountNumber("ACC123");
        request.setPassword("pass");

        assertDoesNotThrow(() ->
                AccountValidator.validateLoginRequest(request)
        );
    }
    @Test
    void validateLoginRequest_nullRequest() {
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> AccountValidator.validateLoginRequest(null)
        );

        assertEquals("Login request is required", ex.getMessage());
    }
    @Test
    void validateLoginRequest_accountNumberNull() {
        LoginRequest request = new LoginRequest();
        request.setPassword("pass");

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> AccountValidator.validateLoginRequest(request)
        );

        assertEquals("Account number is required", ex.getMessage());
    }
    @Test
    void validateLoginRequest_accountNumberBlank() {
        LoginRequest request = new LoginRequest();
        request.setAccountNumber(" ");
        request.setPassword("pass");

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> AccountValidator.validateLoginRequest(request)
        );

        assertEquals("Account number is required", ex.getMessage());
    }
    @Test
    void validateLoginRequest_passwordNull() {
        LoginRequest request = new LoginRequest();
        request.setAccountNumber("ACC123");

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> AccountValidator.validateLoginRequest(request)
        );

        assertEquals("Password is required", ex.getMessage());
    }
    @Test
    void validateLoginRequest_passwordBlank() {
        LoginRequest request = new LoginRequest();
        request.setAccountNumber("ACC123");
        request.setPassword("");

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> AccountValidator.validateLoginRequest(request)
        );

        assertEquals("Password is required", ex.getMessage());
    }

    @Test
    void validateAccountStatus_active() {
        Account account = Account.builder()
                .status(AccountStatus.ACTIVE)
                .build();

        assertDoesNotThrow(() ->
                AccountValidator.validateAccountStatus(account)
        );
    }
    @Test
    void validateAccountStatus_blocked() {
        Account account = Account.builder()
                .status(AccountStatus.BLOCKED)
                .build();

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> AccountValidator.validateAccountStatus(account)
        );

        assertEquals("Account is blocked", ex.getMessage());
    }
    @Test
    void validateAccountStatus_closed() {
        Account account = Account.builder()
                .status(AccountStatus.CLOSED)
                .build();

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> AccountValidator.validateAccountStatus(account)
        );

        assertEquals("Account is closed", ex.getMessage());
    }
}
