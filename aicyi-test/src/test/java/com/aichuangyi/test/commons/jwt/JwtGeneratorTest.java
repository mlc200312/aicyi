package com.aichuangyi.test.commons.jwt;

import com.aichuangyi.commons.jwt.JwtGenerator;
import com.aichuangyi.commons.util.id.IdGenerator;
import com.aichuangyi.test.domain.BaseLoggerTest;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.collections4.MapUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 业务描述
 * @Author: Mr.Min
 * @Date: 2025/8/9
 **/
public class JwtGeneratorTest extends BaseLoggerTest {
    private JwtGenerator tokenGenerator;
    private String userId;
    private String expiredToken;
    private String token;

    @Before
    public void before() {
        userId = IdGenerator.generateId() + "";
        tokenGenerator = new JwtGenerator(Keys.secretKeyFor(SignatureAlgorithm.HS256), "test");
        expiredToken = tokenGenerator.generateToken(userId, new HashMap<>(), -1 * 3600 * 1000, TimeUnit.MILLISECONDS);

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("jti", IdGenerator.generateV7Id());
        claims.put("userId", IdGenerator.generateId());
        Date date = new Date(System.currentTimeMillis() + 2 * 3600 * 1000);
        token = tokenGenerator.generateToken(claims, date.getTime(), TimeUnit.MILLISECONDS);
    }

    @Test
    public void test() {
        boolean verifyToken0 = tokenGenerator.verifyToken(expiredToken);
        boolean verifyToken1 = tokenGenerator.verifyToken(token);

        String getId = tokenGenerator.getId(token).orElse(null);
        Map<String, Object> claims = tokenGenerator.parseToken(token).orElse(null);
        String getUserId = MapUtils.getString(claims, "userId");

        log("test", token, verifyToken0, verifyToken1, getId, getUserId, getUserId.equals(userId), claims);
    }
}
