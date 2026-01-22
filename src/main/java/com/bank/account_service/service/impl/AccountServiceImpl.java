package com.bank.account_service.service.impl;

import com.bank.account_service.dto.auth.LoginRequest;
import com.bank.account_service.dto.auth.LoginResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.security.JwtUtil;
import com.bank.account_service.service.AccountService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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

        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Account Number: " + request.getAccountNumber());

        // 1. Find account
        Account account = accountRepo.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(BusinessException::invalidCredentials);

        // 2. Status checks
        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw BusinessException.accountBlocked();
        }

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw BusinessException.accountClosed();
        }


        // 3. Verify password
        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw BusinessException.invalidCredentials();
        }

        // 4. Generate token
        String token = jwtUtil.generate(
                account.getId(),
                account.getCustomerId(),
                "ROLE_CUSTOMER"
        );

        System.out.println("Login successful, token generated");

        // 5. Return ONLY token
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

    /* ================= CREDIT ================= */

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
