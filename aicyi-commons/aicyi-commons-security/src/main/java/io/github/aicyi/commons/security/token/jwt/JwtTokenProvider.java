package io.github.aicyi.commons.security.token.jwt;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.core.token.TokenProvider;
import io.github.aicyi.commons.lang.exception.TokenException;
import io.github.aicyi.commons.lang.exception.TokenExpiredException;
import io.github.aicyi.commons.lang.exception.TokenInvalidException;
import io.github.aicyi.commons.lang.exception.TokenParseException;
import io.github.aicyi.commons.lang.Assert;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * JWT Token Provider
 *
 * @author Mr.Min
 * @description JWT令牌提供者
 * @date 2025/05/14
 */
public class JwtTokenProvider implements TokenProvider<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

    /**
     * 未指定有效期时的默认过期时间，避免产生永久 token
     */
    private static final long DEFAULT_TTL_MILLIS = TimeUnit.HOURS.toMillis(2);

    /**
     * JWT标准Claim
     */
    private static final Set<String> STANDARD_CLAIM_NAMES =
            new HashSet<>(Arrays.asList(
                    Claims.SUBJECT,
                    Claims.ISSUER,
                    Claims.ID,
                    Claims.EXPIRATION,
                    Claims.ISSUED_AT,
                    Claims.NOT_BEFORE,
                    Claims.AUDIENCE
            ));

    /**
     * JWT签名Key
     */
    private final SecretKey secretKey;

    /**
     * JWT解析器
     */
    private final JwtParser jwtParser;

    /**
     * JWT签发者
     */
    private final String issuer;

    /**
     * JWT主题
     */
    private final String subject;

    public JwtTokenProvider(String secretKey, String issuer, String subject) {

        this(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), issuer, subject);
    }

    public JwtTokenProvider(SecretKey secretKey, String issuer, String subject) {

        Assert.notNull(secretKey, "secretKey");

        Assert.notBlank(issuer, "issuer");

        Assert.notBlank(subject, "subject");

        this.secretKey = secretKey;
        this.issuer = issuer;
        this.subject = subject;

        this.jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(issuer)
                .requireSubject(subject)
                .build();
    }

    /**
     * 创建Token
     *
     * @param tokenId
     * @param attributes
     * @return
     */
    @Override
    public String create(String tokenId, Map<String, Object> attributes) {

        return create(tokenId, attributes, null, null);
    }

    /**
     * 创建Token
     *
     * @param tokenId
     * @param attributes
     * @param ttl
     * @param timeUnit
     * @return
     */
    @Override
    public String create(String tokenId, Map<String, Object> attributes, Long ttl, TimeUnit timeUnit) {

        Assert.notBlank(tokenId, "tokenId");

        Date now = new Date();

        JwtBuilder builder = Jwts.builder()
                .setId(tokenId)
                .setIssuer(issuer)
                .setSubject(subject)
                .setIssuedAt(now)
                .signWith(secretKey, Jwts.SIG.HS256);

        if (attributes != null && !attributes.isEmpty()) {

            builder.addClaims(attributes);
        }

        long ttlMillis;
        if (ttl != null && timeUnit != null) {

            ttlMillis = timeUnit.toMillis(ttl);

        } else {

            // 未指定有效期时使用默认过期时间，避免签发永久有效 token
            LOGGER.warn("create token [{}] without ttl, use default expiration: {}ms", tokenId, DEFAULT_TTL_MILLIS);

            ttlMillis = DEFAULT_TTL_MILLIS;
        }

        builder.setExpiration(new Date(now.getTime() + ttlMillis));

        return builder.compact();
    }

    /**
     * Token是否有效
     *
     * @param token
     * @return
     */
    @Override
    public boolean isValid(String token) {

        try {

            parseClaims(token);

            return true;

        } catch (TokenException e) {

            return false;
        }
    }

    /**
     * 解析Claims
     *
     * @param token
     * @return
     */
    @Override
    public Claims parseClaims(String token) {

        try {

            return jwtParser.parseClaimsJws(token).getBody();

        } catch (ExpiredJwtException e) {

            throw new TokenExpiredException("token expired", e);

        } catch (UnsupportedJwtException | MalformedJwtException e) {

            throw new TokenParseException("invalid token format", e);

        } catch (SignatureException e) {

            throw new TokenInvalidException("invalid token signature", e);

        } catch (JwtException | IllegalArgumentException e) {

            throw new TokenParseException("invalid token", e);
        }
    }

    /**
     * 获取自定义属性
     *
     * @param token
     * @return
     */
    @Override
    public Map<String, Object> getAttributes(String token) {

        Claims claims;
        try {

            claims = parseClaims(token);

        } catch (Exception e) {

            return Collections.emptyMap();
        }

        Map<String, Object> attributes = new HashMap<>();

        for (Map.Entry<String, Object> entry : claims.entrySet()) {

            if (STANDARD_CLAIM_NAMES.contains(entry.getKey())) {

                continue;
            }

            attributes.put(entry.getKey(), entry.getValue());
        }

        return attributes;
    }

    /**
     * 获取指定属性
     *
     * @param token
     * @param attributeName
     * @param <T>
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String token, String attributeName) {

        return (T) parseClaims(token).get(attributeName);
    }

    /**
     * 获取Token ID
     *
     * @param token
     * @return
     */
    @Override
    public String getTokenId(String token) {

        return parseClaims(token).getId();
    }

    /**
     * 获取过期时间
     *
     * @param token
     * @return
     */
    @Override
    public Date getExpiration(String token) {

        return parseClaims(token).getExpiration();
    }

    /**
     * 获取剩余有效期
     *
     * @param token
     * @param unit
     * @return 负值表示已过期；-1 表示未设置有效期（永久有效）
     */
    @Override
    public long getRemainingTtl(String token, TimeUnit unit) {

        Date expiration = getExpiration(token);

        if (expiration == null) {

            return -1;
        }

        long millis = expiration.getTime() - System.currentTimeMillis();

        return unit.convert(millis, TimeUnit.MILLISECONDS);
    }
}
