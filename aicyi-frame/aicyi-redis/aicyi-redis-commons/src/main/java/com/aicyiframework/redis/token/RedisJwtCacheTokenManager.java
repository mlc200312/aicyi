package com.aicyiframework.redis.token;

import com.aichuangyi.commons.core.token.AbstractCacheTokenManager;
import com.aichuangyi.commons.lang.UserInfo;
import com.aichuangyi.commons.logging.Logger;
import com.aichuangyi.commons.logging.LoggerFactory;
import com.aichuangyi.commons.security.jwt.JwtTokenGenerator;
import com.aichuangyi.commons.util.Assert;
import com.aichuangyi.commons.util.json.JacksonConverter;
import com.aichuangyi.commons.core.api.JsonConverter;
import com.aichuangyi.commons.core.token.TokenConfig;
import com.aichuangyi.commons.core.token.TokenGenerator;
import com.aichuangyi.commons.core.token.TokenManager;
import com.aicyiframework.redis.EnhancedRedisTemplateFactory;
import com.aicyiframework.redis.cache.RedisCacheFactory;
import com.aicyiframework.redis.cache.RedisCacheManager;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description Redis缓存Token管理
 * @date 23:33
 **/
public class RedisJwtCacheTokenManager<U extends UserInfo> extends AbstractCacheTokenManager<U> implements TokenManager<String, U> {

    // 日志
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisJwtCacheTokenManager.class);
    // Json序列化
    private static final JacksonConverter INSTANCE = new JacksonConverter();
    // 用户Token集合前缀
    private static final String USER_TOKENS_PREFIX = "user_tokens:";

    static {
        INSTANCE.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final TokenGenerator<String> tokenGenerator;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final HashOperations<String, String, String> opsForHash;
    private final RedisCacheManager<U> redisCacheManager;
    private JsonConverter jsonConverter;

    public RedisJwtCacheTokenManager(TokenConfig tokenConfig, RedisConnectionFactory redisConnectionFactory, JsonConverter jsonConverter) {
        super(tokenConfig);
        EnhancedRedisTemplateFactory enhancedRedisTemplateFactory = new EnhancedRedisTemplateFactory(redisConnectionFactory);
        RedisCacheFactory redisCacheFactory = new RedisCacheFactory(redisConnectionFactory);
        this.tokenGenerator = new JwtTokenGenerator(tokenConfig.getIssuer(), tokenConfig.getSigningKey());
        this.stringRedisTemplate = enhancedRedisTemplateFactory.getStringTemplate();
        this.opsForHash = this.stringRedisTemplate.opsForHash();
        this.redisCacheManager = (RedisCacheManager<U>) redisCacheFactory.createCache(UserInfo.class);
        this.jsonConverter = jsonConverter;
    }

    public RedisJwtCacheTokenManager(TokenConfig tokenConfig, RedisConnectionFactory redisConnectionFactory) {
        this(tokenConfig, redisConnectionFactory, INSTANCE);
    }

    @Override
    protected TokenGenerator<String> getTokenGenerator() {
        return this.tokenGenerator;
    }

    @Override
    public U getCache(String token) {

        // 获取用户
        String id = tokenGenerator.getId(token).get();
        return redisCacheManager.get(id);
    }

    @Override
    public boolean hashCache(String token) {

        // 检查Token是否存在
        String id = tokenGenerator.getId(token).get();
        return redisCacheManager.containsKey(id);
    }

    @Override
    public long getCacheExpire(String token, TimeUnit unit) {

        // 获取Token的有效期
        String id = tokenGenerator.getId(token).get();
        return redisCacheManager.getExpire(id, unit);
    }

    @Override
    public void removeCache(String token) {

        // 删除Token
        String id = tokenGenerator.getId(token).get();
        redisCacheManager.remove(id);
    }

    @Override
    public String createToken(U userInfo, Map<String, Object> claims, long timeout, TimeUnit unit) {
        Assert.notNull(userInfo, "userInfo");
        Assert.notNull(claims, "claims");

        // 允许创建多Token
        String hashKey = buildUserTokenKey(userInfo);
        if (config.isMultiTokenAllowed()) {

            // 获取有效的Token列表
            Set<String> userTokens = getUserTokens(userInfo);

            if (userInfo.isMasterDevice()) {

                // 踢出主设备
                invalidateOtherToken(userTokens, userInfo.isMasterDevice());

            }

            // 获取用户设备ID列表
            Set<String> deviceIds = opsForHash.keys(hashKey);

            // 如果不是当前设备且Token数量大于允许最大计数，踢出其中一个Token
            if (!deviceIds.contains(userInfo.getDeviceId()) && userTokens.size() >= config.getMultiTokenCount()) {

                // 保留最新Token，踢出该用户其他一个Token
                invalidateOtherToken(userTokens, false);

            }
        } else {

            // 使用户的所有Token失效
            invalidateUserTokens(userInfo);

        }

        String json = jsonConverter.toJson(userInfo);
        Map<String, Object> addClaims = jsonConverter.parseMap(json, Object.class);
        claims.putAll(addClaims);

        // 存储用户Token
        String token = tokenGenerator.generateToken(userInfo.getUserId(), claims, timeout, unit);
        opsForHash.put(hashKey, userInfo.getDeviceId(), token);

        // 存储Token用户
        String id = tokenGenerator.getId(token).get();
        redisCacheManager.put(id, userInfo, timeout, unit);

        return token;
    }

    @Override
    public Set<String> getUserTokens(U userInfo) {

        // 获取Token的Hash集合
        String hashKey = buildUserTokenKey(userInfo);
        Map<String, String> tokenMap = opsForHash.entries(hashKey);

        if (tokenMap == null || tokenMap.isEmpty()) {
            return Collections.emptySet();
        }

        // 过滤掉已失效的Token
        Set<String> userTokens = new HashSet<>();
        for (Map.Entry<String, String> entry : tokenMap.entrySet()) {

            // 设备ID
            String key = entry.getKey();

            // Token
            String value = entry.getValue();

            if (validateToken(value)) {

                // 添加有效Token
                userTokens.add(value);

            } else {

                // 自动清理无效Token
                opsForHash.delete(hashKey, key);

            }
        }

        return userTokens;
    }

    @Override
    public void invalidateToken(String token) {
        if (validateToken(token)) {

            // 解析Token并获取用户
            Optional<U> parseUserInfo = parseUserInfo(token);

            if (parseUserInfo.isPresent()) {

                // 删除Hash集合里的Token
                U userInfo = parseUserInfo.get();
                String hashKey = buildUserTokenKey(userInfo);
                opsForHash.delete(hashKey, userInfo.getDeviceId());

            }

            // 删除Token
            removeCache(token);
        }
    }

    /**
     * 保留最新Token，踢出该用户其他所有Token
     *
     * @param userTokens
     * @param isMasterDevice
     */
    protected void invalidateOtherToken(Set<String> userTokens, boolean isMasterDevice) {
        Iterator<String> iterator = userTokens.iterator();
        while (iterator.hasNext()) {
            String token = iterator.next();
            Optional<U> opt = parseUserInfo(token);
            if (opt.isPresent()) {
                U u = opt.get();
                if (isMasterDevice == u.isMasterDevice()) {
                    invalidateToken(token);
                    iterator.remove();
                    LOGGER.debug("踢出Token，设备号ID:{}.", u.getDeviceId());
                    break;
                }
            }
        }
    }

    private String buildUserTokenKey(UserInfo userInfo) {
        return redisCacheManager.getCacheName() + ":" + USER_TOKENS_PREFIX + ":" + userInfo.getUserId();
    }
}
