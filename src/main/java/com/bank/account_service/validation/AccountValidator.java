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

    /* ================= LOGIN ================= */

    public static void validateLoginRequest(LoginRequest request) {

        log.info("Login validation started");

        if (request == null) {
            log.warn("Login failed: request is null");
            throw BusinessException.badRequest("Login request is required");
        }

        if (request.getAccountNumber() == null ||
                request.getAccountNumber().isBlank()) {
            log.warn("Login failed: account number missing");
            throw BusinessException.badRequest("Account number is required");
        }

        if (request.getPassword() == null ||
                request.getPassword().isBlank()) {
            log.warn("Login failed: password missing");
            throw BusinessException.badRequest("Password is required");
        }

        log.info("Login request validation passed");
    }

    public static void validateAccountStatus(Account account) {

        log.info("Validating account status for accountId={}",
                account.getId());

        if (account.getStatus() == AccountStatus.BLOCKED) {
            log.warn("Account blocked: {}", account.getId());
            throw BusinessException.accountBlocked();
        }

        if (account.getStatus() == AccountStatus.CLOSED) {
            log.warn("Account closed: {}", account.getId());
            throw BusinessException.accountClosed();
        }

        log.info("Account status validation passed");
    }

//    /* ================= DEBIT ================= */
//
//    public static void validateDebit(Account account, DebitRequest request) {
//
//        log.info("Debit validation started for accountId={}",
//                account.getId());
//
//        if (request == null) {
//            throw BusinessException.badRequest("Debit request is required");
//        }
//
//        if (request.getAmount() <= 0) {
//            log.warn("Invalid debit amount: {}", request.getAmount());
//            throw BusinessException.invalidAmount();
//        }
//
//        if (account.getBalance().doubleValue() < request.getAmount()) {
//            log.warn(
//                    "Insufficient balance: available={}, requested={}",
//                    account.getBalance(),
//                    request.getAmount()
//            );
//            throw BusinessException.insufficientBalance();
//        }
//
//        log.info("Debit validation passed for accountId={}",
//                account.getId());
//    }
//
//    /* ================= CREDIT ================= */
//
//    public static void validateCredit(Account account, CreditRequest request) {
//
//        log.info("Credit validation started for accountId={}",
//                account.getId());
//
//        if (request == null) {
//            throw BusinessException.badRequest("Credit request is required");
//        }
//
//        if (request.getAmount() <= 0) {
//            log.warn("Invalid credit amount: {}", request.getAmount());
//            throw BusinessException.invalidAmount();
//        }
//
//        log.info("Credit validation passed for accountId={}",
//                account.getId());
//    }

    /* ================= BALANCE ================= */

    public static void validateBalanceAccess(Account account) {

        log.info("Balance check validation for accountId={}",
                account.getId());

        if (account.getStatus() != AccountStatus.ACTIVE) {
            log.warn(
                    "Balance access denied for status={}",
                    account.getStatus()
            );
            throw BusinessException.forbidden(
                    "Account is not active"
            );
        }
    }

    /* ================= PASSWORD ================= */

    public static void validatePasswordChange(
            String oldPassword,
            String newPassword
    ) {

        log.info("Password change validation started");

        if (oldPassword == null || oldPassword.isBlank()) {
            throw BusinessException.badRequest(
                    "Old password is required"
            );
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw BusinessException.badRequest(
                    "New password is required"
            );
        }

        if (newPassword.length() < 8) {
            throw BusinessException.badRequest(
                    "Password must be at least 8 characters long"
            );
        }

        log.info("Password change validation passed");
    }
}
