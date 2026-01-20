package com.bank.account_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(
            BusinessException ex
    ) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ApiErrorResponse(
                        ex.getMessage(),
                        ex.getErrorCode().name(),
                        ex.getStatus().value(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(
                        "Internal server error",
                        "INTERNAL_SERVER_ERROR",
                        500,
                        LocalDateTime.now()
                ));
    }
}
