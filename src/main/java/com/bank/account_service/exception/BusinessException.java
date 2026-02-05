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

    // 400 BAD REQUEST
    public static BusinessException badRequest(String message) {
        return new BusinessException(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }

    public static BusinessException invalidAmount() {
        return badRequest("Amount must be greater than zero");
    }

    // 401 UNAUTHORIZED
    public static BusinessException unauthorized(String message) {
        return new BusinessException(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }

    public static BusinessException invalidCredentials() {
        return unauthorized("Invalid account number or password");
    }

    // 403 FORBIDDEN
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

    public static BusinessException adminAccessRequired() {
        return forbidden("Admin access required");
    }

    // 404 NOT FOUND
    public static BusinessException notFound(String message) {
        return new BusinessException(message, HttpStatus.NOT_FOUND, "NOT_FOUND");
    }

    public static BusinessException accountNotFound() {
        return notFound("Account not found");
    }

    public static BusinessException loanNotFound() {
        return notFound("Loan not found");
    }

    public static BusinessException creditCardNotFound() {
        return notFound("Credit card not found");
    }

    // 409 CONFLICT
    public static BusinessException conflict(String message) {
        return new BusinessException(message, HttpStatus.CONFLICT, "CONFLICT");
    }

    public static BusinessException insufficientBalance() {
        return conflict("Insufficient balance");
    }

    // 503 SERVICE UNAVAILABLE
    public static BusinessException serviceUnavailable(String message) {
        return new BusinessException(message, HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE");
    }
}