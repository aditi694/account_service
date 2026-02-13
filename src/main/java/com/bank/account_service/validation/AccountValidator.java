package com.bank.account_service.validation;

import com.bank.account_service.dto.auth.LoginRequest;
import com.bank.account_service.entity.Account;
import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.exception.BusinessException;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

public final class AccountValidator {

     private static final Logger log =
             LoggerFactory.getLogger(AccountValidator.class);

    private AccountValidator() {}

    public static void validateLoginRequest(LoginRequest request) {

        // log.info("Login validation started");

        if (request == null) {
            // log.warn("Login failed: request is null");
            throw BusinessException.badRequest("Login request is required");
        }

        if (request.getAccountNumber() == null ||
                request.getAccountNumber().isBlank()) {
            // log.warn("Login failed: account number missing");
            throw BusinessException.badRequest("Account number is required");
        }

        if (request.getPassword() == null ||
                request.getPassword().isBlank()) {
            // log.warn("Login failed: password missing");
            throw BusinessException.badRequest("Password is required");
        }

        // log.info("Login request validation passed");
    }

    public static void validateAccountStatus(Account account) {

        // log.info("Validating account status for accountId={}",
        //         account.getId());

        if (account.getStatus() == AccountStatus.BLOCKED) {
            // log.warn("Account blocked: {}", account.getId());
            throw BusinessException.accountBlocked();
        }

        if (account.getStatus() == AccountStatus.CLOSED) {
            // log.warn("Account closed: {}", account.getId());
            throw BusinessException.accountClosed();
        }

        // log.info("Account status validation passed");
    }

}
