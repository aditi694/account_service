package com.bank.account_service.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    private BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    // ---------- FACTORY METHODS ----------

    public static BusinessException accountNotFound() {
        return new BusinessException(
                "Account not found",
                HttpStatus.NOT_FOUND
        );
    }

    public static BusinessException insufficientBalance() {
        return new BusinessException(
                "Insufficient balance",
                HttpStatus.BAD_REQUEST
        );
    }

    public static BusinessException accountFrozen() {
        return new BusinessException(
                "Account is frozen",
                HttpStatus.BAD_REQUEST
        );
    }

    public static BusinessException invalidAmount() {
        return new BusinessException(
                "Amount must be greater than zero",
                HttpStatus.BAD_REQUEST
        );
    }
}
