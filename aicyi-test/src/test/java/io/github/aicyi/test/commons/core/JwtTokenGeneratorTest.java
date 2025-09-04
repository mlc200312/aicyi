package io.github.aicyi.test.commons.core;

import io.github.aicyi.commons.core.jwt.JwtTokenGenerator;
import io.github.aicyi.commons.util.DateUtils;
import io.github.aicyi.commons.util.id.IdGenerator;
import io.github.aicyi.test.domain.BaseLoggerTest;
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
public class JwtTokenGeneratorTest extends BaseLoggerTest {
    private JwtTokenGenerator tokenGenerator;
    private String userId;
    private String expiredToken;
    private String token;

    @Before
    public void before() {
        userId = IdGenerator.generateId() + "";
        tokenGenerator = new JwtTokenGenerator(Keys.secretKeyFor(SignatureAlgorithm.HS256), "test");
        expiredToken = tokenGenerator.generateToken(userId, new HashMap<>(), -1 * 3600 * 1000, TimeUnit.MILLISECONDS);

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("jti", IdGenerator.generateV7Id());
        claims.put("userId", userId);
        Date date = new Date(System.currentTimeMillis() + 2 * 3600 * 1000);
        token = tokenGenerator.generateToken(claims, date.getTime(), TimeUnit.MILLISECONDS);
    }

    @Test
    public void test() {
        boolean verifyToken0 = tokenGenerator.verifyToken(expiredToken);
        boolean verifyToken1 = tokenGenerator.verifyToken(token);

        Map<String, Object> claims = tokenGenerator.parseToken(token).orElse(null);
        String getId = tokenGenerator.getId(token).orElse(null);
        Date date = tokenGenerator.getExpiration(token).get();
        String getUserId = MapUtils.getString(claims, "userId");

        assert !verifyToken0;

        assert verifyToken1;

        assert getUserId.equals(userId);

        log("test", token, verifyToken0, verifyToken1, claims, getId, DateUtils.formatDate(date), getUserId, getUserId.equals(userId));
    }
}
