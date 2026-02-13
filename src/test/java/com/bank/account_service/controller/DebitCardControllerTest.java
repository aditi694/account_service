package com.bank.account_service.controller;

import com.bank.account_service.dto.card.DebitCardResponse;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.security.JwtFilter;
import com.bank.account_service.security.JwtUtil;
import com.bank.account_service.security.SecurityUtil;
import com.bank.account_service.service.CardService;
import com.bank.account_service.util.AppConstants;
import org.junit.jupiter.api.Test;
import com.bank.account_service.controller.DebitCardController;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DebitCardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DebitCardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService service;
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtFilter jwtFilter;

    @Test
    void debitCardStatus_success() throws Exception {
        UUID accountId = UUID.randomUUID();
        DebitCardResponse response = DebitCardResponse.builder()
                .cardNumber("1234")
                .status("ACTIVE")
                .build();
        try (MockedStatic<SecurityUtil> securityMock = Mockito.mockStatic(SecurityUtil.class)) {
            securityMock.when(SecurityUtil::getCurrentAccountId)
                    .thenReturn(accountId);

            when(service.getDebitCard(accountId))
                    .thenReturn(response);
            mockMvc.perform(get("/api/account/cards/debit"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.cardNumber").value("1234"))
                    .andExpect(jsonPath("$.resultInfo.resultMsg")
                            .value(AppConstants.SUCCESS_MSG));

            verify(service).getDebitCard(accountId);
        }
    }
    @Test
    void block_adminSuccess() throws Exception {

        UUID accountId = UUID.randomUUID();

        AuthUser admin = AuthUser.builder()
                .role("ROLE_ADMIN")
                .build();

        try (MockedStatic<SecurityUtil> securityMock =
                     Mockito.mockStatic(SecurityUtil.class)) {

            securityMock.when(SecurityUtil::getCurrentUser)
                    .thenReturn(admin);

            mockMvc.perform(post("/api/admin/cards/debit/{id}/block", accountId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultInfo.resultMsg")
                            .value("Debit card blocked"));

            verify(service).blockDebitCard(accountId);
        }
    }
    @Test
    void unblock_adminSuccess() throws Exception {

        UUID accountId = UUID.randomUUID();

        AuthUser admin = AuthUser.builder()
                .role("ROLE_ADMIN")
                .build();

        try (MockedStatic<SecurityUtil> securityMock =
                     Mockito.mockStatic(SecurityUtil.class)) {

            securityMock.when(SecurityUtil::getCurrentUser)
                    .thenReturn(admin);

            mockMvc.perform(post("/api/admin/cards/debit/{id}/unblock", accountId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultInfo.resultMsg")
                            .value("Debit card unblocked"));

            verify(service).unblockDebitCard(accountId);
        }
    }
    @Test
    void nonAdminForbidden_unblockUsecase() throws Exception {

        UUID accountId = UUID.randomUUID();

        AuthUser user = AuthUser.builder()
                .role("ROLE_CUSTOMER")
                .build();

        try (MockedStatic<SecurityUtil> securityMock =
                     Mockito.mockStatic(SecurityUtil.class)) {

            securityMock.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            mockMvc.perform(post("/api/admin/cards/debit/{id}/unblock", accountId))
                    .andExpect(status().isForbidden());

            verify(service, never()).unblockDebitCard(any());
        }
    }


}
