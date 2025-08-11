package com.aichuangyi.test.commons.security.jwt;

import com.aichuangyi.commons.security.jwt.JJwtTokenManagerImpl;
import com.aichuangyi.core.TokenManager;
import com.aichuangyi.commons.util.DateUtils;
import com.aichuangyi.commons.util.id.IdGenerator;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aichuangyi.test.util.RandomGenerator;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;

/**
 * @Description: 业务描述
 * @Author: Mr.Min
 * @Date: 2025/8/9
 **/
public class JJwtTokenManagerTest extends BaseLoggerTest {
    private TokenManager tokenManager;
    private String expiredToken;

    @Before
    public void before() {
        SecretKey secretKey = JJwtTokenManagerImpl.randomSecretKey();
        tokenManager = new JJwtTokenManagerImpl(secretKey);
        expiredToken = tokenManager.createToken("123", DateUtils.parseDate("2020-01-1 00:00:00"), new HashMap<>());
    }

    @Test
    public void jwtTest() {
        String id = IdGenerator.generateId() + "";
        String fullName = RandomGenerator.generateFullName();
        HashMap<String, Object> claimMap = new HashMap<>();
        claimMap.put("username", fullName);

        String token = tokenManager.createToken(id, null, claimMap);
        Date date = tokenManager.getExpire(token);

        boolean verifyToken = tokenManager.verifyToken(token);
        boolean verifyToken1 = tokenManager.verifyToken(expiredToken);

        String getId = tokenManager.getId(token);
        String username = tokenManager.get(token, "username", String.class);

        String refreshToken = tokenManager.refreshToken(token, new Date(System.currentTimeMillis() + 7200000L));
        Date date2 = tokenManager.getExpire(refreshToken);

        log("jwtTest", token, refreshToken, verifyToken, verifyToken1, getId.equals(id), username.equals(fullName), DateUtils.formatDate(date), DateUtils.formatDate(date2));
    }
}
