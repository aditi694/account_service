package com.bank.account_service.controller;

import com.bank.account_service.dto.account.*;
import com.bank.account_service.dto.auth.*;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.security.SecurityUtil;
import com.bank.account_service.service.*;
import com.bank.account_service.util.AppConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private BalanceService balanceService;

    @Mock
    private DashboardService dashboardService;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private AccountController controller;

    @Test
    void testLogin() {
        LoginRequest request = new LoginRequest();
        LoginResponse response = new LoginResponse();

        when(accountService.login(request)).thenReturn(response);

        BaseResponse<LoginResponse> result = controller.login(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(response, result.getData());
        Assertions.assertEquals(AppConstants.SUCCESS_MSG,
                result.getResultInfo().getResultMsg());
        Assertions.assertEquals(AppConstants.SUCCESS_CODE,
                result.getResultInfo().getResultCode());
    }

    @Test
    void testGetBalance() {
        UUID accountId = UUID.randomUUID();
        BalanceResponse response = new BalanceResponse();

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentAccountId)
                    .thenReturn(accountId);

            when(balanceService.getBalance(accountId))
                    .thenReturn(response);

            BaseResponse<BalanceResponse> result =
                    controller.getBalance();

            Assertions.assertNotNull(result);
            Assertions.assertEquals(response, result.getData());
            Assertions.assertEquals(AppConstants.SUCCESS_MSG,
                    result.getResultInfo().getResultMsg());
            Assertions.assertEquals(AppConstants.SUCCESS_CODE,
                    result.getResultInfo().getResultCode());

            verify(balanceService).getBalance(accountId);
        }
    }

    @Test
    void testDashboard() {

        AuthUser mockUser = mock(AuthUser.class);
        AccountDashboardResponse response =
                new AccountDashboardResponse();

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(mockUser);

            when(dashboardService.getDashboard(mockUser))
                    .thenReturn(response);

            BaseResponse<AccountDashboardResponse> result =
                    controller.dashboard();

            Assertions.assertNotNull(result);
            Assertions.assertEquals(response, result.getData());
            Assertions.assertEquals(AppConstants.SUCCESS_MSG,
                    result.getResultInfo().getResultMsg());
            Assertions.assertEquals(AppConstants.SUCCESS_CODE,
                    result.getResultInfo().getResultCode());

            verify(dashboardService).getDashboard(mockUser);
        }
    }


    @Test
    void testChangePassword() {
        UUID accountId = UUID.randomUUID();
        ChangePasswordRequest request =
                new ChangePasswordRequest();
        ChangePasswordResponse response =
                new ChangePasswordResponse();

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentAccountId)
                    .thenReturn(accountId);

            when(passwordService.changePassword(accountId, request))
                    .thenReturn(response);

            BaseResponse<ChangePasswordResponse> result =
                    controller.changePassword(request);

            Assertions.assertNotNull(result);
            Assertions.assertEquals(response, result.getData());
            Assertions.assertEquals("Password changed successfully",
                    result.getResultInfo().getResultMsg());
            Assertions.assertEquals(AppConstants.SUCCESS_CODE,
                    result.getResultInfo().getResultCode());

            verify(passwordService)
                    .changePassword(accountId, request);
        }
    }

    @Test
    void testRootEndpoint() {
        BaseResponse<Void> result = controller.root();

        Assertions.assertNull(result.getData());
        Assertions.assertEquals("Invalid account API endpoint",
                result.getResultInfo().getResultMsg());
        Assertions.assertEquals("NOT_FOUND",
                result.getResultInfo().getResultCode());
    }
}
