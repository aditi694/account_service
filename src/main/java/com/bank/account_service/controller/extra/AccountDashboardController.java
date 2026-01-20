//package com.bank.account_service.controller;
//
//import com.bank.account_service.dto.account.AccountDashboardResponse;
//import com.bank.account_service.security.AuthUser;
//import com.bank.account_service.service.DashboardService;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/account")
//public class AccountDashboardController {
//
//    private final DashboardService dashboardService;
//
//    public AccountDashboardController(DashboardService dashboardService) {
//        this.dashboardService = dashboardService;
//    }
//
//    @GetMapping("/dashboard")
//    public AccountDashboardResponse dashboard() {
//        AuthUser user = (AuthUser) SecurityContextHolder
//                .getContext()
//                .getAuthentication()
//                .getPrincipal();
//
//        return dashboardService.getDashboard(user);
//    }
//}
//
