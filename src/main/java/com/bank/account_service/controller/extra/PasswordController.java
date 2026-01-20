//package com.bank.account_service.controller;
//
//import com.bank.account_service.dto.account.ChangePasswordRequest;
//import com.bank.account_service.dto.account.ChangePasswordResponse;
//import com.bank.account_service.security.AuthUser;
//import com.bank.account_service.service.PasswordService;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/account")
//public class PasswordController {
//
//    private final PasswordService passwordService;
//
//    public PasswordController(PasswordService passwordService) {
//        this.passwordService = passwordService;
//    }
//
//    @PostMapping("/change-password")
//    public ChangePasswordResponse changePassword(@RequestBody ChangePasswordRequest request) {
//        AuthUser user = (AuthUser) SecurityContextHolder
//                .getContext()
//                .getAuthentication()
//                .getPrincipal();
//
//        return passwordService.changePassword(user.getAccountId(), request);
//    }
//}