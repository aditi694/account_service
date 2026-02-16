package com.bank.account_service.controller;

import com.bank.account_service.dto.auth.BaseResponse;
import com.bank.account_service.dto.loan.*;
import com.bank.account_service.entity.Loan;
import com.bank.account_service.enums.LoanStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.security.SecurityUtil;
import com.bank.account_service.service.LoanService;
import com.bank.account_service.util.AppConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanControllerTest {

    @Mock
    private LoanService service;

    @InjectMocks
    private LoanController controller;

    @Test
    void testRequestLoan_Approved() {
        UUID accountId = UUID.randomUUID();
        IssueLoanRequest request = new IssueLoanRequest();

        LoanRequestResponse response = new LoanRequestResponse();
        response.setStatus(LoanStatus.ACTIVE);

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentAccountId)
                    .thenReturn(accountId);

            when(service.requestLoan(accountId, request))
                    .thenReturn(response);

            BaseResponse<LoanRequestResponse> result =
                    controller.requestLoan(request);

            Assertions.assertEquals(response, result.getData());
            Assertions.assertEquals("Loan approved successfully",
                    result.getResultInfo().getResultMsg());
            Assertions.assertEquals(AppConstants.SUCCESS_CODE,
                    result.getResultInfo().getResultCode());
        }
    }

    @Test
    void testRequestLoan_Submitted() {
        UUID accountId = UUID.randomUUID();
        IssueLoanRequest request = new IssueLoanRequest();

        LoanRequestResponse response = new LoanRequestResponse();
        response.setStatus(LoanStatus.REQUESTED);

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentAccountId)
                    .thenReturn(accountId);

            when(service.requestLoan(accountId, request))
                    .thenReturn(response);

            BaseResponse<LoanRequestResponse> result =
                    controller.requestLoan(request);

            Assertions.assertEquals("Loan request submitted",
                    result.getResultInfo().getResultMsg());
        }
    }

    @Test
    void testMyLoans() {
        UUID customerId = UUID.randomUUID();
        List<LoanResponse> list =
                List.of(new LoanResponse());

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentCustomerId)
                    .thenReturn(customerId);

            when(service.getLoans(customerId))
                    .thenReturn(list);

            BaseResponse<List<LoanResponse>> result =
                    controller.myLoans();

            Assertions.assertEquals(list, result.getData());
            Assertions.assertEquals(AppConstants.SUCCESS_MSG,
                    result.getResultInfo().getResultMsg());
        }
    }

    @Test
    void testPendingLoans_Admin() {
        AuthUser user = mock(AuthUser.class);
        when(user.isAdmin()).thenReturn(true);

        List<Loan> loans = List.of(new Loan());

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            when(service.getPendingLoans())
                    .thenReturn(loans);

            BaseResponse<Map<String, Object>> result =
                    controller.pendingLoans();

            Assertions.assertEquals(1,
                    result.getData().get("count"));
        }
    }

    @Test
    void testPendingLoans_NotAdmin_ShouldThrow() {
        AuthUser user = mock(AuthUser.class);
        when(user.isAdmin()).thenReturn(false);

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            Assertions.assertThrows(BusinessException.class,
                    () -> controller.pendingLoans());
        }
    }

    @Test
    void testApprove_Admin() {
        String loanId = "123";
        AuthUser user = mock(AuthUser.class);
        when(user.isAdmin()).thenReturn(true);

        LoanApprovalResponse response =
                new LoanApprovalResponse();

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            when(service.approveLoan(loanId))
                    .thenReturn(response);

            BaseResponse<LoanApprovalResponse> result =
                    controller.approve(loanId);

            Assertions.assertEquals(response, result.getData());
            Assertions.assertEquals(AppConstants.LOAN_APPROVED,
                    result.getResultInfo().getResultMsg());
        }
    }

    @Test
    void testReject_Admin() {
        String loanId = "123";
        AuthUser user = mock(AuthUser.class);
        when(user.isAdmin()).thenReturn(true);

        LoanApprovalResponse response =
                new LoanApprovalResponse();

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            when(service.rejectLoan(loanId))
                    .thenReturn(response);

            BaseResponse<LoanApprovalResponse> result =
                    controller.reject(loanId);

            Assertions.assertEquals(response, result.getData());
            Assertions.assertEquals("Loan rejected",
                    result.getResultInfo().getResultMsg());
        }
    }

    @Test
    void testApprove_NotAdmin_ShouldThrow() {
        AuthUser user = mock(AuthUser.class);
        when(user.isAdmin()).thenReturn(false);

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            Assertions.assertThrows(BusinessException.class,
                    () -> controller.approve("123"));
        }
    }
}
