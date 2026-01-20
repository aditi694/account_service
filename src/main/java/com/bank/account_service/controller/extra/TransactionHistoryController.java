//package com.bank.account_service.controller;
//
//import com.bank.account_service.dto.transaction.TransactionHistoryResponse;
//import com.bank.account_service.security.AuthUser;
//import com.bank.account_service.service.TransactionHistoryService;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/account")
//public class TransactionHistoryController {
//
//    private final TransactionHistoryService service;
//
//    public TransactionHistoryController(TransactionHistoryService service) {
//        this.service = service;
//    }
//
//    @GetMapping("/transactions")
//    public TransactionHistoryResponse getTransactions(
//            @RequestParam(defaultValue = "10") int limit,
//            @RequestParam(defaultValue = "1") int page
//    ) {
//        AuthUser user = (AuthUser) SecurityContextHolder
//                .getContext()
//                .getAuthentication()
//                .getPrincipal();
//
//        return service.getTransactions(user.getAccountId(), limit, page);
//    }
//}