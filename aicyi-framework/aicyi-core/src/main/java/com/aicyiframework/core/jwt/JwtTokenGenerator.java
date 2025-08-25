package com.aicyiframework.core.jwt;

import com.aichuangyi.commons.lang.TokenGenerator;
import com.aichuangyi.commons.util.Assert;
import com.aichuangyi.commons.util.id.IdGenerator;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description jwt令牌生成器
 * @date 10:50
 **/
public class JwtTokenGenerator implements TokenGenerator<String> {
    private static final String JJWT_SUBJECT = "aichuangyi";

    private final String issuer;
    private final SecretKey secretKey;

    public JwtTokenGenerator(SecretKey secretKey, String issuer) {
        this.secretKey = secretKey;
        this.issuer = issuer;
    }

    public JwtTokenGenerator(String secretKey, String issuer) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
    }

    public String getIssuer() {
        return issuer;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    @Override
    public String generateToken(String id, Map<String, Object> claims) {
        Assert.notBlank(id, "id");
        Date now = new Date();

        // 自定义声明和默认声明
        return Jwts.builder()
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .setSubject(JJWT_SUBJECT)
                .setIssuer(issuer)
                .setId(id)
                .addClaims(claims)
                .setIssuedAt(now)
                .compact();
    }

    @Override
    public String generateToken(Map<String, Object> claims) {
        String id = IdGenerator.generateV7Id();
        return generateToken(id, claims);
    }

    @Override
    public String generateToken(String id, Map<String, Object> claims, long timeout, TimeUnit unit) {
        Assert.notBlank(id, "id");
        Assert.notNull(unit, "unit");
        Date now = new Date();
        Date expiration = new Date(now.getTime() + unit.toMillis(timeout));

        // 合并自定义声明和默认声明
        return Jwts.builder()
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .setSubject(JJWT_SUBJECT)
                .setIssuer(issuer)
                .setId(id)
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .compact();
    }

    @Override
    public String generateToken(Map<String, Object> claims, long timeout, TimeUnit unit) {
        String id = IdGenerator.generateV7Id();
        return generateToken(id, claims, timeout, unit);
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
    public Optional<Date> getExpiration(String token) {
        if (verifyToken(token)) {
            return Optional.ofNullable(Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration());
        }
        return Optional.empty();
    }
}
