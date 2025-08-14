package com.aichuangyi.commons.security.jwt;

import com.aichuangyi.commons.util.Assert;
import com.aichuangyi.commons.util.id.IdGenerator;
import com.aichuangyi.core.token.TokenGenerator;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 10:50
 **/
public class JwtTokenGenerator implements TokenGenerator<String> {
    private static final String JJWT_SUBJECT = "aichuangyi";

    private final String issuer;
    private final SecretKey secretKey;

    public JwtTokenGenerator(String issuer, SecretKey secretKey) {
        this.issuer = issuer;
        this.secretKey = secretKey;
    }

    public JwtTokenGenerator(String issuer, String secretKey) {
        this.issuer = issuer;
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String getIssuer() {
        return issuer;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    @Override
    public String generateToken(String userId, Map<String, Object> claims) {
        Assert.notBlank(userId, "userId");

        String id = IdGenerator.generateV7Id();
        Date now = new Date();

        // 合并自定义声明和默认声明
        Map<String, Object> enhancedClaims = new HashMap<>(claims);
        enhancedClaims.put("userId", userId);

        String token = Jwts.builder()
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .setSubject(JJWT_SUBJECT)
                .setIssuer(issuer)
                .setId(id)
                .addClaims(enhancedClaims)
                .setIssuedAt(now)
                .compact();

        return token;
    }

    @Override
    public String generateToken(String userId, Map<String, Object> claims, long timeout, TimeUnit unit) {
        Assert.notBlank(userId, "userId");
        Assert.notNull(unit, "unit");

        String id = IdGenerator.generateV7Id();
        Date now = new Date();
        Date expiration = new Date(now.getTime() + unit.toMillis(timeout));

        // 合并自定义声明和默认声明
        Map<String, Object> enhancedClaims = new HashMap<>(claims);
        enhancedClaims.put("userId", userId);

        String token = Jwts.builder()
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .setSubject(JJWT_SUBJECT)
                .setIssuer(issuer)
                .setId(id)
                .addClaims(enhancedClaims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .compact();

        return token;
    }

    @Override
    public boolean verifyToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .requireSubject(JJWT_SUBJECT)// 必须匹配的主题
                    .requireIssuer(issuer)// 必须匹配的颁发者
                    .build()
                    .parseClaimsJws(token) != null;
        } catch (ExpiredJwtException ex) {
            // 令牌过期
        } catch (UnsupportedJwtException ex) {
            // 不支持的JWT
        } catch (MalformedJwtException ex) {
            // 格式错误
        } catch (SignatureException ex) {
            // 签名无效
        } catch (IllegalArgumentException ex) {
            // 参数错误
        }
        return false;
    }

    @Override
    public Optional<Map<String, Object>> parseToken(String token) {
        if (verifyToken(token)) {
            return Optional.ofNullable(Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody());
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getId(String token) {
        if (verifyToken(token)) {
            return Optional.ofNullable(Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getId());
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getUserId(String token) {
        Optional<Map<String, Object>> claims = parseToken(token);
        if (claims.isPresent()) {
            String userId = (String) claims.get().get("userId");
            return Optional.ofNullable(userId);
        }
        return Optional.empty();
    }
}
