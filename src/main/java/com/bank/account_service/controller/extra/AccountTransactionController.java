//package com.bank.account_service.controller;
//
//import com.bank.account_service.dto.transaction.CreditRequest;
//import com.bank.account_service.dto.transaction.DebitRequest;
//import com.bank.account_service.dto.transaction.CreditResponse;
//import com.bank.account_service.dto.transaction.DebitResponse;
//import com.bank.account_service.security.AuthUser;
//import com.bank.account_service.service.TransactionFacadeService;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/account/transactions")
//public class AccountTransactionController {
//
//    private final TransactionFacadeService service;
//
//    public AccountTransactionController(TransactionFacadeService service) {
//        this.service = service;
//    }
//
//    @PostMapping("/debit")
//    public DebitResponse debit(@RequestBody DebitRequest request) {
//        AuthUser user = getUser();
//        return service.debit(user.getCustomerId(), request);
//    }
//
//    @PostMapping("/credit")
//    public CreditResponse credit(@RequestBody CreditRequest request) {
//        AuthUser user = getUser();
//        return service.credit(user.getCustomerId(), request);
//    }
//
//    private AuthUser getUser() {
//        return (AuthUser) SecurityContextHolder
//                .getContext().getAuthentication().getPrincipal();
//    }
//}
