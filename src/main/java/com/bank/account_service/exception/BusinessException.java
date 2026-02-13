package com.bank.account_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    private BusinessException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
    public static BusinessException badRequest(String message) {
        return new BusinessException(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }
    public static BusinessException unauthorized(String message) {
        return new BusinessException(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }

    public static BusinessException invalidCredentials() {
        return unauthorized("Invalid account number or password");
    }
    public static BusinessException forbidden(String message) {
        return new BusinessException(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
    }

    public static BusinessException accountBlocked() {
        return forbidden("Account is blocked");
    }

    public static BusinessException accountClosed() {
        return forbidden("Account is closed");
    }

    public static BusinessException accountInactive() {
        return forbidden("Account is inactive");
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(message, HttpStatus.NOT_FOUND, "NOT_FOUND");
    }

    public static BusinessException accountNotFound() {
        return notFound("Account not found");
    }

    public static BusinessException loanNotFound() {
        return notFound("Loan not found");
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(message, HttpStatus.CONFLICT, "CONFLICT");
    }

    public static BusinessException insufficientBalance() {
        return conflict("Insufficient balance");
    }

}