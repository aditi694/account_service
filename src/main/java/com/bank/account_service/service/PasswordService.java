package com.bank.account_service.service;

import com.bank.account_service.dto.account.request.ChangePasswordRequest;
import com.bank.account_service.dto.account.response.ChangePasswordResponse;

import java.util.UUID;

public interface PasswordService {
    ChangePasswordResponse changePassword(UUID accountId, ChangePasswordRequest request);
}