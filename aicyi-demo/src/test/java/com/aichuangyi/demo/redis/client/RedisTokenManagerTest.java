package com.aichuangyi.demo.redis.client;

import com.aichuangyi.core.TokenManager;
import com.aichuangyi.commons.security.jwt.JJwtTokenManagerImpl;
import com.aichuangyi.commons.util.DateUtils;
import com.aichuangyi.commons.util.id.IdGenerator;
import com.aichuangyi.demo.AicyiDemoApplication;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aichuangyi.test.util.RandomGenerator;
import com.aicyiframework.redis.token.RedisTokenManagerImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 19:53
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AicyiDemoApplication.class)
public class RedisTokenManagerTest extends BaseLoggerTest {
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    private TokenManager tokenManager;
    private String expiredToken;

    @Before
    public void before() {
        SecretKey secretKey = JJwtTokenManagerImpl.randomSecretKey();
        tokenManager = new RedisTokenManagerImpl(redisConnectionFactory, new JJwtTokenManagerImpl(secretKey));
        expiredToken = tokenManager.createToken("123", DateUtils.parseDate("2020-01-1 00:00:00"), new HashMap<>());
    }

    @Test
    public void tokenTest() {
        String id = IdGenerator.generateId() + "";
        String fullName = RandomGenerator.generateFullName();
        HashMap<String, Object> claimMap = new HashMap<>();
        claimMap.put("username", fullName);

        String token = tokenManager.createToken(id, claimMap);
        Date date = tokenManager.getExpire(token);

        boolean verifyToken = tokenManager.verifyToken(token);
        boolean verifyToken1 = tokenManager.verifyToken(expiredToken);

        String getId = tokenManager.getId(token);
        String username = tokenManager.get(token, "username", String.class);

        String refreshToken = tokenManager.refreshToken(token, new Date(System.currentTimeMillis() + 7200000L));
        Date date2 = tokenManager.getExpire(refreshToken);

        log("tokenTest", token, refreshToken, verifyToken, verifyToken1, getId.equals(id), username.equals(fullName), DateUtils.formatDate(date), DateUtils.formatDate(date2));
    }
}
