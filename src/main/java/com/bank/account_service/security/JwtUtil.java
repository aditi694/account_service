package com.bank.account_service.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret:}")
    private String secret;

    @PostConstruct
    public void validateSecret() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is not configured");
        }
    }


    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
