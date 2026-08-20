package io.github.aicyi.midware.starter.autoconfigure;

import io.github.aicyi.commons.core.id.IdGenerator;
import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.core.id.WorkerIdAllocator;
import io.github.aicyi.midware.starter.properties.SnowflakeProperties;
import io.github.aicyi.midware.redis.id.RedisCoordinatedSnowflakeIdGenerator;
import io.github.aicyi.midware.redis.id.RedisWorkerIdAllocator;
import io.github.aicyi.midware.redis.id.WorkerIdManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author Mr.Min
 * @description 雪花 ID 自动配置类
 * <p>
 * 类级 @ConditionalOnClass 守卫：需同时覆盖 spring-data-redis 与 aicyi-midware-redis 两侧的类，
 * 业务未引入 redis 时整个配置类跳过加载，避免 NoClassDefFoundError
 **/
@AutoConfiguration
@EnableConfigurationProperties(SnowflakeProperties.class)
@ConditionalOnClass({StringRedisTemplate.class, WorkerIdManager.class})
@ConditionalOnProperty(
        prefix = "aicyi.snowflake",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class SnowflakeAutoConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * WorkerId allocator
     */
    @Bean
    @ConditionalOnMissingBean
    public WorkerIdAllocator workerIdAllocator(StringRedisTemplate redisTemplate, SnowflakeProperties properties) {

        validateProperties(properties);

        logger.info("Initializing RedisWorkerIdAllocator");

        return new RedisWorkerIdAllocator(
                redisTemplate,
                properties.getServiceName(),
                properties.getMaxWorkerId(),
                properties.getTtlSeconds()
        );
    }

    /**
     * 配置契约校验（fail-fast）：底层发号器固定 5 位 workerId，超限配置会导致运行期发号异常；
     * serviceName 为空或使用默认值时多服务共享 workerId 命名空间
     */
    private void validateProperties(SnowflakeProperties properties) {

        if (properties.getWorkerIdBits() != SnowflakeProperties.FIXED_WORKER_ID_BITS) {
            throw new IllegalStateException(
                    "aicyi.snowflake.worker-id-bits only supports " + SnowflakeProperties.FIXED_WORKER_ID_BITS
                            + " (SnowflakeIdGenerator uses a fixed 5-bit workerId), but was set to " + properties.getWorkerIdBits());
        }

        String serviceName = properties.getServiceName();
        if (serviceName == null || serviceName.trim().isEmpty()) {
            throw new IllegalStateException("aicyi.snowflake.service-name must not be blank");
        }

        if (SnowflakeProperties.DEFAULT_SERVICE_NAME.equals(serviceName)) {
            logger.warn("aicyi.snowflake.service-name uses default value '{}', multiple services sharing the same Redis "
                    + "will compete for the same workerId namespace. Please configure it explicitly", serviceName);
        }
    }

    /**
     * WorkerId manager
     */
    @Bean
    @ConditionalOnMissingBean
    public WorkerIdManager workerIdManager(WorkerIdAllocator allocator, SnowflakeProperties properties) {
        logger.info("Initializing WorkerIdManager");

        return new WorkerIdManager(
                allocator,
                properties.getHeartbeatSeconds(),
                properties.isAutoRecover()
        );
    }

    /**
     * Snowflake generator
     */
    @Bean
    @ConditionalOnMissingBean(IdGenerator.class)
    public IdGenerator idGenerator(WorkerIdManager manager, SnowflakeProperties properties) {
        logger.info("Initializing RedisCoordinatedSnowflakeIdGenerator");

        return new RedisCoordinatedSnowflakeIdGenerator(
                manager,
                properties.getDatacenterId(),
                properties.getEpoch(),
                properties.getClockBackwardToleranceMs()
        );
    }
}