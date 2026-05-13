package io.github.aicyi.test.commons.core;

import io.github.aicyi.commons.security.token.JwtTokenProvider;
import io.github.aicyi.commons.util.DateUtils;
import io.github.aicyi.commons.util.id.IdUtils;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.collections4.CollectionUtils;
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
public class JwtTokenProviderTest extends BaseLoggerTest {
    private JwtTokenProvider tokenProvider;
    private String userId;
    private String expiredToken;
    private String token;

    @Before
    @Override
    public void beforeTest() {
        userId = IdUtils.generateId() + "";
        tokenProvider = new JwtTokenProvider(Keys.secretKeyFor(SignatureAlgorithm.HS256), "test", "subject");
    }

    @Test
    public void test() {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("jti", IdUtils.generateV7Id());
        claims.put("userId", userId);

        expiredToken = tokenProvider.create(userId, new HashMap<>(), -1 * 3600 * 1000L, TimeUnit.MILLISECONDS);
        token = tokenProvider.create(userId, claims, 2 * 3600 * 1000L, TimeUnit.MILLISECONDS);

        boolean valid0 = tokenProvider.isValid(expiredToken);
        boolean valid1 = tokenProvider.isValid(token);

        Map<String, Object> parseClaims = tokenProvider.parseClaims(token);
        assert parseClaims.containsKey("jti") && parseClaims.containsKey("userId");

        Map<String, Object> attributes1 = tokenProvider.getAttributes(expiredToken);
        assert MapUtils.isEmpty(attributes1);

        Map<String, Object> attributes2 = tokenProvider.getAttributes(token);
        assert !attributes2.containsKey("jti") && attributes2.containsKey("userId");

        String tokenId = tokenProvider.getTokenId(token);
        Date expiration = tokenProvider.getExpiration(token);
        String userId = tokenProvider.getAttribute(token, "userId");

        assert !valid0 && valid1 && userId.equals(this.userId);

        log(expiredToken, token, parseClaims, attributes2, tokenId, DateUtils.formatDate(expiration), userId);
    }
}
