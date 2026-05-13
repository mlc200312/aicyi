package io.github.aicyi.test.commons.core;

import io.github.aicyi.commons.core.token.TokenProvider;
import io.github.aicyi.commons.security.token.*;
import io.github.aicyi.commons.core.token.TokenCreateRequest;
import io.github.aicyi.commons.core.token.TokenService;
import io.github.aicyi.commons.util.Maps;
import io.github.aicyi.commons.util.id.IdUtils;
import io.github.aicyi.commons.util.jackson.JacksonJsonCodec;
import io.github.aicyi.example.domain.UserInfo;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.test.util.RandomGenerator;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 19:53
 **/
public class JwtTokenServiceImplTest extends BaseLoggerTest {
    private UserInfo userInfo;
    private TokenCreateRequest<UserInfo> request;
    private TokenService<String, UserInfo> tokenService;

    @Before
    @Override
    public void beforeTest() {
        userInfo = new UserInfo();
        userInfo.setId("610780341698822144");
        userInfo.setUniqueName(RandomGenerator.generateFullName());
        userInfo.setDeviceId(IdUtils.generateV7Id());
        userInfo.setMainDevice(false);
        userInfo.setNickname("nickname");
        userInfo.setMobile(RandomGenerator.generatePhoneNum());

        request = new TokenCreateRequest<>();
        request.setPrincipal(userInfo);
        request.setAttributes(Maps.of("phone", RandomGenerator.generatePhoneNum()).build());
        request.setTtl(1);
        request.setTimeUnit(TimeUnit.HOURS);

        TokenProvider<String> tokenProvider = new JwtTokenProvider("LcR6QUhqWrDqK1InQDKlpZuKx6X/ZgEISdFpKwO3i/E=", "test", "subject");

        tokenService = new JwtTokenServiceImpl<>(tokenProvider, JacksonJsonCodec.DEFAULT, UserInfo.class);
    }

    @Test
    public void test() {
        String token = tokenService.create(request);
        Long ttl = tokenService.getRemainingTtl(token, TimeUnit.MINUTES);
        assert ttl > 0;

        boolean isValid = tokenService.isValid(token);
        assert isValid == true;

        String refresh = tokenService.refresh(token);
        UserInfo principal = tokenService.parsePrincipal(refresh);
        assert principal.getId().equals(userInfo.getId());

        Map<String, Object> attributes = tokenService.parseAttributes(refresh);
        assert attributes.containsKey("phone");

        String phone = tokenService.getAttribute(refresh, "phone");
        assert StringUtils.isNotBlank(phone);

        Long refreshTtl = tokenService.getRemainingTtl(refresh, TimeUnit.MINUTES);
        assert refreshTtl > 0 && refreshTtl > ttl;

        Set<String> tokens = tokenService.getTokens(userInfo);
        assert tokens.contains(refresh);

        log(token, refresh, principal, phone);
    }

    @Test
    public void tokenTest2() {
        // 模拟多设备登录

        String token1 = tokenService.create(request);

        String token2 = tokenService.create(request);

        String token3 = tokenService.create(request);

        UserInfo principal = tokenService.parsePrincipal(token1);

        Set<String> tokens = tokenService.getTokens(principal);
        assert tokens.contains(token1) && tokens.contains(token2) && tokens.contains(token3);

        log(tokens);
    }

    @Test
    public void tokenTest3() {
        tokenService.revokeAll(userInfo);
    }
}
