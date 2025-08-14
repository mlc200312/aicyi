package com.aichuangyi.demo.redis.client;

import com.aichuangyi.commons.lang.token.UserInfo;
import com.aichuangyi.commons.security.SecretKeyUtils;
import com.aichuangyi.commons.util.id.IdGenerator;
import com.aichuangyi.core.token.TokenManager;
import com.aichuangyi.demo.AicyiDemoApplication;
import com.aicyiframework.redis.token.DefaultTokenConfig;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aichuangyi.test.util.RandomGenerator;
import com.aicyiframework.redis.token.RedisJwtTokenManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
    private TokenManager<String, UserInfo> tokenManager;
    private UserInfo userInfo;

    @Before
    public void before() {
        String singingKey = SecretKeyUtils.randomSecretKeyStr();
        System.out.println(singingKey);

        tokenManager = new RedisJwtTokenManager<>(DefaultTokenConfig.builder()
                .signingKey("LcR6QUhqWrDqK1InQDKlpZuKx6X/ZgEISdFpKwO3i/E=")
                .multiTokenAllowed(true)
                .build(), redisConnectionFactory);
        userInfo = UserInfo.builder()
                .userId("610780341698822144")
                .username(RandomGenerator.generateFullName())
                .isMasterDevice(false)
                .build();
    }

    @Test
    public void tokenTest() {
        userInfo.setDeviceId(IdGenerator.generateV7Id());
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("mobile", RandomGenerator.generatePhoneNum());
        String token = tokenManager.createToken(userInfo, claims, 1, TimeUnit.HOURS);
        Long tokenExpire = tokenManager.getTokenExpire(token, TimeUnit.MINUTES).get();
        boolean validateToken = tokenManager.validateToken(token);
        String refreshToken = tokenManager.refreshToken(token).get();
        UserInfo parsedUserInfo = tokenManager.parseUserInfo(refreshToken).get();
        Object mobile = tokenManager.parseClaim(refreshToken, "mobile").get();
        Long refreshTokenExpire = tokenManager.getTokenExpire(refreshToken, TimeUnit.MINUTES).get();
        Set<String> userTokens = tokenManager.getUserTokens(userInfo);

        log("tokenTest", token, tokenExpire, validateToken, refreshToken, parsedUserInfo, mobile, refreshTokenExpire, userTokens);
    }

    @Test
    public void tokenTest2() {
        userInfo.setDeviceId("1f078f9a6cfb6524a83d6b8f79fdf7c9");
        String token = tokenManager.createToken(userInfo);
        tokenManager.invalidateToken(token);
    }

    @Test
    public void tokenTest3() {
        tokenManager.invalidateUserTokens(userInfo);
    }
}
