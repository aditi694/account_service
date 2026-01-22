package com.bank.account_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    // 🔥 SAME SECRET IN ALL SERVICES
    private static final String SECRET = "BANKING_UNIFIED_SECRET_KEY_32_CHARACTERS_MINIMUM_LENGTH_2026";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generate(UUID accountId, UUID customerId, String role) {
        return Jwts.builder()
                .claim("accountId", accountId.toString())
                .claim("customerId", customerId.toString())
                .claim("role", role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public AuthUser getAuthUser(String token) {
        Claims claims = parse(token);

        return AuthUser.builder()
                .accountId(UUID.fromString(claims.get("accountId", String.class)))
                .customerId(UUID.fromString(claims.get("customerId", String.class)))
                .role(claims.get("role", String.class))
                .build();
    }
}
