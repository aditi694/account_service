//package com.bank.account_service.controller;
//
//import com.bank.account_service.dto.auth.LoginRequest;
//import com.bank.account_service.dto.auth.LoginResponse;
//import com.bank.account_service.service.AccountService;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/account")
//public class AccountAuthController {
//
//    private final AccountService service;
//
//    public AccountAuthController(AccountService service) {
//        this.service = service;
//    }
//
//    @PostMapping("/login")
//    public LoginResponse login(@RequestBody LoginRequest request) {
//        return service.login(request);
//    }
//}
