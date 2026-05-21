package io.github.aicyi.midware.redis.token;

import io.github.aicyi.commons.security.token.jwt.IJWTInfo;
import io.github.aicyi.commons.security.token.AbstractAuthenticationTokenService;
import io.github.aicyi.commons.security.token.TokenSession;
import io.github.aicyi.commons.lang.exception.TokenInvalidException;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 刷新Token服务
 * @date 14:55
 **/
public class JwtRefreshAuthenticationTokenService<P extends IJWTInfo> extends AbstractAuthenticationTokenService<P> {

    public JwtRefreshAuthenticationTokenService(AuthenticationConfig config, StringRedisTemplate redisTemplate, Class<? extends P> principalType) {
        super(
                new MultiRedisTokenServiceImpl<>(
                        redisTemplate,
                        principalType,
                        config.getRefreshTokenTtl(),
                        config.getRefreshTokenTimeUnit(),
                        config.isMultiTokenAllowed(),
                        config.getMultiTokenCount()
                ),
                principalType,
                config.getSecretKey(),
                config.getIssuer(),
                config.getIssuer(),
                config.getRefreshTokenTtl(),
                config.getRefreshTokenTimeUnit(),
                config.getAccessTokenTtl(),
                config.getAccessTokenTimeUnit()
        );
    }
}
