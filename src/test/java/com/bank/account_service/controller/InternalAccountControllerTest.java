package com.bank.account_service.controller;

import com.bank.account_service.dto.account.AccountSyncRequest;
import com.bank.account_service.dto.account.BalanceUpdateRequest;
import com.bank.account_service.entity.Account;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.service.BalanceService;
import com.bank.account_service.service.InternalAccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InternalAccountControllerTest {

    @Mock
    private InternalAccountService service;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private InternalAccountController controller;

    @Test
    void testCreateAccount() {
        AccountSyncRequest request = new AccountSyncRequest();

        ResponseEntity<String> response =
                controller.createAccount(request);

        verify(service).createAccount(request);

        Assertions.assertEquals("Account created successfully",
                response.getBody());
    }

    @Test
    void testGetBalance_Found() {
        String accountNumber = "123";
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(1000));

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        BigDecimal result =
                controller.getBalance(accountNumber);

        Assertions.assertEquals(BigDecimal.valueOf(1000), result);
    }

    @Test
    void testGetBalance_NotFound() {
        String accountNumber = "123";

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(BusinessException.class,
                () -> controller.getBalance(accountNumber));
    }

    @Test
    void testAccountExists_True() {
        when(accountRepository.findByAccountNumber("123"))
                .thenReturn(Optional.of(new Account()));

        boolean result = controller.accountExists("123");

        Assertions.assertTrue(result);
    }

    @Test
    void testAccountExists_False() {
        when(accountRepository.findByAccountNumber("123"))
                .thenReturn(Optional.empty());

        boolean result = controller.accountExists("123");

        Assertions.assertFalse(result);
    }

    @Test
    void testCredit() {
        controller.credit("123", BigDecimal.TEN);

        verify(service).credit("123", BigDecimal.TEN);
    }

    @Test
    void testDebit() {
        controller.debit("123", BigDecimal.ONE);

        verify(service).debit("123", BigDecimal.ONE);
    }

    @Test
    void testTransfer() {
        controller.transfer("A1", "A2",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(5));

        verify(service).transfer("A1", "A2",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(5));
    }

    @Test
    void testGetAccountOwner_Found() {
        String accountNumber = "123";
        UUID customerId = UUID.randomUUID();

        Account account = new Account();
        account.setCustomerId(customerId);

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        UUID result =
                controller.getAccountOwner(accountNumber);

        Assertions.assertEquals(customerId, result);
    }

    @Test
    void testGetAccountOwner_NotFound() {
        when(accountRepository.findByAccountNumber("123"))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(BusinessException.class,
                () -> controller.getAccountOwner("123"));
    }

    @Test
    void testUpdateBalance() {
        BalanceUpdateRequest request =
                new BalanceUpdateRequest();

        controller.updateBalance(request);

        verify(service).updateBalance(request);
    }
}
