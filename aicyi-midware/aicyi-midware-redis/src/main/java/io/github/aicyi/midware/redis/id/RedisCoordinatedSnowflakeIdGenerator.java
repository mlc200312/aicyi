package io.github.aicyi.midware.redis.id;

import io.github.aicyi.commons.core.id.IdGenerator;
import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.util.id.SnowflakeIdGenerator;
import io.github.aicyi.commons.core.id.WorkerIdLease;

/**
 * @author Mr.Min
 * @description 基于Redis的分布式雪花ID生成器
 * @date 2026/5/21
 **/
public class RedisCoordinatedSnowflakeIdGenerator implements IdGenerator {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final WorkerIdManager workerIdManager;
    private final int datacenterId;
    private final long epoch;
    private final long clockBackwardToleranceMs;

    private volatile SnowflakeIdGenerator snowflake;

    public RedisCoordinatedSnowflakeIdGenerator(
            WorkerIdManager workerIdManager,
            int datacenterId,
            long epoch,
            long clockBackwardToleranceMs
    ) {
        this.workerIdManager = workerIdManager;
        this.datacenterId = datacenterId;
        this.epoch = epoch;
        this.clockBackwardToleranceMs = clockBackwardToleranceMs;
    }

    private SnowflakeIdGenerator getSnowflake() {
        if (snowflake != null) {
            return snowflake;
        }

        synchronized (this) {
            if (snowflake == null) {
                init();
            }
        }

        return snowflake;
    }

    public void init() {
        WorkerIdLease lease = workerIdManager.getLease();

        if (lease == null || !workerIdManager.isLeaseValid()) {
            throw new IllegalStateException("WorkerId not ready");
        }

        int workerId = lease.getWorkerId();

        this.snowflake = new SnowflakeIdGenerator(
                workerId,
                datacenterId,
                epoch,
                clockBackwardToleranceMs
        );

        logger.info("Snowflake initialized workerId={}, datacenterId={}", workerId, datacenterId
        );
    }

    @Override
    public long nextId() {
        return getSnowflake().nextId();
    }
}