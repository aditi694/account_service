package com.bank.account_service.controller;

import com.bank.account_service.controller.AccountController;
import com.bank.account_service.dto.account.AccountDashboardResponse;
import com.bank.account_service.dto.account.BalanceResponse;
import com.bank.account_service.dto.account.ChangePasswordResponse;
import com.bank.account_service.dto.auth.LoginResponse;
import com.bank.account_service.dto.loan.LoanApprovalResponse;
import com.bank.account_service.dto.loan.LoanRequestResponse;
import com.bank.account_service.enums.LoanStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.security.JwtFilter;
import com.bank.account_service.security.JwtUtil;
import com.bank.account_service.security.SecurityUtil;
import com.bank.account_service.service.*;
import com.bank.account_service.util.AppConstants;
import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoanControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService service;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtFilter jwtFilter;

    @Test
    void requestLoan_autoApproved() throws Exception {

        UUID accountId = UUID.randomUUID();

        LoanRequestResponse response = LoanRequestResponse.builder()
                .loanId("L1")
                .status(LoanStatus.ACTIVE)
                .message("Approved")
                .build();

        String json = ("""
                 {
                  "loanType":"PERSONAL",
                  "amount":10000
                }
                """);

        try (MockedStatic<SecurityUtil> mocked =
                     Mockito.mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentAccountId)
                    .thenReturn(accountId);

            when(service.requestLoan(eq(accountId), any()))
                    .thenReturn(response);

            mockMvc.perform(post("/api/account/loans/request")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultInfo.resultMsg")
                            .value("Loan approved successfully"));

            verify(service).requestLoan(eq(accountId), any());
        }
    }

    @Test
    void requestLoan_pending() throws Exception {

        UUID accountId = UUID.randomUUID();

        LoanRequestResponse response = LoanRequestResponse.builder()
                .loanId("L2")
                .status(LoanStatus.REQUESTED)
                .message("Pending")
                .build();
        String json = ("""
                {
                  "loanType":"PERSONAL",
                  "amount":10000
                }
                """);

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentAccountId)
                    .thenReturn(accountId);

            when(service.requestLoan(eq(accountId), any()))
                    .thenReturn(response);

            mockMvc.perform(post("/api/account/loans/request")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultInfo.resultMsg")
                            .value("Loan request submitted"));
        }
    }

    @Test
    void myLoansStatus_success() throws Exception {

        UUID customerId = UUID.randomUUID();

        try (MockedStatic<SecurityUtil> mocked =
                     Mockito.mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentCustomerId)
                    .thenReturn(customerId);

            when(service.getLoans(customerId))
                    .thenReturn(List.of());

            mockMvc.perform(get("/api/account/loans"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultInfo.resultMsg")
                            .value(AppConstants.SUCCESS_MSG));

            verify(service).getLoans(customerId);
        }
    }
    @Test
    void pendingLoans_adminSuccess() throws Exception {

        AuthUser admin = AuthUser.builder()
                .accountId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .role("ROLE_ADMIN")
                .build();

        try (MockedStatic<SecurityUtil> mocked =
                     Mockito.mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(admin);

            when(service.getPendingLoans())
                    .thenReturn(List.of());

            mockMvc.perform(get("/api/admin/loans/pending"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.success").value(true));

            verify(service).getPendingLoans();
        }
    }
    @Test
    void approve_success() throws Exception {

        AuthUser admin = AuthUser.builder()
                .role("ROLE_ADMIN")
                .build();

        LoanApprovalResponse response =
                LoanApprovalResponse.builder()
                        .loanId("L1")
                        .build();

        try (MockedStatic<SecurityUtil> mocked =
                     Mockito.mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(admin);

            when(service.approveLoan("L1"))
                    .thenReturn(response);

            mockMvc.perform(post("/api/admin/loans/L1/approve"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultInfo.resultMsg")
                            .value(AppConstants.LOAN_APPROVED));

            verify(service).approveLoan("L1");
        }
    }
    @Test
    void reject_success() throws Exception {

        AuthUser admin = AuthUser.builder()
                .role("ROLE_ADMIN")
                .build();

        LoanApprovalResponse response =
                LoanApprovalResponse.builder()
                        .loanId("L1")
                        .build();

        try (MockedStatic<SecurityUtil> mocked =
                     Mockito.mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(admin);

            when(service.rejectLoan("L1"))
                    .thenReturn(response);

            mockMvc.perform(post("/api/admin/loans/L1/reject"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultInfo.resultMsg")
                            .value("Loan rejected"));

            verify(service).rejectLoan("L1");
        }
    }
    @Test
    void nonAdminForbidden_approveLoan_UseCase() throws Exception {

        AuthUser user = AuthUser.builder()
                .role("ROLE_CUSTOMER")
                .build();

        try (MockedStatic<SecurityUtil> mocked =
                     Mockito.mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            mockMvc.perform(post("/api/admin/loans/L1/approve"))
                    .andExpect(status().isForbidden());

            verify(service, never()).approveLoan(any());
        }
    }

}