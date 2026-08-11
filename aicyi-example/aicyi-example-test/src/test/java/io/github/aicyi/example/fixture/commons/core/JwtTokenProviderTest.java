package io.github.aicyi.example.fixture.commons.core;

import io.github.aicyi.commons.lang.exception.TokenExpiredException;
import io.github.aicyi.commons.lang.exception.TokenInvalidException;
import io.github.aicyi.commons.lang.exception.TokenParseException;
import io.github.aicyi.commons.security.token.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @Description: 业务描述
 * @Author: Mr.Min
 * @Date: 2025/8/9
 **/
public class JwtTokenProviderTest {
    /**
     * 32字节以上
     */
    private static final String SECRET =
            "aicyi-jwt-secret-key-demo-1234567890";

    private static final String ISSUER = "aicyi";

    private static final String SUBJECT = "access-token";

    private JwtTokenProvider tokenProvider;

    @Before
    public void setUp() {

        SecretKey secretKey = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );

        tokenProvider = new JwtTokenProvider(
                secretKey,
                ISSUER,
                SUBJECT
        );
    }

    @Test
    @DisplayName("create token success")
    public void testCreateToken() {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("userId", 1001L);
        attributes.put("username", "tom");

        String token = tokenProvider.create(
                "token-001",
                attributes
        );

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("parse claims success")
    public void testParseClaims() {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("userId", 1001L);
        attributes.put("role", "admin");

        String token = tokenProvider.create(
                "token-001",
                attributes
        );

        Claims claims = tokenProvider.parseClaims(token);

        assertNotNull(claims);

        assertEquals("token-001", claims.getId());

        assertEquals(ISSUER, claims.getIssuer());

        assertEquals(SUBJECT, claims.getSubject());

        assertEquals(1001, ((Number) claims.get("userId")).intValue());

        assertEquals("admin", claims.get("role"));
    }

    @Test
    @DisplayName("is valid token")
    public void testIsValid() {

        String token = tokenProvider.create(
                "token-001",
                null
        );

        assertTrue(tokenProvider.isValid(token));
    }

    @Test
    @DisplayName("invalid token")
    public void testInvalidToken() {

        String invalidToken = "invalid.jwt.token";

        assertFalse(tokenProvider.isValid(invalidToken));

        assertThrows(
                TokenParseException.class,
                () -> tokenProvider.parseClaims(invalidToken)
        );
    }

    @Test
    @DisplayName("expired token")
    public void testExpiredToken() throws Exception {

        String token = tokenProvider.create(
                "token-001",
                null,
                1L,
                TimeUnit.MILLISECONDS
        );

        Thread.sleep(10);

        assertFalse(tokenProvider.isValid(token));

        assertThrows(
                TokenExpiredException.class,
                () -> tokenProvider.parseClaims(token)
        );
    }

    @Test
    @DisplayName("get token id")
    public void testGetTokenId() {

        String token = tokenProvider.create(
                "token-888",
                null
        );

        String tokenId = tokenProvider.getTokenId(token);

        assertEquals("token-888", tokenId);
    }

    @Test
    @DisplayName("get attributes")
    public void testGetAttributes() {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("userId", 1001L);
        attributes.put("nickname", "jack");

        String token = tokenProvider.create(
                "token-001",
                attributes
        );

        Map<String, Object> result = tokenProvider.getAttributes(token);

        assertEquals(2, result.size());

        assertEquals(
                1001,
                ((Number) result.get("userId")).intValue()
        );

        assertEquals(
                "jack",
                result.get("nickname")
        );
    }

    @Test
    @DisplayName("get attribute")
    public void testGetAttribute() {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("tenantId", "t-001");

        String token = tokenProvider.create(
                "token-001",
                attributes
        );

        String tenantId =
                tokenProvider.getAttribute(token, "tenantId");

        assertEquals("t-001", tenantId);
    }

    @Test
    @DisplayName("get expiration")
    public void testGetExpiration() {

        String token = tokenProvider.create(
                "token-001",
                null,
                5L,
                TimeUnit.MINUTES
        );

        Date expiration = tokenProvider.getExpiration(token);

        assertNotNull(expiration);

        assertTrue(expiration.after(new Date()));
    }

    @Test
    @DisplayName("get remaining ttl")
    public void testGetRemainingTtl() {

        String token = tokenProvider.create(
                "token-001",
                null,
                5L,
                TimeUnit.MINUTES
        );

        long ttl = tokenProvider.getRemainingTtl(
                token,
                TimeUnit.SECONDS
        );

        assertTrue(ttl > 0);

        assertTrue(ttl <= 300);
    }

    @Test
    @DisplayName("invalid signature")
    public void testInvalidSignature() {

        JwtTokenProvider anotherProvider =
                new JwtTokenProvider(
                        "another-secret-key-12345678901234567890",
                        ISSUER,
                        SUBJECT
                );

        String token = anotherProvider.create(
                "token-001",
                null
        );

        assertFalse(tokenProvider.isValid(token));

        assertThrows(
                TokenInvalidException.class,
                () -> tokenProvider.parseClaims(token)
        );
    }

    @Test
    @DisplayName("null attributes")
    public void testNullAttributes() {

        String token = tokenProvider.create(
                "token-001",
                null
        );

        Map<String, Object> attributes = tokenProvider.getAttributes(token);

        assertNotNull(attributes);

        assertTrue(attributes.isEmpty());
    }

    @Test
    @DisplayName("empty attributes")
    public void testEmptyAttributes() {

        String token = tokenProvider.create(
                "token-001",
                new HashMap<>()
        );

        Map<String, Object> attributes = tokenProvider.getAttributes(token);

        assertNotNull(attributes);

        assertTrue(attributes.isEmpty());
    }

    @Test
    @DisplayName("create token without expiration")
    public void testCreateWithoutExpiration() {

        String token = tokenProvider.create(
                "token-001",
                null,
                null,
                null
        );

        assertNotNull(token);

        Date expiration = tokenProvider.getExpiration(token);

        assertNull(expiration);
    }

    @Test
    @DisplayName("blank token id")
    public void testBlankTokenId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> tokenProvider.create("", null)
        );
    }

    @Test
    @DisplayName("constructor validation")
    public void testConstructorValidation() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new JwtTokenProvider(
                        (SecretKey) null,
                        ISSUER,
                        SUBJECT
                )
        );

        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        assertThrows(
                IllegalArgumentException.class,
                () -> new JwtTokenProvider(
                        secretKey,
                        "",
                        SUBJECT
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new JwtTokenProvider(
                        secretKey,
                        ISSUER,
                        ""
                )
        );
    }
}
