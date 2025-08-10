package com.aichuangyi.test.base.security.jwt;

import com.aichuangyi.base.security.jwt.JJwtTokenManagerImpl;
import com.aichuangyi.base.core.TokenManager;
import com.aichuangyi.base.util.DateUtils;
import com.aichuangyi.base.util.id.IdGenerator;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aichuangyi.test.util.RandomGenerator;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;

/**
 * @Description: 业务描述
 * @Author: Mr.Min
 * @Date: 2025/8/9
 **/
public class TokenManagerTest extends BaseLoggerTest {

    @Test
    public void jwtTest() {
        SecretKey secretKey = JJwtTokenManagerImpl.randomSecretKey();
        TokenManager tokenManager = new JJwtTokenManagerImpl(secretKey);
        String id = IdGenerator.generateId() + "";
        HashMap<String, Object> claimMap = new HashMap<>();
        claimMap.put("username", RandomGenerator.generateFullName());

        String token = tokenManager.createToken(id, claimMap);
        boolean verifyToken = tokenManager.verifyToken(token);
        String getId = tokenManager.verifyAndGetId(token);
        String username = tokenManager.verifyAndGet(token, "username", String.class);
        String refreshToken = tokenManager.refreshToken(token);

        Date date = tokenManager.verifyAndGetExpire(token);
        Date date2 = tokenManager.verifyAndGetExpire(refreshToken);

        log("jwtTest", id, token, verifyToken, getId.equals(id), username, refreshToken, DateUtils.formatDate(date), DateUtils.formatDate(date2));

    }
}
