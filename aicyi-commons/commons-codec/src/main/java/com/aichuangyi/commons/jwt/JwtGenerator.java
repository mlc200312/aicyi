package com.aichuangyi.commons.jwt;

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
 * @description 业务描述
 * @date 10:50
 **/
public class JwtGenerator {
    private static final String JJWT_SUBJECT = "aichuangyi";

    private final String issuer;
    private final SecretKey secretKey;

    public JwtGenerator(SecretKey secretKey, String issuer) {
        this.secretKey = secretKey;
        this.issuer = issuer;
    }

    public JwtGenerator(String secretKey, String issuer) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
    }

    public String getIssuer() {
        return issuer;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    /**
     * 生成token(永久有效)
     *
     * @param id
     * @param claims
     * @return Token
     */
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

    /**
     * 生成token(永久有效)
     *
     * @param claims
     * @return Token
     */
    public String generateToken(Map<String, Object> claims) {
        String id = IdGenerator.generateV7Id();
        return generateToken(id, claims);
    }

    /**
     * 生成token
     *
     * @param id
     * @param claims
     * @param timeout
     * @param unit
     * @return Token
     */
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

    /**
     * 生成token
     *
     * @param claims
     * @param timeout
     * @param unit
     * @return Token
     */
    public String generateToken(Map<String, Object> claims, long timeout, TimeUnit unit) {
        String id = IdGenerator.generateV7Id();
        return generateToken(id, claims, timeout, unit);
    }

    /**
     * 验证Token签名
     *
     * @param token Token
     * @return 是否有效
     */
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

    /**
     * 解析Token声明
     *
     * @param token Token
     * @return 声明集合
     */
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

    /**
     * 解析Token声明并获取Id
     *
     * @param token Token
     * @return 声明id
     */
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

    /**
     * 解析Token声明并返回有效期
     *
     * @param token
     * @return 声明有效期
     */
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
