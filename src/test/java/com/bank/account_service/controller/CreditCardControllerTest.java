package com.bank.account_service.controller;

import com.bank.account_service.dto.auth.response.BaseResponse;
import com.bank.account_service.dto.card.*;
import com.bank.account_service.dto.card.response.CreditCardIssueResponse;
import com.bank.account_service.dto.card.response.CreditCardResponse;
import com.bank.account_service.entity.CreditCardRequest;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.security.SecurityUtil;
import com.bank.account_service.service.CreditCardService;
import com.bank.account_service.util.AppConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreditCardControllerTest {

    @Mock
    private CreditCardService service;

    @InjectMocks
    private CreditCardController controller;

    @Test
    void testApply_Approved() {
        CreditCardApplyRequest request = new CreditCardApplyRequest();
        request.setCardHolderName("John");

        AuthUser user = mock(AuthUser.class);

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser).thenReturn(user);
            when(service.applyCreditCard(user, "John"))
                    .thenReturn(null);

            ResponseEntity<BaseResponse<Map<String, Object>>> response =
                    controller.apply(request);

            Assertions.assertEquals(HttpStatus.OK,
                    response.getStatusCode());

            Assertions.assertEquals(AppConstants.SUCCESS_MSG,
                    response.getBody().getResultInfo().getResultMsg());

            Assertions.assertTrue(
                    (Boolean) response.getBody().getData()
                            .get(AppConstants.SUCCESS)
            );
        }
    }

    @Test
    void testApply_Pending() {
        CreditCardApplyRequest request = new CreditCardApplyRequest();
        request.setCardHolderName("John");

        UUID requestId = UUID.randomUUID();
        AuthUser user = mock(AuthUser.class);

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser).thenReturn(user);
            when(service.applyCreditCard(user, "John"))
                    .thenReturn(requestId);

            ResponseEntity<BaseResponse<Map<String, Object>>> response =
                    controller.apply(request);

            Assertions.assertEquals(HttpStatus.ACCEPTED,
                    response.getStatusCode());

            Assertions.assertEquals(requestId.toString(),
                    response.getBody().getData()
                            .get(AppConstants.REQUEST_ID));
        }
    }

    @Test
    void testGetStatus() {

        UUID customerId = UUID.randomUUID();
        CreditCardResponse responseObj =
                new CreditCardResponse();

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentCustomerId)
                    .thenReturn(customerId);

            when(service.getCreditCardSummary(customerId))
                    .thenReturn(responseObj);

            BaseResponse<CreditCardResponse> result =
                    controller.getStatus();

            Assertions.assertEquals(responseObj, result.getData());
            Assertions.assertEquals(AppConstants.SUCCESS_MSG,
                    result.getResultInfo().getResultMsg());
        }
    }

    @Test
    void testPending_Admin() {
        AuthUser user = mock(AuthUser.class);
        when(user.isAdmin()).thenReturn(true);

        List<CreditCardRequest> list =
                List.of(new CreditCardRequest());

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            when(service.getPendingRequests())
                    .thenReturn(list);

            BaseResponse<Map<String, Object>> result =
                    controller.pending();

            Assertions.assertEquals(1,
                    result.getData().get(AppConstants.COUNT));
        }
    }

    @Test
    void testPending_NotAdmin_ShouldThrow() {
        AuthUser user = mock(AuthUser.class);
        when(user.isAdmin()).thenReturn(false);

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            Assertions.assertThrows(BusinessException.class,
                    () -> controller.pending());
        }
    }

    @Test
    void testApprove_Admin() {
        UUID requestId = UUID.randomUUID();
        AuthUser user = mock(AuthUser.class);
        when(user.isAdmin()).thenReturn(true);

        CreditCardIssueResponse responseObj =
                new CreditCardIssueResponse();

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            when(service.approveRequest(requestId))
                    .thenReturn(responseObj);

            BaseResponse<CreditCardIssueResponse> result =
                    controller.approve(requestId);

            Assertions.assertEquals(responseObj, result.getData());
            Assertions.assertEquals(AppConstants.CREDIT_CARD_APPROVED,
                    result.getResultInfo().getResultMsg());
        }
    }

    @Test
    void testReject_Admin() {
        UUID requestId = UUID.randomUUID();
        AuthUser user = mock(AuthUser.class);
        when(user.isAdmin()).thenReturn(true);

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            BaseResponse<Void> result =
                    controller.reject(requestId, "Invalid docs");

            verify(service)
                    .rejectRequest(requestId, "Invalid docs");

            Assertions.assertEquals(AppConstants.CONFLICT_MSG,
                    result.getResultInfo().getResultMsg());
        }
    }
}
