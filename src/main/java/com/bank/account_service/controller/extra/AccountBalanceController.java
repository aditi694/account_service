//package com.bank.account_service.controller;
//
//import com.bank.account_service.dto.account.BalanceResponse;
//import com.bank.account_service.security.AuthUser;
//import com.bank.account_service.service.BalanceService;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/account")
//public class AccountBalanceController {
//
//    private final BalanceService balanceService;
//
//    public AccountBalanceController(BalanceService balanceService) {
//        this.balanceService = balanceService;
//    }
//
//    @GetMapping("/balance")
//    public BalanceResponse getBalance() {
//        AuthUser user = (AuthUser) SecurityContextHolder
//                .getContext()
//                .getAuthentication()
//                .getPrincipal();
//
//        return balanceService.getBalance(user.getAccountId());
//    }
//}