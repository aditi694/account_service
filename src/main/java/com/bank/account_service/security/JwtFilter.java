package com.bank.account_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip login and internal endpoints
        if (path.startsWith("/api/account/login") || path.startsWith("/api/internal/")) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            sendError(response, HttpStatus.UNAUTHORIZED, "Authorization token is missing");
            return;
        }

        try {
            String token = header.substring(7);
            Claims claims = jwtUtil.parse(token);

            // Extract from token
            UUID accountId = claims.get("accountId") != null
                    ? UUID.fromString(claims.get("accountId", String.class))
                    : null;
            UUID customerId = UUID.fromString(claims.get("customerId", String.class));
            String role = claims.get("role", String.class);

            // ✅ KEY FIX: Ensure role has ROLE_ prefix
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }

            System.out.println("✅ Account Service Auth: " + role + " | Customer: " + customerId);

            // Create AuthUser
            AuthUser authUser = AuthUser.builder()
                    .accountId(accountId)
                    .customerId(customerId)
                    .role(role)
                    .build();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            authUser,
                            null,
                            List.of(new SimpleGrantedAuthority(role))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);

        } catch (Exception e) {
            System.err.println("❌ Account Service JWT Auth Failed: " + e.getMessage());
            e.printStackTrace();
            sendError(response, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }

    private void sendError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        );

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}