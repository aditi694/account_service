package com.bank.account_service.service.impl;

import com.bank.account_service.dto.auth.LoginRequest;
import com.bank.account_service.dto.auth.response.LoginResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.security.JwtUtil;
import com.bank.account_service.service.AccountService;
import com.bank.account_service.validation.AccountValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    public AccountServiceImpl(
            AccountRepository accountRepo,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.accountRepo = accountRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    @Override
    public LoginResponse login(LoginRequest request) {

        AccountValidator.validateLoginRequest(request);

        Account account = accountRepo.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(BusinessException::invalidCredentials);

        AccountValidator.validateAccountStatus(account);

        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw BusinessException.invalidCredentials();
        }

        String token = jwtUtil.generate(
                account.getId(),
                account.getCustomerId(),
                "CUSTOMER"
        );

        return LoginResponse.builder()
                .success(true)
                .token(token)
                .requiresPasswordChange(account.isRequiresPasswordChange())
                .build();
    }

}
