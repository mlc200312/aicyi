package io.github.aicyi.midware.kit.util;

import io.github.aicyi.commons.core.id.IdGenerator;
import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.util.id.SnowflakeIdGenerator;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Mr.Min
 * @description ID生成工具类
 * <p>
 * 容器注入 {@link IdGenerator} 后以分布式发号器生成 ID；
 * 未注入时（如未启用 aicyi.snowflake）降级为本地 SnowflakeIdGenerator(workerId=0)，
 * 降级仅适用于单机场景，多实例部署会产生重复 ID（首次降级时输出告警日志）
 * @date 19:15
 **/
public class IdUtils implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdUtils.class);

    private static final SnowflakeIdGenerator DEFAULT_ID_GENERATOR = new SnowflakeIdGenerator(0, 0);

    /**
     * 降级告警仅输出一次，避免高频发号场景日志风暴
     */
    private static final AtomicBoolean FALLBACK_WARNED = new AtomicBoolean(false);

    private static volatile IdUtils INSTANCE;

    private final IdGenerator idGenerator;

    public IdUtils(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public static long generateId() {

        IdUtils instance = INSTANCE;
        if (instance == null || instance.idGenerator == null) {
            if (FALLBACK_WARNED.compareAndSet(false, true)) {
                LOGGER.warn("IdGenerator is not injected, fallback to local SnowflakeIdGenerator(workerId=0). "
                        + "Multi-instance deployments may generate duplicate IDs, "
                        + "please enable aicyi.snowflake for distributed ID generation");
            }
            return DEFAULT_ID_GENERATOR.nextId();
        }
        return instance.idGenerator.nextId();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.idGenerator == null) {
            throw new IllegalStateException("IdUtils 未完成初始化，请确认 Spring 容器已加载且 IdGenerator 已注入");
        }

        IdUtils.INSTANCE = this;
    }
}
