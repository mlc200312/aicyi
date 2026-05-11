package io.github.aicyi.midware.redis.jwt;

import io.github.aicyi.commons.codec.jwt.DefaultJwtTokenManager;
import io.github.aicyi.commons.codec.jwt.JwtTokenGenerator;
import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.core.token.JwtTokenManager;
import io.github.aicyi.commons.codec.jwt.JWTInfo;
import io.github.aicyi.commons.core.IJWTInfo;
import io.github.aicyi.commons.core.JsonCodec;
import io.github.aicyi.commons.core.token.AbstractTokenManager;
import io.github.aicyi.commons.core.token.TokenConfig;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.util.Assert;
import io.github.aicyi.commons.util.JsonUtils;
import io.github.aicyi.commons.util.jackson.JacksonTypeFactory;
import io.github.aicyi.midware.redis.EnhancedRedisTemplateFactory;
import io.github.aicyi.midware.redis.cache.RedisCacheFactory;
import io.github.aicyi.midware.redis.cache.RedisCacheManager;
import com.fasterxml.jackson.databind.JavaType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description Redis+Jwt Token管理实现
 * @date 15:48
 **/
public class RedisJwtTokenManager<V extends IJWTInfo> extends AbstractTokenManager<V> implements JwtTokenManager<V> {

