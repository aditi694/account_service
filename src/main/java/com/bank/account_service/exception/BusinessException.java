package com.bank.account_service.exception;

import com.bank.account_service.enums.BusinessErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final BusinessErrorCode errorCode;
    private final HttpStatus status;

    private BusinessException(
            String message,
            BusinessErrorCode errorCode,
            HttpStatus status
    ) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    /* ========== GENERIC ========== */
    public static BusinessException validationError(String msg) {
        return new BusinessException(
                msg,
                BusinessErrorCode.BAD_REQUEST,
                HttpStatus.BAD_REQUEST
        );
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(
                message,
                BusinessErrorCode.BAD_REQUEST,
                HttpStatus.BAD_REQUEST
        );
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(
                message,
                BusinessErrorCode.FORBIDDEN,
                HttpStatus.FORBIDDEN
        );
    }

    public static BusinessException unauthorized() {
        return new BusinessException(
                "Unauthorized access",
                BusinessErrorCode.UNAUTHORIZED,
                HttpStatus.UNAUTHORIZED
        );
    }

    /* ========== ACCOUNT ========== */

    public static BusinessException accountNotFound() {
        return new BusinessException(
                "Account not found",
                BusinessErrorCode.ACCOUNT_NOT_FOUND,
                HttpStatus.NOT_FOUND
        );
    }

    public static BusinessException invalidCredentials() {
        return new BusinessException(
                "Invalid account number or password",
                BusinessErrorCode.INVALID_CREDENTIALS,
                HttpStatus.UNAUTHORIZED
        );
    }

    public static BusinessException accountBlocked() {
        return new BusinessException(
                "Account is blocked",
                BusinessErrorCode.ACCOUNT_BLOCKED,
                HttpStatus.FORBIDDEN
        );
    }

    public static BusinessException accountClosed() {
        return new BusinessException(
                "Account is closed",
                BusinessErrorCode.ACCOUNT_CLOSED,
                HttpStatus.BAD_REQUEST
        );
    }

    /* ========== TRANSACTION ========== */

    public static BusinessException invalidAmount() {
        return new BusinessException(
                "Amount must be greater than zero",
                BusinessErrorCode.INVALID_AMOUNT,
                HttpStatus.BAD_REQUEST
        );
    }

    public static BusinessException insufficientBalance() {
        return new BusinessException(
                "Insufficient balance",
                BusinessErrorCode.INSUFFICIENT_BALANCE,
                HttpStatus.BAD_REQUEST
        );
    }

    public static BusinessException loanNotFound() {
        return new BusinessException(
                "Loan not found",
                BusinessErrorCode.BAD_REQUEST,
                HttpStatus.NOT_FOUND
        );
    }
    public static BusinessException loanRejected() {
        return new BusinessException(
                "Loan rejected",
                BusinessErrorCode.LOAN_REJECTED,
                HttpStatus.BAD_REQUEST
        );
    }


}
