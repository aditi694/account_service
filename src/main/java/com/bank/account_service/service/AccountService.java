package com.bank.account_service.service;

import com.bank.account_service.dto.auth.LoginRequest;
import com.bank.account_service.dto.auth.LoginResponse;

import java.math.BigDecimal;

public interface AccountService {

    LoginResponse login(LoginRequest request);

}
