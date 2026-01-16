package com.bank.account_service.service.impl;

import com.bank.account_service.dto.client.CustomerClient;
import com.bank.account_service.dto.client.CustomerSummaryDto;
import com.bank.account_service.dto.request.AccountCreateRequest;
import com.bank.account_service.dto.response.AccountResponse;
import com.bank.account_service.dto.response.BalanceUpdateResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.AccountAudit;
import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountAuditRepository;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.AccountService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repo;
    private final AccountAuditRepository auditRepo;

    private final CustomerClient customerClient;

    public AccountServiceImpl(AccountRepository repo,
                              AccountAuditRepository auditRepo,
                              CustomerClient customerClient) {
        this.repo = repo;
        this.auditRepo = auditRepo;
        this.customerClient = customerClient;
    }

    // ---------------- AUTH ----------------

    private AuthUser getAuthUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof AuthUser)) {
            throw BusinessException.unauthorized();
        }
        return (AuthUser) auth.getPrincipal();
    }

    // ---------------- FIND ----------------

    private Account findAccount(UUID accountId) {
        Account acc = repo.findById(accountId)
                .orElseThrow(BusinessException::accountNotFound);

        AuthUser user = getAuthUser();

        if (user.isCustomer() && !acc.getCustomerId().equals(user.getCustomerId())) {
            throw BusinessException.forbidden();
        }
        return acc;
    }

    // ---------------- READ ----------------

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public AccountResponse getById(UUID id) {
        return AccountResponse.from(findAccount(id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<AccountResponse> getAll() {

        AuthUser user = getAuthUser();
        if (!user.isAdmin()) {
            throw BusinessException.forbidden();
        }

        return repo.findAll()
                .stream()
                .map(AccountResponse::from)
                .toList();
    }

    // ---------------- CREATE ----------------

    @Override
    public AccountResponse create(AccountCreateRequest req) {

        AuthUser user = getAuthUser();
        if (!user.isAdmin()) {
            throw BusinessException.forbidden();
        }

        // 🔗 Call Customer Service
        CustomerSummaryDto customer =
                customerClient.getCustomer(req.getCustomerId());

        // 🔒 CUSTOMER STATUS CHECK
        if (!"ACTIVE".equals(customer.getStatus())) {
            throw BusinessException.customerBlocked();
        }

        // 🔒 KYC CHECK (MANDATORY)
        if (!"VERIFIED".equals(customer.getKycStatus())) {
            throw BusinessException.kycNotCompleted();
        }

        Account acc = Account.builder()
                .customerId(customer.getId())
                .customerName(customer.getFullName())
                .accountNumber(UUID.randomUUID().toString())
                .accountType(req.getAccountType())
                .currency(req.getCurrency())
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .openedAt(LocalDateTime.now())
                .build();

        return AccountResponse.from(repo.save(acc));
    }


    // ---------------- DEBIT ----------------

    @Override
    public BalanceUpdateResponse debit(UUID accountId, BigDecimal amount) {

        AuthUser user = getAuthUser();
        if (!user.isCustomer()) {
            throw BusinessException.forbidden();
        }

        Account acc = findAccount(accountId);

        if (acc.getStatus() == AccountStatus.BLOCKED) {
            throw BusinessException.accountBlocked();
        }
        if (acc.getStatus() == AccountStatus.CLOSED) {
            throw BusinessException.accountClosed();
        }

        BigDecimal old = acc.getBalance();
        acc.debit(amount);

        audit(acc, "DEBIT", old, acc.getBalance(), user);

        return BalanceUpdateResponse.success(
                "Amount debited successfully",
                acc.getId(),
                old,
                acc.getBalance(),
                acc.getCurrency()
        );
    }

    // ---------------- AUDIT ----------------

    private void audit(Account acc,
                       String action,
                       BigDecimal oldBalance,
                       BigDecimal newBalance,
                       AuthUser user) {

        auditRepo.save(AccountAudit.builder()
                .accountId(acc.getId())
                .action(action)
                .oldBalance(oldBalance)
                .newBalance(newBalance)
                .performedBy(user.getUsername())
                .performedByRole(user.getRole())
                .timestamp(LocalDateTime.now())
                .build());
    }
}
