package com.aichuangyi.demo.redis;

import com.aicyiframework.core.token.TokenConfig;
import com.aicyiframework.core.token.TokenManager;
import com.aichuangyi.commons.util.id.IdGenerator;
import com.aichuangyi.commons.util.json.JacksonHelper;
import com.aichuangyi.demo.AicyiDemoApplication;
import com.aichuangyi.commons.lang.UserInfo;
import com.aicyiframework.core.token.DefaultTokenConfig;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aichuangyi.test.domain.User;
import com.aichuangyi.test.util.DataSource;
import com.aichuangyi.test.util.RandomGenerator;
import com.aicyiframework.redis.jwt.RedisJwtTokenManager;
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
public class RedisJwtTokenManagerTest extends BaseLoggerTest {
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    private UserInfo userInfo;
    private TokenConfig config;
    private TokenManager<String, UserInfo> tokenManager;

    @Before
    public void before() {
        userInfo = DataSource.getUser();
        userInfo.setUserId("610780341698822144");
        userInfo.setMasterDevice(false);

        config = DefaultTokenConfig.builder()
                .signingKey("LcR6QUhqWrDqK1InQDKlpZuKx6X/ZgEISdFpKwO3i/E=")
                .multiTokenAllowed(true)
                .build();

        tokenManager = new RedisJwtTokenManager(config, redisConnectionFactory, JacksonHelper.getType(User.class));
    }

    @Test
    public void test() {
        userInfo.setDeviceId(IdGenerator.generateV7Id());
        HashMap<String, Object> claims = new HashMap<>();
        String mobile = RandomGenerator.generatePhoneNum();
        claims.put("testMobile", mobile);
        String token = tokenManager.createToken(userInfo, claims, 1, TimeUnit.HOURS);
        Long tokenExpire = tokenManager.getTokenExpire(token, TimeUnit.MINUTES).get();

        assert tokenExpire <= config.getDefaultExpire(TimeUnit.MINUTES);

        boolean validateToken = tokenManager.validateToken(token);

        assert validateToken == true;

        String refreshToken = tokenManager.refreshToken(token).get();
        UserInfo parsedUserInfo = tokenManager.parseUserInfo(refreshToken).get();
        Object getMobile = tokenManager.parseClaim(refreshToken, "testMobile").get();

        assert getMobile.equals(mobile);

        Long refreshTokenExpire = tokenManager.getTokenExpire(refreshToken, TimeUnit.MINUTES).get();

        assert refreshTokenExpire <= config.getRefreshWindow(TimeUnit.MINUTES);

        Set<String> userTokens = tokenManager.getUserTokens(userInfo);

        log("test", token, tokenExpire, validateToken, refreshToken, parsedUserInfo, getMobile, refreshTokenExpire, userTokens.size());
    }

    @Test
    public void tokenTest2() {
        tokenManager.invalidateToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhaWNodWFuZ3lpIiwiaXNzIjoiZGVmYXVsdC1pc3N1ZXIiLCJqdGkiOiI2MTA3ODAzNDE2OTg4MjIxNDQ6MWYwN2U2ZTkzMzdhNjhiODg4ZjE2MTI5NjRiZDlmMmUiLCJiaXJ0aGRheSI6IjIwMjUvMDgvMjEiLCJtYXN0ZXJEZXZpY2UiOmZhbHNlLCJ0ZXN0TW9iaWxlIjoiMTcwMTg5NDcxNTEiLCJpZENhcmQiOiIxZjA3ZTZlOTMzNWQ2M2Y3ODhmMTYxMjk2NGJkOWYyZSIsIm1vYmlsZSI6IjE0NTg0ODA2NjA0IiwidXNlcklkIjoiNjEwNzgwMzQxNjk4ODIyMTQ0IiwiZGV2aWNlSWQiOiIxZjA3ZTZlOTMzN2E2OGI4ODhmMTYxMjk2NGJkOWYyZSIsImlkIjo2MTM2NjA5OTcwODg5Njg3MDQsImV4cCI6MTc1NTc2OTE4MywiaWF0IjoxNzU1NzY3MzgzLCJhZ2UiOjM4LCJ1c2VybmFtZSI6Iua2guiDnCIsImdlbmRlclR5cGUiOjJ9.GzzU6AcYVeorDSIoHpEttPUkM_oJRRJ-bBWOU6nRoQo");
    }

    @Test
    public void tokenTest3() {
        tokenManager.invalidateAllTokens(userInfo);
    }
}
