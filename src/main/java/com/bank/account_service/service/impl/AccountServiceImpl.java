package com.bank.account_service.service.impl;

import com.bank.account_service.dto.request.AccountCreateRequest;
import com.bank.account_service.dto.response.AccountResponse;
import com.bank.account_service.dto.response.BalanceUpdateResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.AccountAudit;
import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountAuditRepository;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.service.AccountService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repo;
    private final AccountAuditRepository auditRepo;

    public AccountServiceImpl(AccountRepository repo,
                              AccountAuditRepository auditRepo) {
        this.repo = repo;
        this.auditRepo = auditRepo;
    }

    @Override
    public AccountResponse create(AccountCreateRequest request) {

        Account account = Account.builder()
                .customerId(request.getCustomerId())
                .accountNumber(UUID.randomUUID().toString())
                .balance(BigDecimal.ZERO)
                .currency(request.getCurrency())
                .status(AccountStatus.ACTIVE)
                .build();

        return map(repo.save(account));
    }

    @Override
    public AccountResponse getById(UUID id) {
        return map(find(id));
    }

    @Override
    public BalanceUpdateResponse credit(UUID id, BigDecimal amount) {

        Account account = find(id);

        BigDecimal oldBalance = account.getBalance();

        account.credit(amount); // domain-level validation

        audit(
                id,
                "CREDIT",
                oldBalance,
                account.getBalance()
        );

        return BalanceUpdateResponse.builder()
                .message("Amount credited successfully")
                .accountId(id)
                .oldBalance(oldBalance)
                .newBalance(account.getBalance())
                .currency(account.getCurrency())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public BalanceUpdateResponse debit(UUID id, BigDecimal amount) {

        Account account = find(id);

        BigDecimal oldBalance = account.getBalance();

        account.debit(amount); // domain-level validation

        audit(
                id,
                "DEBIT",
                oldBalance,
                account.getBalance()
        );

        return BalanceUpdateResponse.builder()
                .message("Amount debited successfully")
                .accountId(id)
                .oldBalance(oldBalance)
                .newBalance(account.getBalance())
                .currency(account.getCurrency())
                .timestamp(LocalDateTime.now())
                .build();
    }


    private Account find(UUID id) {
        return repo.findById(id)
                .orElseThrow(BusinessException::accountNotFound);
    }

    private void audit(UUID id, String action,
                       BigDecimal oldB, BigDecimal newB) {

        auditRepo.save(AccountAudit.builder()
                .accountId(id)
                .action(action)
                .oldBalance(oldB)
                .newBalance(newB)
                .performedBy("SYSTEM")
                .timestamp(LocalDateTime.now())
                .build());
    }

    private AccountResponse map(Account a) {
        return AccountResponse.builder()
                .id(a.getId())
                .customerId(a.getCustomerId())
                .accountNumber(a.getAccountNumber())
                .balance(a.getBalance())
                .status(a.getStatus())
                .build();
    }
}
