package com.bank.account_service.service.impl;

import com.bank.account_service.dto.account.BalanceResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final AccountRepository accountRepo;

    @Override
    public BalanceResponse getBalance(UUID accountId) {

        Account account = accountRepo.findById(accountId)
                .orElseThrow(BusinessException::accountNotFound);

        return BalanceResponse.success(
                "Balance fetched successfully",
                account.getId(),
                account.getBalance(),
                account.getBalance(),
                "INR"
        );
    }
}