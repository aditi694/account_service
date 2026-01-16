package com.bank.account_service.exception;

import com.bank.account_service.exception.ApiErrorResponse;
import com.bank.account_service.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex) {

        ApiErrorResponse response =
                new ApiErrorResponse(ex.getMessage(), ex.getStatus().value());

        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {

        ApiErrorResponse response =
                new ApiErrorResponse("Internal server error", 500);

        return ResponseEntity.status(500).body(response);
    }
}
