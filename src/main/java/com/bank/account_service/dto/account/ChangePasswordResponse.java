package com.bank.account_service.dto.account;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePasswordResponse {
    private boolean success;
    private String message;
}