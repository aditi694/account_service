package com.bank.account_service.security;

import com.bank.account_service.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();
    @Test
    void generateToken_success() {
        String token = jwtUtil.generate(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "CUSTOMER"
        );

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }
    @Test
    void parseToken_success() {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        String token = jwtUtil.generate(accountId, customerId, "CUSTOMER");
        Claims claims = jwtUtil.parse(token);

        assertEquals(accountId.toString(), claims.get("accountId"));
        assertEquals(customerId.toString(), claims.get("customerId"));
        assertEquals("CUSTOMER", claims.get("role"));

        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }
    @Test
    void rolePrefixRemoved() {
        String token = jwtUtil.generate(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "ROLE_ADMIN"
        );

        Claims claims = jwtUtil.parse(token);

        assertEquals("ADMIN", claims.get("role"));
    }
    @Test
    void invalidToken_throwsException() {
        assertThrows(JwtException.class,
                () -> jwtUtil.parse("invalid.token.value"));
    }
}


