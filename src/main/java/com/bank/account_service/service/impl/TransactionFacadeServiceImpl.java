//package com.bank.account_service.service.impl;
//
//import com.bank.account_service.dto.transaction.CreditRequest;
//import com.bank.account_service.dto.transaction.DebitRequest;
//import com.bank.account_service.dto.transaction.CreditResponse;
//import com.bank.account_service.dto.transaction.DebitResponse;
//import com.bank.account_service.entity.Account;
//import com.bank.account_service.entity.DebitCard;
//import com.bank.account_service.entity.Transaction;
//import com.bank.account_service.enums.CardStatus;
//import com.bank.account_service.enums.TransactionType;
//import com.bank.account_service.exception.BusinessException;
//import com.bank.account_service.repository.AccountRepository;
//import com.bank.account_service.repository.DebitCardRepository;
//import com.bank.account_service.repository.TransactionRepository;
//import com.bank.account_service.service.TransactionFacadeService;
//import jakarta.transaction.Transactional;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Service
//@Transactional
//public class TransactionFacadeServiceImpl implements TransactionFacadeService {
//
//    private final AccountRepository accountRepo;
//    private final DebitCardRepository debitCardRepo;
//    private final TransactionRepository transactionRepo;
//
//    public TransactionFacadeServiceImpl(
//            AccountRepository accountRepo,
//            DebitCardRepository debitCardRepo,
//            TransactionRepository transactionRepo
//    ) {
//        this.accountRepo = accountRepo;
//        this.debitCardRepo = debitCardRepo;
//        this.transactionRepo = transactionRepo;
//    }
//
//    @Override
//    public DebitResponse debit(UUID customerId, DebitRequest req) {
//
//        if (req.getAmount() <= 0) {
//            throw BusinessException.invalidAmount();
//        }
//
//        Account acc = accountRepo.findByCustomerId(customerId)
//                .stream()
//                .filter(Account::isPrimaryAccount)
//                .findFirst()
//                .orElseThrow(BusinessException::accountNotFound);
//
//        DebitCard card = debitCardRepo.findByAccountNumber(acc.getAccountNumber())
//                .orElseThrow(() -> BusinessException.badRequest("Debit card not issued"));
//
//        if (card.getStatus() != CardStatus.ACTIVE) {
//            throw BusinessException.forbidden("Debit card is blocked");
//        }
//
//        if (acc.getBalance().doubleValue() < req.getAmount()) {
//            throw BusinessException.badRequest("Insufficient balance");
//        }
//
//        double previous = acc.getBalance().doubleValue();
//
//        acc.setBalance(acc.getBalance().subtract(BigDecimal.valueOf(req.getAmount())));
//
//        Transaction tx = Transaction.builder()
//                .accountId(acc.getId())
//                .type(TransactionType.DEBIT)   // ✅ ENUM
//                .amount(BigDecimal.valueOf(req.getAmount()))
//                .description(req.getDescription())
//                .balanceAfter(acc.getBalance())
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        transactionRepo.save(tx);
//
//        return DebitResponse.builder()
//                .success(true)
//                .message("Debit successful")
//                .transactionId(tx.getId().toString())
//                .previousBalance(previous)
//                .currentBalance(acc.getBalance().doubleValue())
//                .timestamp(LocalDateTime.now())
//                .build();
//    }
//
//    @Override
//    public CreditResponse credit(UUID customerId, CreditRequest req) {
//
//        if (req.getAmount() <= 0) {
//            throw BusinessException.badRequest("Invalid amount");
//        }
//
//        Account acc = accountRepo.findByCustomerId(customerId)
//                .stream()
//                .filter(Account::isPrimaryAccount)
//                .findFirst()
//                .orElseThrow(BusinessException::accountNotFound);
//
//        acc.setBalance(acc.getBalance().add(BigDecimal.valueOf(req.getAmount())));
//
//        Transaction tx = Transaction.builder()
//                .accountId(acc.getId())
//                .type(TransactionType.CREDIT) // ✅ ENUM
//                .amount(BigDecimal.valueOf(req.getAmount()))
//                .description(req.getDescription())
//                .balanceAfter(acc.getBalance())
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        transactionRepo.save(tx);
//
//        return CreditResponse.builder()
//                .success(true)
//                .transactionId(tx.getId().toString())
//                .currentBalance(acc.getBalance().doubleValue())
//                .build();
//    }
//}
