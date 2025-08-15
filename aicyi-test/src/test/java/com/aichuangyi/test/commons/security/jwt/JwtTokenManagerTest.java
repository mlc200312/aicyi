package com.aichuangyi.test.commons.security.jwt;

import com.aichuangyi.commons.core.token.DefaultTokenConfig;
import com.aichuangyi.commons.core.token.TokenConfig;
import com.aichuangyi.commons.core.token.TokenManager;
import com.aichuangyi.commons.lang.UserInfo;
import com.aichuangyi.commons.security.SecretKeyUtils;
import com.aichuangyi.commons.security.jwt.JwtTokenManager;
import com.aichuangyi.commons.util.id.IdGenerator;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aichuangyi.test.util.RandomGenerator;
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
        userInfo = UserInfo.builder()
                .userId("610780341698822144")
                .username(RandomGenerator.generateFullName())
                .isMasterDevice(false)
                .build();
        String singingKey = SecretKeyUtils.randomSecretKeyStr();
        config = DefaultTokenConfig.builder()
                .signingKey("LcR6QUhqWrDqK1InQDKlpZuKx6X/ZgEISdFpKwO3i/E=")
                .multiTokenAllowed(true)
                .build();
        System.out.println(singingKey);
        tokenManager = new JwtTokenManager<>(config);
    }

    @Test
    public void test() {
        userInfo.setDeviceId(IdGenerator.generateV7Id());
        HashMap<String, Object> claims = new HashMap<>();
        String mobile = RandomGenerator.generatePhoneNum();
        claims.put("mobile", mobile);
        String token = tokenManager.createToken(userInfo, claims, 1, TimeUnit.HOURS);
        Long tokenExpire = tokenManager.getTokenExpire(token, TimeUnit.MINUTES).get();

        assert tokenExpire <= config.getDefaultExpire(TimeUnit.MINUTES);

        boolean validateToken = tokenManager.validateToken(token);

        assert validateToken == true;

        String refreshToken = tokenManager.refreshToken(token).get();
        UserInfo parsedUserInfo = tokenManager.parseUserInfo(refreshToken).get();
        Object getMobile = tokenManager.parseClaim(refreshToken, "mobile").get();

        assert getMobile.equals(mobile);

        Long refreshTokenExpire = tokenManager.getTokenExpire(refreshToken, TimeUnit.MINUTES).get();

        assert refreshTokenExpire <= config.getRefreshWindow(TimeUnit.MINUTES);

        Set<String> userTokens = tokenManager.getUserTokens(userInfo);

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
        tokenManager.invalidateUserTokens(userInfo);
    }
}