    // 日志
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisJwtTokenManager.class);

    // JsonCodec
    private static final JsonCodec DEFAULT_JSON_CODEC = JsonUtils.getInstance();

    // 用户Token集合前缀
    private static final String USER_TOKENS_PREFIX = "user_tokens:";

    private final RedisTemplate<String, String> stringRedisTemplate;
    private final HashOperations<String, String, String> opsForHash;
    private final RedisCacheManager<V> redisCacheManager;


    public RedisJwtTokenManager(TokenConfig config, EnhancedRedisTemplateFactory enhancedRedisTemplateFactory, JavaType javaType) {
        super(config, new JwtTokenGenerator(config.getSigningKey(), config.getIssuer()));

        RedisCacheFactory redisCacheFactory = new RedisCacheFactory(enhancedRedisTemplateFactory);
        this.stringRedisTemplate = enhancedRedisTemplateFactory.getStringTemplate();
        this.opsForHash = stringRedisTemplate.opsForHash();
        this.redisCacheManager = redisCacheFactory.createCache(javaType);
    }

    public RedisJwtTokenManager(TokenConfig tokenConfig, EnhancedRedisTemplateFactory enhancedRedisTemplateFactory, Class<V> clazz) {
        this(tokenConfig, enhancedRedisTemplateFactory, JacksonTypeFactory.typeOf(clazz));
    }

    public RedisJwtTokenManager(TokenConfig tokenConfig, EnhancedRedisTemplateFactory enhancedRedisTemplateFactory) {
        this(tokenConfig, enhancedRedisTemplateFactory, JacksonTypeFactory.typeOf(JWTInfo.class));
    }

    @Override
    public String createToken(V value, Map<String, Object> claims, long timeout, TimeUnit unit) {
        Assert.notNull(value, "jwtInfo");
        Assert.notNull(value.getId(), "jwtInfo.id");
        Assert.notNull(value.getDeviceId(), "jwtInfo.deviceId");
        // 允许创建多Token
        if (config.isMultiTokenAllowed()) {
            // 获取Token列表
            Set<String> userTokens = getUserTokens(value);
            if (value.isMainDevice()) {
                // 踢出主设备
                invalidateOneToken(userTokens, true);
            }
            // 如果已存在Token数大于最大Token数，踢出其中一个Token
            if (userTokens.size() >= config.getMultiTokenCount()) {
                // 保留最新Token，踢出其中一个Token
                invalidateOneToken(userTokens, false);
            }
        } else {
            // 使用户的所有Token失效
            invalidateAllTokens(value);
        }
        // 自定义声明和默认声明组合
        Map<String, Object> enhancedClaims = new HashMap<>(claims);
        String json = DEFAULT_JSON_CODEC.toJson(value);
        Map<String, Object> addClaims = DEFAULT_JSON_CODEC.fromJsonMap(json, String.class, Object.class);
        enhancedClaims.putAll(addClaims);
        // 生成Token
        String id = value.getId() + ":" + value.getDeviceId();
        String token = tokenGenerator.generateToken(id, enhancedClaims, timeout, unit);
        // 存储用户Token
        String hashKey = buildTokenKey(value);
        opsForHash.put(hashKey, value.getDeviceId(), token);
        redisCacheManager.put(id, value, timeout, unit);
        return token;
    }

    @Override
    public boolean validateToken(String token) {
        Optional<String> opt = tokenGenerator.getId(token);
        if (opt.isPresent()) {
            String id = opt.get();
            return redisCacheManager.containsKey(id);
        }
        return false;
    }

    @Override
    public Optional<V> parseJwtInfo(String token) {
        Optional<String> opt = tokenGenerator.getId(token);
        if (opt.isPresent()) {
            String id = opt.get();
            V jwtInfo = redisCacheManager.get(id);
            return Optional.ofNullable(jwtInfo);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Long> getTokenExpire(String token, TimeUnit unit) {
        Optional<String> opt = tokenGenerator.getId(token);
        if (opt.isPresent()) {
            String id = opt.get();
            long expire = redisCacheManager.getExpire(id, unit);
            return Optional.of(expire);
        }
        return Optional.empty();
    }

    @Override
    public Set<String> getUserTokens(V value) {
        String hashKey = buildTokenKey(value);
        Map<String, String> entries = opsForHash.entries(hashKey);
        if (entries == null || entries.size() == 0) {
            return Collections.emptySet();
        }
        Set<String> userTokens = new HashSet<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            if (validateToken(entry.getValue())) {
                userTokens.add(entry.getValue());
            } else {
                invalidateToken(entry.getValue());
            }
        }
        return userTokens;
    }

    @Override
    public void invalidateToken(String token) {
        Optional<String> opt = tokenGenerator.getId(token);
        if (opt.isPresent()) {
            // 解析Token并获取用户
            Optional<V> parseJwtInfo = parseJwtInfo(token);
            if (parseJwtInfo.isPresent()) {
                V jwtInfo = parseJwtInfo.get();
                String hashKey = buildTokenKey(jwtInfo);
                opsForHash.delete(hashKey, jwtInfo.getDeviceId());
                LOGGER.info("id：{} uniqueName：{} deviceId【{}】 token invalidated.", jwtInfo.getId(), jwtInfo.getUniqueName(), jwtInfo.getDeviceId());
            }
            String id = opt.get();
            redisCacheManager.remove(id);
        }
    }

    @Override
    public void invalidateAllTokens(V value) {
        // 获取用户所有的Token
        Set<String> userTokens = getUserTokens(value);
        // 使Token失效
        userTokens.forEach(this::invalidateToken);
    }

    private String buildTokenKey(V jwtInfo) {
        return redisCacheManager.getCacheName() + ":" + USER_TOKENS_PREFIX + ":" + jwtInfo.getId();
    }

    /**
     * 保留最新Token，踢出该用户其他所有Token
     *
     * @param userTokens
     * @param isMasterDevice
     */
    protected void invalidateOneToken(Set<String> userTokens, boolean isMasterDevice) {
        Iterator<String> iterator = userTokens.iterator();
        while (iterator.hasNext()) {
            String token = iterator.next();
            Optional<V> parseJwtInfo = parseJwtInfo(token);
            if (parseJwtInfo.isPresent()) {
                V jwtInfo = parseJwtInfo.get();
                if (isMasterDevice == jwtInfo.isMainDevice()) {
                    invalidateToken(token);
                    iterator.remove();
                    break;
                }
            }
        }
    }
}
