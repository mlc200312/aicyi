package io.github.aicyi.midware.autoconfigure;

import io.github.aicyi.commons.core.id.IdGenerator;
import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.core.id.WorkerIdAllocator;
import io.github.aicyi.midware.properties.SnowflakeProperties;
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

@AutoConfiguration
@EnableConfigurationProperties(SnowflakeProperties.class)
@ConditionalOnClass(StringRedisTemplate.class)
@ConditionalOnProperty(
        prefix = "snowflake",
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
        logger.info("Initializing RedisWorkerIdAllocator");

        return new RedisWorkerIdAllocator(
                redisTemplate,
                properties.getServiceName(),
                properties.getMaxWorkerId(),
                properties.getTtlSeconds()
        );
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