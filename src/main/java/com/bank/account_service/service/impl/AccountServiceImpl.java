package com.bank.account_service.service.impl;

import com.bank.account_service.dto.auth.LoginRequest;
import com.bank.account_service.dto.auth.LoginResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.security.JwtUtil;
import com.bank.account_service.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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

        log.info("Customer login attempt for account: {}", request.getAccountNumber());

        Account account = accountRepo.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(BusinessException::invalidCredentials);

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw BusinessException.accountBlocked();
        }

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw BusinessException.accountClosed();
        }

        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw BusinessException.invalidCredentials();
        }

        // ✅ Generate token with CUSTOMER role (without ROLE_ prefix - filter adds it)
        String token = jwtUtil.generate(
                account.getId(),
                account.getCustomerId(),
                "CUSTOMER"
        );

        log.info("✅ Customer login successful for account: {}", request.getAccountNumber());

        return LoginResponse.builder()
                .success(true)
                .token(token)
                .requiresPasswordChange(account.isRequiresPasswordChange())
                .build();
    }
    @Override
    public BigDecimal getBalance(String accountNumber) {

        Account account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(BusinessException::accountNotFound);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw BusinessException.accountInactive();
        }

        return account.getBalance();
    }

    @Override
    public void debit(String accountNumber, BigDecimal amount) {

        Account account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(BusinessException::accountNotFound);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw BusinessException.accountInactive();
        }

        BigDecimal currentBalance = account.getBalance();

        if (currentBalance.compareTo(amount) < 0) {
            throw BusinessException.insufficientBalance();
        }

        account.setBalance(currentBalance.subtract(amount));

        accountRepo.save(account);
    }



    @Override
    public void credit(String accountNumber, BigDecimal amount) {

        Account account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(BusinessException::accountNotFound);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw BusinessException.accountInactive();
        }

        account.setBalance(account.getBalance().add(amount));

        accountRepo.save(account);
    }
}
