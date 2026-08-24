package io.github.aicyi.midware.redis.token;

import io.github.aicyi.commons.core.token.IJWTInfo;
import io.github.aicyi.commons.core.token.AbstractAuthenticationTokenService;
import io.github.aicyi.commons.security.token.jwt.JwtTokenProvider;
import io.github.aicyi.commons.util.id.UUIDUtils;
import io.github.aicyi.commons.util.codec.JsonCodecPrincipalSerializer;
import org.springframework.data.redis.core.StringRedisTemplate;

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
                new JwtTokenProvider(
                        config.getSecretKey(),
                        config.getIssuer(),
                        config.getSubject()
                ),
                new JsonCodecPrincipalSerializer<>(principalType),
                config.getRefreshTokenTtl(),
                config.getRefreshTokenTimeUnit(),
                config.getAccessTokenTtl(),
                config.getAccessTokenTimeUnit()
        );
    }

    @Override
    protected String generateTokenId() {
        return UUIDUtils.generateV7Id();
    }
}
