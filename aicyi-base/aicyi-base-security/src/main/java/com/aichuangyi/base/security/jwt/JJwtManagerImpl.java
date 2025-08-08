package com.aichuangyi.base.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * @author Mr.Min
 * @description jjwt 实现
 * @date 18:30
 **/
public class JJwtManagerImpl implements JwtManager {
    private SecretKey secretKey;

    public JJwtManagerImpl(String base64Key) {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Key));
    }

    public JJwtManagerImpl(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String createToken(Map<String, Object> claimMap) {
        return Jwts.builder()
                .setSubject("user123")                      // 设置主题
                .setIssuedAt(new Date())                    // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1小时后过期
                .signWith(secretKey, SignatureAlgorithm.HS256)    // 签名算法和密钥
                .setClaims(claimMap)
                .compact();
    }

    @Override
    public boolean verifyToken(String jwt) {
        return false;
    }

    @Override
    public String refreshToken(String token) {
        return "";
    }

    public SecretKey randomSecretKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }
}
