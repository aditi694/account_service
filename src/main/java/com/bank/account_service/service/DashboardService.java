package com.bank.account_service.service;

import com.bank.account_service.dto.account.response.AccountDashboardResponse;
import com.bank.account_service.security.AuthUser;

public interface DashboardService {

    AccountDashboardResponse getDashboard(AuthUser user);
}
