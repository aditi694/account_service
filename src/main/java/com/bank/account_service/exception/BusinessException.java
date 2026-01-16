package com.bank.account_service.exception;

import com.bank.account_service.enums.BusinessErrorCode;
import org.springframework.http.HttpStatus;
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final BusinessErrorCode code;

    private BusinessException(String message, HttpStatus status, BusinessErrorCode code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() { return status; }
    public BusinessErrorCode getCode() { return code; }

    // ---------- FACTORIES ----------

    public static BusinessException accountNotFound() {
        return new BusinessException("Account not found", HttpStatus.NOT_FOUND,
                BusinessErrorCode.ACCOUNT_NOT_FOUND);
    }
    public static BusinessException unauthorized() {
        return new BusinessException(
                "Authentication token is required",
                HttpStatus.UNAUTHORIZED,
                BusinessErrorCode.UNAUTHORIZED
        );
    }

    public static BusinessException accountNotActive() {
        return new BusinessException(
                "Account is not active",
                HttpStatus.CONFLICT,
                BusinessErrorCode.ACCOUNT_CLOSED
        );
    }

    public static BusinessException forbidden() {
        return new BusinessException("You are not allowed to perform this action",
                HttpStatus.FORBIDDEN, BusinessErrorCode.FORBIDDEN);
    }

    public static BusinessException invalidAmount() {
        return new BusinessException("Amount must be greater than zero",
                HttpStatus.BAD_REQUEST, BusinessErrorCode.INVALID_AMOUNT);
    }

    public static BusinessException insufficientBalance() {
        return new BusinessException("Insufficient balance",
                HttpStatus.BAD_REQUEST, BusinessErrorCode.INSUFFICIENT_BALANCE);
    }

    public static BusinessException accountBlocked() {
        return new BusinessException("Account is blocked",
                HttpStatus.CONFLICT, BusinessErrorCode.ACCOUNT_BLOCKED);
    }

    public static BusinessException accountClosed() {
        return new BusinessException("Account is closed",
                HttpStatus.CONFLICT, BusinessErrorCode.ACCOUNT_CLOSED);
    }

    public static BusinessException customerNotFound() {
        return new BusinessException(
                "Customer not found",
                HttpStatus.NOT_FOUND,
                BusinessErrorCode.CUSTOMER_NOT_FOUND
        );
    }

    public static BusinessException kycNotCompleted() {
        return new BusinessException(
                "KYC not completed",
                HttpStatus.CONFLICT,
                BusinessErrorCode.KYC_NOT_COMPLETED
        );
    }

    public static BusinessException customerBlocked() {
        return new BusinessException(
                "Customer is blocked",
                HttpStatus.CONFLICT,
                BusinessErrorCode.CUSTOMER_BLOCKED
        );
    }

}
