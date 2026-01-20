package com.bank.account_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;
import io.jsonwebtoken.Jwts;


@Component
public class JwtUtil {

    private static final String SECRET =
            "BANKING_ADMIN_SECRET_12345678901234567890";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

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
    public String generate(UUID accountId, UUID customerId, String role) {

        return Jwts.builder()
                .claim("accountId", accountId.toString())
                .claim("customerId", customerId.toString())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24h
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}
