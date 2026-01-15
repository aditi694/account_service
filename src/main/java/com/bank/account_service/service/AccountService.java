package com.bank.account_service.service;

import com.bank.account_service.dto.request.AccountCreateRequest;
import com.bank.account_service.dto.response.AccountResponse;
import com.bank.account_service.dto.response.BalanceUpdateResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountService {

    AccountResponse create(AccountCreateRequest request);

    AccountResponse getById(UUID id);

    BalanceUpdateResponse credit(UUID id, BigDecimal amount);

    BalanceUpdateResponse debit(UUID id, BigDecimal amount);
}
