package io.github.aicyi.midware.redis.token;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.security.token.jwt.IJWTInfo;
import io.github.aicyi.commons.core.token.TokenCreateRequest;
import io.github.aicyi.commons.lang.Assert;
import io.github.aicyi.commons.logging.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description Redis多Token管理实现
 * @date 17:07
 **/
public class MultiRedisTokenServiceImpl<P extends IJWTInfo> extends RedisTokenServiceImpl<P> implements RedisTokenService<P> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 用户Token集合前缀
     */
    private static final String USER_TOKENS_KEY_PREFIX = "security:user:tokens:";

    /**
     * 原子裁剪脚本：保留 score 最新的 maxTokens 个成员，返回被移除的旧成员
     */
    private static final DefaultRedisScript<List> TRIM_SCRIPT;

    static {
        TRIM_SCRIPT = new DefaultRedisScript<>();
        TRIM_SCRIPT.setResultType(List.class);
        TRIM_SCRIPT.setScriptText(
                "local stop = -(tonumber(ARGV[1]) + 1) " +
                        "local members = redis.call('ZRANGE', KEYS[1], 0, stop) " +
                        "if #members > 0 then " +
                        "   redis.call('ZREM', KEYS[1], unpack(members)) " +
                        "end " +
                        "return members"
        );
    }

    /**
     * 是否允许多设备登录
     */
    private boolean isMultiTokenAllowed = false;

    /**
     * 多设备登录数量
     */
    private int multiTokenCount = 1;

    public MultiRedisTokenServiceImpl(StringRedisTemplate redisTemplate, Class<? extends P> principalType, long refreshTtl, TimeUnit refreshTimeUnit, boolean isMultiTokenAllowed, int multiTokenCount) {
        super(redisTemplate, principalType, refreshTtl, refreshTimeUnit);
        this.isMultiTokenAllowed = isMultiTokenAllowed;
        this.multiTokenCount = multiTokenCount;
    }

    public MultiRedisTokenServiceImpl(StringRedisTemplate redisTemplate, Class<? extends P> principalType, long refreshTtl, TimeUnit refreshTimeUnit) {
        super(redisTemplate, principalType, refreshTtl, refreshTimeUnit);
    }

    public boolean isMultiTokenAllowed() {
        return isMultiTokenAllowed;
    }

    public void setMultiTokenAllowed(boolean multiTokenAllowed) {
        isMultiTokenAllowed = multiTokenAllowed;
    }

    public int getMultiTokenCount() {
        return multiTokenCount;
    }

    public void setMultiTokenCount(int multiTokenCount) {
        this.multiTokenCount = multiTokenCount;
    }

    @Override
    protected String getPrincipalId(P principal) {
        return USER_TOKENS_KEY_PREFIX + principal.getId();
    }

    @Override
    protected void cachePrincipalToken(P principal, String token, long ttlSeconds) {

        String principalId = getPrincipalId(principal);

        long now = System.currentTimeMillis();

        redisTemplate.opsForZSet().add(principalId, token, now);

        redisTemplate.expire(principalId, Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public String create(TokenCreateRequest<P> request) {

        Assert.notNull(request, "tokenCreateRequest");

        P principal = request.getPrincipal();

        Assert.notNull(principal, "principal");

        String principalId = getPrincipalId(principal);

        removeExpiredTokens(principalId);

        String token = super.create(request);

        // 写入后再原子裁剪，消除 zCard 判断与写入之间的并发窗口；单设备模式等价于保留最新 1 个
        int maxTokens = isMultiTokenAllowed ? multiTokenCount : 1;

        trimTokens(principalId, maxTokens);

        return token;
    }

    @Override
    public Set<String> getTokens(P principal) {

        String principalId = getPrincipalId(principal);

        Set<String> tokens = redisTemplate.opsForZSet().range(principalId, 0, -1);

        return tokens == null ? Collections.emptySet() : tokens;
    }

    @Override
    protected void revokePrincipal(P principal, String token) {

        String principalId = getPrincipalId(principal);

        redisTemplate.opsForZSet().remove(principalId, token);
    }

    /**
     * 原子裁剪：保留最新的 maxTokens 个 Token，超出的最旧 Token 会话同步失效
     *
     * @param principalId 用户Token集合Key
     * @param maxTokens   保留的最大Token数量
     */
    private void trimTokens(String principalId, int maxTokens) {

        List<String> removed = redisTemplate.execute(
                TRIM_SCRIPT,
                Collections.singletonList(principalId),
                String.valueOf(maxTokens)
        );

        if (removed.isEmpty()) {
            return;
        }

        for (String oldToken : removed) {

            // 会话失效（集合成员已由脚本移除，无需重复 ZREM）
            tokenCache.evict(getTokenId(oldToken));

            logger.info("剔除设备：{}", maskToken(oldToken));
        }
    }

    /**
     * Token 脱敏：仅保留前 8 位用于问题定位，避免凭证明文入日志
     */
    private static String maskToken(String token) {
        if (token == null || token.length() <= 8) {
            return "***";
        }

        return token.substring(0, 8) + "***";
    }

    /**
     * 删除已过期Token
     *
     * @param principalId 用户ID
     */
    private void removeExpiredTokens(String principalId) {

        Set<String> tokens = redisTemplate.opsForZSet().range(principalId, 0, -1);

        if (tokens == null || tokens.isEmpty()) {

            return;
        }

        for (String token : tokens) {

            try {

                isValid(token);

            } catch (Exception e) {

                // 过期与非法 Token 均从集合中清理，避免单个 Token 异常中断整个清理流程
                redisTemplate.opsForZSet().remove(principalId, token);
            }
        }
    }
}
