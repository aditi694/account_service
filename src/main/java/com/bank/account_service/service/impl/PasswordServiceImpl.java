package com.bank.account_service.service.impl;

import com.bank.account_service.dto.account.ChangePasswordRequest;
import com.bank.account_service.dto.account.ChangePasswordResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ChangePasswordResponse changePassword(UUID accountId, ChangePasswordRequest request) {

        Account account = accountRepo.findById(accountId)
                .orElseThrow(BusinessException::accountNotFound);

        if (!passwordEncoder.matches(request.getOldPassword(), account.getPasswordHash())) {
            throw BusinessException.badRequest("Old password is incorrect");
        }

        account.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        account.setRequiresPasswordChange(false);

        return ChangePasswordResponse.builder()
                .success(true)
                .message("Password changed successfully")
                .build();
    }
}