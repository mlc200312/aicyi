package com.aicyiframework.data.redis.jwt;

import com.aichuangyi.commons.lang.JsonConverter;
import com.aichuangyi.commons.core.token.DefaultTokenManager;
import com.aichuangyi.commons.core.token.TokenConfig;
import com.aichuangyi.commons.core.token.TokenManager;
import com.aichuangyi.commons.lang.UserInfo;
import com.aichuangyi.commons.logging.Logger;
import com.aichuangyi.commons.logging.LoggerFactory;
import com.aichuangyi.commons.core.jwt.JwtTokenGenerator;
import com.aichuangyi.commons.util.Assert;
import com.aichuangyi.commons.util.json.JacksonConverter;
import com.aichuangyi.commons.util.json.JacksonHelper;
import com.aicyiframework.data.redis.EnhancedRedisTemplateFactory;
import com.aicyiframework.data.redis.cache.RedisCacheFactory;
import com.aicyiframework.data.redis.cache.RedisCacheManager;
import com.fasterxml.jackson.databind.JavaType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 15:48
 **/
public class RedisJwtTokenManager<U extends UserInfo> extends DefaultTokenManager<U> implements TokenManager<String, U> {

    // 日志
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisJwtTokenManager.class);

    // 用户Token集合前缀
    private static final String USER_TOKENS_PREFIX = "user_tokens:";

    private final RedisTemplate<String, String> stringRedisTemplate;
    private final HashOperations<String, String, String> opsForHash;
    private final RedisCacheManager<U> redisCacheManager;
    private final JsonConverter jsonConverter;

    public RedisJwtTokenManager(TokenConfig tokenConfig, RedisConnectionFactory redisConnectionFactory, JacksonConverter jsonConverter, JavaType javaType) {
        super(tokenConfig, new JwtTokenGenerator(tokenConfig.getSigningKey(), tokenConfig.getIssuer()));
        EnhancedRedisTemplateFactory enhancedRedisTemplateFactory = new EnhancedRedisTemplateFactory(redisConnectionFactory, jsonConverter);
        RedisCacheFactory redisCacheFactory = new RedisCacheFactory(redisConnectionFactory, jsonConverter);
        this.stringRedisTemplate = enhancedRedisTemplateFactory.getStringTemplate();
        this.opsForHash = this.stringRedisTemplate.opsForHash();
        this.redisCacheManager = redisCacheFactory.createCache(javaType);
        this.jsonConverter = jsonConverter;
    }

    public RedisJwtTokenManager(TokenConfig tokenConfig, RedisConnectionFactory redisConnectionFactory, JavaType javaType) {
        this(tokenConfig, redisConnectionFactory, JacksonConverter.DEFAULT_SIMPLE_CONVERTER, javaType);
    }

    public RedisJwtTokenManager(TokenConfig tokenConfig, RedisConnectionFactory redisConnectionFactory) {
        this(tokenConfig, redisConnectionFactory, JacksonHelper.getType(UserInfo.class));
    }


    @Override
    public String createToken(U userInfo, Map<String, Object> claims, long timeout, TimeUnit unit) {
        Assert.notNull(userInfo, "userInfo");
        Assert.notNull(userInfo.getUserId(), "userInfo.userId");
        Assert.notNull(userInfo.getDeviceId(), "userInfo.deviceId");

        // 允许创建多Token
        if (config.isMultiTokenAllowed()) {

            // 获取Token列表
            Set<String> userTokens = getUserTokens(userInfo);
            if (userInfo.isMasterDevice()) {

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
            invalidateAllTokens(userInfo);
        }

        // 自定义声明和默认声明组合
        Map<String, Object> enhancedClaims = new HashMap<>(claims);
        String json = jsonConverter.toJson(userInfo);
        Map<String, Object> addClaims = jsonConverter.parseMap(json, Object.class);
        enhancedClaims.putAll(addClaims);

        // 生成Token
        String id = userInfo.getUserId() + ":" + userInfo.getDeviceId();
        String token = tokenGenerator.generateToken(id, enhancedClaims, timeout, unit);

        // 存储用户Token
        String hashKey = buildTokenKey(userInfo);
        opsForHash.put(hashKey, userInfo.getDeviceId(), token);
        redisCacheManager.put(id, userInfo, timeout, unit);

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
    public Optional<U> parseUserInfo(String token) {
        Optional<String> opt = tokenGenerator.getId(token);
        if (opt.isPresent()) {
            String id = opt.get();
            U userInfo = redisCacheManager.get(id);
            return Optional.ofNullable(userInfo);
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
    public Set<String> getUserTokens(U userInfo) {
        String hashKey = buildTokenKey(userInfo);
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
            Optional<U> parseUserInfo = parseUserInfo(token);
            if (parseUserInfo.isPresent()) {
                U userInfo = parseUserInfo.get();
                String hashKey = buildTokenKey(userInfo);
                opsForHash.delete(hashKey, userInfo.getDeviceId());
                LOGGER.info("ID：{} username：{} deviceId【{}】 token invalidated.", userInfo.getUserId(), userInfo.getUsername(), userInfo.getDeviceId());
            }
            String id = opt.get();
            redisCacheManager.remove(id);
        }
    }

    @Override
    public void invalidateAllTokens(U userInfo) {
        // 获取用户所有的Token
        Set<String> userTokens = getUserTokens(userInfo);

        // 使Token失效
        userTokens.forEach(this::invalidateToken);
    }

    private String buildTokenKey(U userInfo) {
        return redisCacheManager.getCacheName() + ":" + USER_TOKENS_PREFIX + ":" + userInfo.getUserId();
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
            Optional<U> parseUserInfo = parseUserInfo(token);
            if (parseUserInfo.isPresent()) {
                U userInfo = parseUserInfo.get();
                if (isMasterDevice == userInfo.isMasterDevice()) {
                    invalidateToken(token);
                    iterator.remove();
                    break;
                }
            }
        }
    }
}
