package com.bank.account_service.service.impl;

import com.bank.account_service.dto.account.request.AccountSyncRequest;
import com.bank.account_service.dto.account.request.BalanceUpdateRequest;
import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.DebitCard;
import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.enums.AccountType;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.repository.DebitCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternalAccountServiceImplTest {

    @Mock
    private AccountRepository accountRepo;

    @Mock
    private DebitCardRepository debitCardRepo;

    @InjectMocks
    private InternalAccountServiceImpl service;
    private Account account;

    @BeforeEach
    void setup() {
        account = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber("ACC123")
                .customerId(UUID.randomUUID())
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.ACTIVE)
                .build();
    }

    @Test
    void createAccount_accountAlreadyExists() {
        AccountSyncRequest request = new AccountSyncRequest();
        request.setAccountNumber("ACC123");

        when(accountRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.of(account));

        service.createAccount(request);

        verify(accountRepo, never()).save(any());
        verify(debitCardRepo, never()).save(any());
    }

    @Test
    void createAccount_success() {
        AccountSyncRequest request = new AccountSyncRequest();
        request.setAccountNumber("ACC123");
        request.setCustomerId(UUID.randomUUID().toString());
        request.setAccountType("SAVINGS");
        request.setBalance(5000.0);
        request.setPrimaryAccount(true);
        request.setStatus("ACTIVE");
        request.setPasswordHash("hash");
        request.setIfscCode("IFSC001");

        when(accountRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.empty());
        when(accountRepo.save(any(Account.class)))
                .thenReturn(account);

        service.createAccount(request);
        verify(accountRepo, times(1)).save(any(Account.class));
        verify(debitCardRepo, times(1)).save(any(DebitCard.class));
    }

    @Test
    void debitCardFails_accountCreated() {
        AccountSyncRequest request = new AccountSyncRequest();
        request.setAccountNumber("ACC123");
        request.setCustomerId(UUID.randomUUID().toString());
        request.setAccountType("SAVINGS");
        request.setBalance(5000.0);
        request.setPrimaryAccount(true);
        request.setStatus("ACTIVE");
        request.setPasswordHash("hash");
        request.setIfscCode("IFSC001");

        when(accountRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.empty());
        when(accountRepo.save(any(Account.class)))
                .thenReturn(account);
        when(debitCardRepo.save(any(DebitCard.class)))
                .thenThrow(new RuntimeException());

        assertDoesNotThrow(() -> service.createAccount(request));

        verify(accountRepo, times(1)).save(any(Account.class));
    }
    @Test
    void credit_accountNotFound(){
        when(accountRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.empty());
        assertThrows(BusinessException.class,()-> service.credit("ACC123",BigDecimal.valueOf(500)));
    }
    @Test
    void debit_accountNotFound(){
        when(accountRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.empty());
        assertThrows(BusinessException.class,()-> service.debit("ACC123",BigDecimal.valueOf(500)));
    }
    @Test
    void credit_accountInactive(){
        account.setStatus(AccountStatus.BLOCKED);
        when(accountRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.of(account));
        assertThrows(BusinessException.class,()-> service.credit("ACC123",BigDecimal.valueOf(500)));
    }
    @Test
    void debit_accountInactive(){
        account.setStatus(AccountStatus.BLOCKED);
        when(accountRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.of(account));
        assertThrows(BusinessException.class,()-> service.debit("ACC123",BigDecimal.valueOf(500)));
    }
    @Test
    void credit_success() {
        when(accountRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.of(account));
        service.credit("ACC123", BigDecimal.valueOf(500));
        assertEquals(BigDecimal.valueOf(1500), account.getBalance());
        verify(accountRepo).save(account);
    }
    @Test
    void debit_insufficientBalance() {
        account.setBalance(BigDecimal.valueOf(100));
        when(accountRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.of(account));
        assertThrows(BusinessException.class,
                () -> service.debit("ACC123", BigDecimal.valueOf(200)));
        verify(accountRepo, never()).save(any());
    }
    @Test
    void debit_success() {
        when(accountRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.of(account));
        service.debit("ACC123", BigDecimal.valueOf(500));
        assertEquals(BigDecimal.valueOf(500), account.getBalance());
        verify(accountRepo).save(account);
    }
    @Test
    void transfer_senderNotFound(){
        when(accountRepo.findByAccountNumber("ACC12"))
                .thenReturn(Optional.empty());
        assertThrows(BusinessException.class,()->service.transfer("ACC12","ACC13",BigDecimal.valueOf(100),BigDecimal.ZERO));
    }
    @Test
    void transfer_receiverNotFound(){
        when(accountRepo.findByAccountNumber("ACC12"))
                .thenReturn(Optional.of(account));
        when(accountRepo.findByAccountNumber("ACC13"))
                .thenReturn(Optional.empty());
        assertThrows(BusinessException.class,()->service.transfer("ACC12","ACC13",BigDecimal.valueOf(100),BigDecimal.ZERO));
    }
    @Test
    void transfer_balanceCheck_insufficient(){
    account.setBalance(BigDecimal.valueOf(100));
        when(accountRepo.findByAccountNumber("ACC12"))
                .thenReturn(Optional.of(account));
        when(accountRepo.findByAccountNumber("ACC13"))
                .thenReturn(Optional.of(new Account()));
        assertThrows(BusinessException.class,
                ()->service.transfer("ACC12","ACC13",BigDecimal.valueOf(80),BigDecimal.valueOf(40)));
        verify(accountRepo, never()).save(any());
    }
    @Test
    void transfer_success() {
        Account receiver = Account.builder()
                .accountNumber("ACC2")
                .balance(BigDecimal.valueOf(500))
                .status(AccountStatus.ACTIVE)
                .build();
        when(accountRepo.findByAccountNumber("ACC1"))
                .thenReturn(Optional.of(account));
        when(accountRepo.findByAccountNumber("ACC2"))
                .thenReturn(Optional.of(receiver));
        service.transfer("ACC1", "ACC2",
                BigDecimal.valueOf(200), BigDecimal.valueOf(10));
//        account - ACC1 has 1000 then after debitbecomes
        assertEquals(BigDecimal.valueOf(790), account.getBalance());
        assertEquals(BigDecimal.valueOf(700), receiver.getBalance());
        verify(accountRepo).save(account);
        verify(accountRepo).save(receiver);
    }
    @Test
    void updateBalance_accountNotFound() {
        BalanceUpdateRequest req = new BalanceUpdateRequest();
        req.setAccountNumber("ACC123");
        when(accountRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.empty());
        assertThrows(BusinessException.class,
                () -> service.updateBalance(req));
    }
    @Test
    void updateBalance_duplicateTransactions(){
        account.setLastProcessedTransactionId("TXN1");
        BalanceUpdateRequest req = new BalanceUpdateRequest();
        req.setAccountNumber("ACC123");
        req.setTransactionId("TXN1");
        when(accountRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.of(account));
        service.updateBalance(req);
        verify(accountRepo,never()).save(any());
    }
    @Test
    void updateBalance_accountInactive(){
        account.setStatus(AccountStatus.BLOCKED);
        BalanceUpdateRequest req = new BalanceUpdateRequest();
        req.setAccountNumber("ACC123");
        req.setTransactionId("TXN1");
        req.setDelta(BigDecimal.valueOf(100));
        when(accountRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.of(account));
        assertThrows(BusinessException.class,()-> service.updateBalance(req));
    }
    @Test
    void updateBalance_checkResult_insufficient(){
        account.setBalance(BigDecimal.valueOf(100));
        BalanceUpdateRequest req = new BalanceUpdateRequest();
        req.setAccountNumber("ACC123");
        req.setTransactionId("TXN1");
        req.setDelta(BigDecimal.valueOf(-200));
        when(accountRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.of(account));
        assertThrows(BusinessException.class,()-> service.updateBalance(req));
        verify(accountRepo,never()).save(any());
    }
    @Test
    void updateBalance_success() {
        account.setBalance(BigDecimal.valueOf(100));
        BalanceUpdateRequest req = new BalanceUpdateRequest();
        req.setAccountNumber("ACC123");
        req.setTransactionId("TXN1");
        req.setDelta(BigDecimal.valueOf(50));
        when(accountRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.of(account));
        service.updateBalance(req);
        assertEquals(BigDecimal.valueOf(150), account.getBalance());
        assertEquals("TXN1", account.getLastProcessedTransactionId());
        verify(accountRepo).save(account);
    }
}
