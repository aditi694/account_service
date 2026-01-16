package com.bank.account_service.service;

import com.bank.account_service.dto.request.AccountCreateRequest;
import com.bank.account_service.dto.response.AccountResponse;
import com.bank.account_service.dto.response.BalanceUpdateResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountService {

    AccountResponse create(AccountCreateRequest request);

    AccountResponse getById(UUID id);

    List<AccountResponse> getAll();

    BalanceUpdateResponse debit(UUID accountId, BigDecimal amount);
}
