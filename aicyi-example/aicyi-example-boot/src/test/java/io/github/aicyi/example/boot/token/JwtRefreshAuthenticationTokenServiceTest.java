package io.github.aicyi.example.boot.token;

import io.github.aicyi.commons.core.BeanMapper;
import io.github.aicyi.commons.core.token.*;
import io.github.aicyi.commons.security.token.JwtRefreshAuthenticationTokenService;
import io.github.aicyi.commons.security.token.jwt.DefultJwtPrincipalSerializer;
import io.github.aicyi.commons.security.token.jwt.JwtTokenProvider;
import io.github.aicyi.commons.util.JsonUtils;
import io.github.aicyi.commons.util.Maps;
import io.github.aicyi.commons.util.id.IdUtils;
import io.github.aicyi.example.boot.AicyiExampleApplication;
import io.github.aicyi.example.domain.UserBean;
import io.github.aicyi.example.domain.UserInfo;
import io.github.aicyi.example.domain.entity.base.User;
import io.github.aicyi.midware.redis.EnhancedRedisTemplateFactory;
import io.github.aicyi.midware.redis.token.MultiRedisTokenServiceImpl;
import io.github.aicyi.test.util.BaseLoggerTest;
import io.github.aicyi.test.util.DataSource;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.crypto.SecretKey;
import java.util.Map;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 17:29
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AicyiExampleApplication.class)
public class JwtRefreshAuthenticationTokenServiceTest extends BaseLoggerTest {

    @Autowired
    private EnhancedRedisTemplateFactory factory;

    @Autowired
    private BeanMapper beanMapper;

    private AuthenticationTokenService<UserInfo> authenticationTokenService;

    @Before
    @Override
    public void beforeTest() {

        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        JwtProvider<String> jwtProvider = new JwtTokenProvider(secretKey, "issuer", "subject");

        TokenService<String, UserInfo> tokenService = new MultiRedisTokenServiceImpl<>(factory, UserInfo.class);

        JwtPrincipalSerializer<UserInfo> serializer = new DefultJwtPrincipalSerializer<>(JsonUtils.getInstance(), UserInfo.class);

        authenticationTokenService = new JwtRefreshAuthenticationTokenService<>(jwtProvider, tokenService, serializer);
    }

    @Test
    @Override
    public void test() {

        UserBean userBean = DataSource.getUser();

        User user = beanMapper.map(userBean, User.class);

        UserInfo userInfo = UserInfo.of(user, IdUtils.generateV7Id());

        Map<String, Object> attributes = Maps.of("test", "test").build();

        TokenPair token = authenticationTokenService.createToken(userInfo, attributes);

        TokenPair refreshToken = authenticationTokenService.refreshToken(token.getRefreshToken());

        boolean validated = authenticationTokenService.validateAccessToken(token.getAccessToken());
        assert validated;

        UserInfo principal = authenticationTokenService.parsePrincipal(token.getAccessToken());
        assert principal != null;

        attributes = authenticationTokenService.getAttributes(token.getAccessToken());

        authenticationTokenService.revokeToken(token.getRefreshToken());

        try {
            authenticationTokenService.refreshToken(token.getRefreshToken());
            assert false;
        } catch (Exception e) {
            assert true;
        }

        log(token, refreshToken, principal, attributes);
    }
}
