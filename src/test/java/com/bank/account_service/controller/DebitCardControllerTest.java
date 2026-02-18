package com.bank.account_service.controller;

import com.bank.account_service.dto.auth.response.BaseResponse;
import com.bank.account_service.dto.card.response.DebitCardResponse;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.security.SecurityUtil;
import com.bank.account_service.service.CardService;
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
public class DebitCardControllerTest {

    @Mock
    private CardService service;

    @InjectMocks
    private DebitCardController controller;

    @Test
    void testMyDebitCard() {
        UUID accountId = UUID.randomUUID();
        DebitCardResponse response = new DebitCardResponse();

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentAccountId)
                    .thenReturn(accountId);

            when(service.getDebitCard(accountId))
                    .thenReturn(response);

            BaseResponse<DebitCardResponse> result =
                    controller.myDebitCard();

            Assertions.assertNotNull(result);
            Assertions.assertEquals(response, result.getData());
            Assertions.assertEquals(AppConstants.SUCCESS_MSG,
                    result.getResultInfo().getResultMsg());
            Assertions.assertEquals(AppConstants.SUCCESS_CODE,
                    result.getResultInfo().getResultCode());

            verify(service).getDebitCard(accountId);
        }
    }

    @Test
    void testBlock_Admin() {
        UUID accountId = UUID.randomUUID();
        AuthUser user = mock(AuthUser.class);
        when(user.isAdmin()).thenReturn(true);

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            BaseResponse<Void> result =
                    controller.block(accountId);

            verify(service).blockDebitCard(accountId);

            Assertions.assertNull(result.getData());
            Assertions.assertEquals("Debit card blocked",
                    result.getResultInfo().getResultMsg());
            Assertions.assertEquals(AppConstants.SUCCESS_CODE,
                    result.getResultInfo().getResultCode());
        }
    }

    @Test
    void testUnblock_Admin() {
        UUID accountId = UUID.randomUUID();
        AuthUser user = mock(AuthUser.class);
        when(user.isAdmin()).thenReturn(true);

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            BaseResponse<Void> result =
                    controller.unblock(accountId);

            verify(service).unblockDebitCard(accountId);

            Assertions.assertNull(result.getData());
            Assertions.assertEquals("Debit card unblocked",
                    result.getResultInfo().getResultMsg());
            Assertions.assertEquals(AppConstants.SUCCESS_CODE,
                    result.getResultInfo().getResultCode());
        }
    }

    @Test
    void testBlock_NotAdmin_ShouldThrow() {
        UUID accountId = UUID.randomUUID();
        AuthUser user = mock(AuthUser.class);
        when(user.isAdmin()).thenReturn(false);

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            Assertions.assertThrows(BusinessException.class,
                    () -> controller.block(accountId));
        }
    }

    @Test
    void testUnblock_NotAdmin_ShouldThrow() {
        UUID accountId = UUID.randomUUID();
        AuthUser user = mock(AuthUser.class);
        when(user.isAdmin()).thenReturn(false);

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            Assertions.assertThrows(BusinessException.class,
                    () -> controller.unblock(accountId));
        }
    }
}
