package com.bank.account_service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private String message;
    private String error;
    private int status;
    private LocalDateTime timestamp;
}