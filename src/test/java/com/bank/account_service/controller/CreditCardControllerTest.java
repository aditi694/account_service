package com.bank.account_service.controller;

import com.bank.account_service.dto.card.CreditCardIssueResponse;
import com.bank.account_service.dto.card.CreditCardResponse;
import com.bank.account_service.entity.CreditCardRequest;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreditCardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CreditCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreditCardService service;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtFilter jwtFilter;

    @Test
    void apply_autoApproved() throws Exception {

        AuthUser user = AuthUser.builder()
                .accountId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .role("ROLE_CUSTOMER")
                .build();

        String json = """
            {
              "cardHolderName": "Aditi"
            }
            """;

        try (MockedStatic<SecurityUtil> securityMock =
                     Mockito.mockStatic(SecurityUtil.class)) {

            securityMock.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            when(service.applyCreditCard(user, "Aditi"))
                    .thenReturn(null);

            mockMvc.perform(post("/api/account/credit-cards/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.success").value(true))
                    .andExpect(jsonPath("$.data.status")
                            .value(AppConstants.STATUS_APPROVED));

            verify(service).applyCreditCard(user, "Aditi");
        }
    }
    @Test
    void apply_pending() throws Exception {

        AuthUser user = AuthUser.builder()
                .accountId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .role("ROLE_CUSTOMER")
                .build();

        String json = """
            {
              "cardHolderName": "Aditi"
            }
            """;

        UUID requestId = UUID.randomUUID();

        try (MockedStatic<SecurityUtil> securityMock =
                     Mockito.mockStatic(SecurityUtil.class)) {

            securityMock.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            when(service.applyCreditCard(user, "Aditi"))
                    .thenReturn(requestId);

            mockMvc.perform(post("/api/account/credit-cards/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isAccepted())
                    .andExpect(jsonPath("$.data.status")
                            .value(AppConstants.STATUS_PENDING))
                    .andExpect(jsonPath("$.data.requestId")
                            .value(requestId.toString()));
        }
    }
    @Test
    void getStatus_success() throws Exception {

        UUID customerId = UUID.randomUUID();

        CreditCardResponse response = CreditCardResponse.builder()
                .cardNumber("1234")
                .status("ACTIVE")
                .build();

        try (MockedStatic<SecurityUtil> securityMock =
                     Mockito.mockStatic(SecurityUtil.class)) {

            securityMock.when(SecurityUtil::getCurrentCustomerId)
                    .thenReturn(customerId);

            when(service.getCreditCardSummary(customerId))
                    .thenReturn(response);

            mockMvc.perform(get("/api/account/credit-cards/status"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.cardNumber")
                            .value("1234"));
            verify(service).getCreditCardSummary(customerId);
        }
    }
    @Test
    void pending_adminSuccess() throws Exception {

        AuthUser admin = AuthUser.builder()
                .role("ROLE_ADMIN")
                .build();

        List<CreditCardRequest> list = List.of(new CreditCardRequest());

        try (MockedStatic<SecurityUtil> securityMock =
                     Mockito.mockStatic(SecurityUtil.class)) {

            securityMock.when(SecurityUtil::getCurrentUser)
                    .thenReturn(admin);

            when(service.getPendingRequests()).thenReturn(list);

            mockMvc.perform(get("/api/admin/credit-cards/pending"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.count").value(1));
        }
    }
    @Test
    void approve_adminSuccess() throws Exception {

        AuthUser admin = AuthUser.builder()
                .role("ROLE_ADMIN")
                .build();

        UUID id = UUID.randomUUID();

        CreditCardIssueResponse response =
                CreditCardIssueResponse.builder()
                        .cardNumber("1234")
                        .status("APPROVED")
                        .build();

        try (MockedStatic<SecurityUtil> securityMock =
                     Mockito.mockStatic(SecurityUtil.class)) {

            securityMock.when(SecurityUtil::getCurrentUser)
                    .thenReturn(admin);

            when(service.approveRequest(id)).thenReturn(response);

            mockMvc.perform(post("/api/admin/credit-cards/approve/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultInfo.resultMsg")
                            .value(AppConstants.CREDIT_CARD_APPROVED));
        }
    }
    @Test
    void reject_adminSuccess() throws Exception {

        AuthUser admin = AuthUser.builder()
                .role("ROLE_ADMIN")
                .build();

        UUID id = UUID.randomUUID();

        try (MockedStatic<SecurityUtil> securityMock =
                     Mockito.mockStatic(SecurityUtil.class)) {

            securityMock.when(SecurityUtil::getCurrentUser)
                    .thenReturn(admin);

            mockMvc.perform(post("/api/admin/credit-cards/reject/{id}?reason=fraud", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultInfo.resultMsg")
                            .value(AppConstants.CONFLICT_MSG));

            verify(service).rejectRequest(id, "fraud");
        }
    }

    @Test
    void approve_nonAdmin_shouldReturnForbidden() throws Exception {

        AuthUser user = AuthUser.builder()
                .role("ROLE_CUSTOMER")
                .build();

        UUID id = UUID.randomUUID();

        try (MockedStatic<SecurityUtil> securityMock =
                     Mockito.mockStatic(SecurityUtil.class)) {

            securityMock.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            mockMvc.perform(post("/api/admin/credit-cards/approve/{id}", id))
                    .andExpect(status().isForbidden());
        }

        verify(service, never()).approveRequest(any());
    }
    @Test
    void reject_nonAdmin_shouldReturnForbidden() throws Exception {

        AuthUser user = AuthUser.builder()
                .role("ROLE_CUSTOMER")
                .build();

        UUID id = UUID.randomUUID();

        try (MockedStatic<SecurityUtil> securityMock =
                     Mockito.mockStatic(SecurityUtil.class)) {

            securityMock.when(SecurityUtil::getCurrentUser)
                    .thenReturn(user);

            mockMvc.perform(post("/api/admin/credit-cards/reject/{id}?reason=test", id))
                    .andExpect(status().isForbidden());
        }

        verify(service, never()).rejectRequest(any(), any());
    }

}