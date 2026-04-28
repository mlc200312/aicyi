package io.github.aicyi.test.commons.core;

import io.github.aicyi.commons.core.jwt.JwtTokenManager;
import io.github.aicyi.commons.core.token.DefaultTokenConfig;
import io.github.aicyi.commons.core.token.TokenConfig;
import io.github.aicyi.commons.core.token.TokenManager;
import io.github.aicyi.commons.core.jwt.JWTInfo;
import io.github.aicyi.commons.util.id.IdGenerator;
import io.github.aicyi.commons.util.jackson.JacksonTypeFactory;
import io.github.aicyi.example.domain.UserInfo;
import io.github.aicyi.test.util.BaseLoggerTest;
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
    private TokenManager<String, JWTInfo> tokenManager;

    @Before
    @Override
    public void beforeTest() {
        userInfo = new UserInfo();
        userInfo.setId("610780341698822144");
        userInfo.setUniqueName(RandomGenerator.generateFullName());
        userInfo.setMainDevice(false);
        userInfo.setNickname("nickname");
        userInfo.setMobile(RandomGenerator.generatePhoneNum());

        config = DefaultTokenConfig.builder()
                .signingKey("LcR6QUhqWrDqK1InQDKlpZuKx6X/ZgEISdFpKwO3i/E=")
                .multiTokenAllowed(true)
                .build();

        tokenManager = new JwtTokenManager<>(config, JacksonTypeFactory.typeOf(UserInfo.class));
    }

    @Test
    public void test() {
        userInfo.setId(null);
        userInfo.setDeviceId(IdGenerator.generateV7Id());
        HashMap<String, Object> claims = new HashMap<>();
        String mobile = RandomGenerator.generatePhoneNum();
        claims.put("testMobile", mobile);
        String token = tokenManager.createToken(userInfo, claims, 60, TimeUnit.MINUTES);
        Long tokenExpire = tokenManager.getTokenExpire(token, TimeUnit.SECONDS).get();
        assert tokenExpire <= config.getDefaultExpire(TimeUnit.SECONDS);

        boolean validateToken = tokenManager.validateToken(token);
        assert validateToken == true;

        String refreshToken = tokenManager.refreshToken(token).get();
        JWTInfo parsedJWTInfo = tokenManager.parseJwtInfo(refreshToken).get();
        Object getMobile = tokenManager.parseClaim(refreshToken, "testMobile").get();
        assert getMobile.equals(mobile);

        Long refreshTokenExpire = tokenManager.getTokenExpire(refreshToken, TimeUnit.MINUTES).get();
        assert refreshTokenExpire <= config.getRefreshWindow(TimeUnit.MINUTES);

        Set<String> userTokens = tokenManager.getUserTokens(userInfo);
        assert userTokens.size() == 0;

        log(token, tokenExpire, validateToken, refreshToken, parsedJWTInfo, getMobile, refreshTokenExpire, userTokens);
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
