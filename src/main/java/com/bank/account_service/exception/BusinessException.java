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
        return new BusinessException(
                "Amount must be greater than zero",
                HttpStatus.BAD_REQUEST,
                "INVALID_AMOUNT"
        );
    }

    public static BusinessException invalidAccountType() {
        return new BusinessException(
                "Invalid account type. Allowed values: SAVINGS, CURRENT, FIXED_DEPOSIT, RECURRING_DEPOSIT",
                HttpStatus.BAD_REQUEST,
                "INVALID_ACCOUNT_TYPE"
        );
    }

    public static BusinessException invalidLoanType() {
        return new BusinessException(
                "Invalid loan type. Allowed values: PERSONAL, HOME, CAR, EDUCATION, BUSINESS",
                HttpStatus.BAD_REQUEST,
                "INVALID_LOAN_TYPE"
        );
    }

    public static BusinessException invalidInsuranceType() {
        return new BusinessException(
                "Invalid insurance type. Allowed values: LIFE, HEALTH, LOAN, VEHICLE",
                HttpStatus.BAD_REQUEST,
                "INVALID_INSURANCE_TYPE"
        );
    }

    public static BusinessException invalidPassword() {
        return new BusinessException(
                "Password must be at least 8 characters long",
                HttpStatus.BAD_REQUEST,
                "INVALID_PASSWORD"
        );
    }

    public static BusinessException incorrectOldPassword() {
        return new BusinessException(
                "The old password you entered is incorrect",
                HttpStatus.BAD_REQUEST,
                "INCORRECT_OLD_PASSWORD"
        );
    }

    public static BusinessException samePassword() {
        return new BusinessException(
                "New password cannot be same as old password",
                HttpStatus.BAD_REQUEST,
                "SAME_PASSWORD"
        );
    }

    // 401 UNAUTHORIZED
    public static BusinessException unauthorized() {
        return new BusinessException(
                "Invalid credentials provided",
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED"
        );
    }

    public static BusinessException invalidCredentials() {
        return new BusinessException(
                "Invalid account number or password",
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS"
        );
    }

    public static BusinessException tokenExpired() {
        return new BusinessException(
                "Your session has expired. Please login again",
                HttpStatus.UNAUTHORIZED,
                "TOKEN_EXPIRED"
        );
    }

    // 403 FORBIDDEN
    public static BusinessException forbidden(String message) {
        return new BusinessException(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
    }

    public static BusinessException accountBlocked() {
        return new BusinessException(
                "Your account has been blocked. Please contact customer support",
                HttpStatus.FORBIDDEN,
                "ACCOUNT_BLOCKED"
        );
    }

    public static BusinessException accountClosed() {
        return new BusinessException(
                "This account has been permanently closed",
                HttpStatus.FORBIDDEN,
                "ACCOUNT_CLOSED"
        );
    }

    public static BusinessException accountInactive() {
        return new BusinessException(
                "Account is not active. Please activate your account",
                HttpStatus.FORBIDDEN,
                "ACCOUNT_INACTIVE"
        );
    }

    public static BusinessException accountDormant() {
        return new BusinessException(
                "Account is dormant due to inactivity. Please contact support to reactivate",
                HttpStatus.FORBIDDEN,
                "ACCOUNT_DORMANT"
        );
    }

    public static BusinessException adminAccessRequired() {
        return new BusinessException(
                "This action requires administrator privileges",
                HttpStatus.FORBIDDEN,
                "ADMIN_ACCESS_REQUIRED"
        );
    }

    // 404 NOT FOUND
    public static BusinessException notFound(String message) {
        return new BusinessException(message, HttpStatus.NOT_FOUND, "NOT_FOUND");
    }

    public static BusinessException accountNotFound() {
        return new BusinessException(
                "Account not found with the provided details",
                HttpStatus.NOT_FOUND,
                "ACCOUNT_NOT_FOUND"
        );
    }

    public static BusinessException loanNotFound() {
        return new BusinessException(
                "Loan not found with the provided ID",
                HttpStatus.NOT_FOUND,
                "LOAN_NOT_FOUND"
        );
    }

    public static BusinessException insuranceNotFound() {
        return new BusinessException(
                "Insurance policy not found",
                HttpStatus.NOT_FOUND,
                "INSURANCE_NOT_FOUND"
        );
    }

    public static BusinessException creditCardNotFound() {
        return new BusinessException(
                "Credit card not found",
                HttpStatus.NOT_FOUND,
                "CREDIT_CARD_NOT_FOUND"
        );
    }

    public static BusinessException debitCardNotFound() {
        return new BusinessException(
                "Debit card not found for this account",
                HttpStatus.NOT_FOUND,
                "DEBIT_CARD_NOT_FOUND"
        );
    }

    // 409 CONFLICT
    public static BusinessException conflict(String message) {
        return new BusinessException(message, HttpStatus.CONFLICT, "CONFLICT");
    }

    public static BusinessException insufficientBalance() {
        return new BusinessException(
                "Insufficient balance in your account",
                HttpStatus.CONFLICT,
                "INSUFFICIENT_BALANCE"
        );
    }

    public static BusinessException loanAlreadyActive() {
        return new BusinessException(
                "You already have an active loan of this type",
                HttpStatus.CONFLICT,
                "LOAN_ALREADY_ACTIVE"
        );
    }

    public static BusinessException loanNotPending() {
        return new BusinessException(
                "Loan is not in pending status",
                HttpStatus.CONFLICT,
                "LOAN_NOT_PENDING"
        );
    }

    public static BusinessException creditCardExists() {
        return new BusinessException(
                "You already have an active credit card",
                HttpStatus.CONFLICT,
                "CREDIT_CARD_EXISTS"
        );
    }

    public static BusinessException creditCardRequestPending() {
        return new BusinessException(
                "You already have a pending credit card application",
                HttpStatus.CONFLICT,
                "CREDIT_CARD_REQUEST_PENDING"
        );
    }

    public static BusinessException debitCardAlreadyIssued() {
        return new BusinessException(
                "Debit card already issued for this account",
                HttpStatus.CONFLICT,
                "DEBIT_CARD_ALREADY_ISSUED"
        );
    }

    public static BusinessException cardAlreadyBlocked() {
        return new BusinessException(
                "Card is already blocked",
                HttpStatus.CONFLICT,
                "CARD_ALREADY_BLOCKED"
        );
    }

    public static BusinessException cardNotBlocked() {
        return new BusinessException(
                "Card is not currently blocked",
                HttpStatus.CONFLICT,
                "CARD_NOT_BLOCKED"
        );
    }

    // 422 UNPROCESSABLE ENTITY
    public static BusinessException unprocessable(String message) {
        return new BusinessException(message, HttpStatus.UNPROCESSABLE_ENTITY, "UNPROCESSABLE_ENTITY");
    }

    public static BusinessException loanRejected() {
        return new BusinessException(
                "Your loan application has been rejected based on credit assessment",
                HttpStatus.UNPROCESSABLE_ENTITY,
                "LOAN_REJECTED"
        );
    }

    public static BusinessException insuranceRejected() {
        return new BusinessException(
                "Your insurance application has been rejected",
                HttpStatus.UNPROCESSABLE_ENTITY,
                "INSURANCE_REJECTED"
        );
    }

    public static BusinessException creditScoreLow() {
        return new BusinessException(
                "Your credit score is too low for this product",
                HttpStatus.UNPROCESSABLE_ENTITY,
                "CREDIT_SCORE_LOW"
        );
    }

    // 503 SERVICE UNAVAILABLE
    public static BusinessException serviceUnavailable(String message) {
        return new BusinessException(
                message,
                HttpStatus.SERVICE_UNAVAILABLE,
                "SERVICE_UNAVAILABLE"
        );
    }

    public static BusinessException externalServiceError(String serviceName) {
        return new BusinessException(
                String.format("%s is temporarily unavailable. Please try again later", serviceName),
                HttpStatus.SERVICE_UNAVAILABLE,
                "EXTERNAL_SERVICE_ERROR"
        );
    }
}