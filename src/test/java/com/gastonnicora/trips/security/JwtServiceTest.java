package com.gastonnicora.trips.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "test-secret-key-with-at-least-32-bytes-long";

    private JwtService jwtService;
    private UUID userUuid;

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        setField(jwtService, "SECRET", SECRET);
        jwtService.init();
        userUuid = UUID.randomUUID();
    }

    @Test
    void shouldGenerateTokenWithExpectedClaims() {
        String token = jwtService.generateToken("user@test.com", 3, userUuid);

        assertNotNull(token);
        assertTrue(jwtService.isValid(token));
        assertEquals("user@test.com", jwtService.extractUsername(token));
        assertEquals(3, jwtService.extractVersion(token));
        assertEquals(userUuid, jwtService.extractUUID(token));
    }

    @Test
    void shouldRejectExpiredToken() {
        String token = Jwts.builder()
                .subject("user@test.com")
                .claim("ver", 0)
                .claim("userId", userUuid.toString())
                .issuedAt(new java.util.Date(System.currentTimeMillis() - 10_000))
                .expiration(new java.util.Date(System.currentTimeMillis() - 1_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();

        assertFalse(jwtService.isValid(token));
    }

    @Test
    void shouldRejectMalformedToken() {
        assertFalse(jwtService.isValid("not-a-jwt"));
    }

    @Test
    void shouldRejectTokenSignedWithAnotherKey() {
        String token = Jwts.builder()
                .subject("user@test.com")
                .claim("ver", 0)
                .claim("userId", userUuid.toString())
                .signWith(Keys.hmacShaKeyFor(
                        "another-test-secret-key-with-at-least-32-bytes".getBytes()))
                .compact();

        assertFalse(jwtService.isValid(token));
    }
}
