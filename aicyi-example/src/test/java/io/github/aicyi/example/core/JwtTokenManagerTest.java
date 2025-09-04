package io.github.aicyi.example.core;

import io.github.aicyi.commons.core.jwt.JwtTokenManager;
import io.github.aicyi.commons.core.token.DefaultTokenConfig;
import io.github.aicyi.commons.core.token.TokenConfig;
import io.github.aicyi.commons.core.token.TokenManager;
import io.github.aicyi.commons.lang.UserInfo;
import io.github.aicyi.commons.util.id.IdGenerator;
import io.github.aicyi.commons.util.json.JacksonHelper;
import io.github.aicyi.test.domain.BaseLoggerTest;
import io.github.aicyi.test.domain.User;
import io.github.aicyi.test.util.DataSource;
import io.github.aicyi.test.util.RandomGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 19:53
 **/
public class JwtTokenManagerTest extends BaseLoggerTest {
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

        tokenManager = new JwtTokenManager<>(config, JacksonHelper.getType(User.class));
    }

    @Test
    public void test() {
        userInfo.setUserId(null);
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

        assert userTokens.size() == 0;

        log("test", token, tokenExpire, validateToken, refreshToken, parsedUserInfo, getMobile, refreshTokenExpire, userTokens);
    }

    @Test
    public void tokenTest2() {
        userInfo.setDeviceId("1f078f9a6cfb6524a83d6b8f79fdf7c9");
        String token = tokenManager.createToken(userInfo);
        tokenManager.invalidateToken(token);
    }

    @Test
    public void tokenTest3() {
        tokenManager.invalidateAllTokens(userInfo);
    }
}
