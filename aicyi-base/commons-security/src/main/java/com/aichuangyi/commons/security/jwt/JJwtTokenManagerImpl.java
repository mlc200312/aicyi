package com.aichuangyi.commons.security.jwt;

import com.aichuangyi.commons.core.TokenManager;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Min
 * @description jjwt 实现
 * @date 18:30
 **/
public class JJwtTokenManagerImpl implements TokenManager {
    private static final String JJWT_SUBJECT = "aichuangyi";
    private static final long TOKEN_EXPIRE_MS = 3600000L;
    private SecretKey secretKey;

    public JJwtTokenManagerImpl(String base64Key) {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Key));
    }

    public JJwtTokenManagerImpl(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String createToken(String id, Map<String, Object> claimMap) {
        return createToken(id, new Date(System.currentTimeMillis() + TOKEN_EXPIRE_MS), claimMap); // 1小时后过期,)
    }


    @Override
    public String createToken(String id, Date expire, Map<String, Object> claimMap) {
        return Jwts.builder()
                .signWith(secretKey, SignatureAlgorithm.HS256)    // 签名算法和密钥
                .setSubject(JJWT_SUBJECT)// 设置主题
                .setId(id)
                .setIssuedAt(new Date())// 设置签发时间
                .setExpiration(expire) // 1小时后过期
                .addClaims(claimMap == null ? new HashMap<>() : claimMap)
                .compact();
    }

    @Override
    public String refreshToken(String token) {
        return refreshToken(token, new Date(System.currentTimeMillis() + TOKEN_EXPIRE_MS));
    }

    @Override
    public String refreshToken(String token, Date expire) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Jwts.builder()
                .signWith(secretKey, SignatureAlgorithm.HS256)    // 签名算法和密钥
                .setClaims(claims)
                .setIssuedAt(new Date())// 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRE_MS))
                .compact();
    }

    @Override
    public boolean verifyToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .requireSubject(JJWT_SUBJECT)// 必须匹配的主题
                    .build()
                    .parseClaimsJws(token);
            return true;
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
    public String verifyAndGetId(String token) {
        if (verifyToken(token)) {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return jws.getBody().getId();
        }
        throw new JwtException("verifyAndGetId has error");
    }

    @Override
    public Date verifyAndGetExpire(String token) {
        if (verifyToken(token)) {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return jws.getBody().getExpiration();
        }
        throw new JwtException("verifyAndGetExpire has error");
    }

    @Override
    public <T> T verifyAndGet(String token, String key, Class<T> clazz) {
        if (verifyToken(token)) {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return jws.getBody().get(key, clazz);
        }
        throw new JwtException("verifyAndGet has error");
    }

    public static SecretKey randomSecretKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }
}
