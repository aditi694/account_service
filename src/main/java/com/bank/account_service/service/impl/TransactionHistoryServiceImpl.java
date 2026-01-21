//package com.bank.account_service.service.impl;
//
//import com.bank.account_service.dto.transaction.TransactionHistoryResponse;
//import com.bank.account_service.dto.transaction.TransactionResponse;
//import com.bank.account_service.entity.Transaction;
//import com.bank.account_service.repository.TransactionRepository;
//import com.bank.account_service.service.TransactionHistoryService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@Transactional(readOnly = true)
//@RequiredArgsConstructor
//public class TransactionHistoryServiceImpl implements TransactionHistoryService {
//
//    private final TransactionRepository transactionRepo;
//
//    @Override
//    public TransactionHistoryResponse getTransactions(UUID accountId, int limit, int page) {
//
//        var pageable = PageRequest.of(page - 1, limit);
//        var txPage = transactionRepo
//                .findByAccountIdOrderByCreatedAtDesc(accountId, pageable);
//
//        List<TransactionResponse> transactions = txPage.getContent()
//                .stream()
//                .map(this::map)
//                .toList();
//
//        return TransactionHistoryResponse.builder()
//                .success(true)
//                .transactions(transactions)
//                .total((int) txPage.getTotalElements())
//                .page(page)
//                .limit(limit)
//                .build();
//    }
//
//    private TransactionResponse map(Transaction tx) {
//        return TransactionResponse.builder()
//                .transactionId(tx.getId().toString())
//                .type(tx.getType().name()) // ✅ ENUM → STRING
//                .amount(tx.getAmount().doubleValue())
//                .description(tx.getDescription())
//                .balanceAfter(tx.getBalanceAfter().doubleValue())
//                .timestamp(tx.getCreatedAt())
//                .build();
//    }
//
//}
