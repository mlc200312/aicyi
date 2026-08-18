package io.github.aicyi.midware.redis.id;

import io.github.aicyi.commons.core.id.IdGenerator;
import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.util.id.SnowflakeIdGenerator;
import io.github.aicyi.commons.lang.model.WorkerIdLease;

/**
 * @author Mr.Min
 * @description 基于Redis的分布式雪花ID生成器
 * @date 2026/5/21
 **/
public class RedisCoordinatedSnowflakeIdGenerator implements IdGenerator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WorkerIdManager workerIdManager;
    private final int datacenterId;
    private final long epoch;
    private final long clockBackwardToleranceMs;

    private volatile SnowflakeIdGenerator snowflake;

    /**
     * 当前 snowflake 对应的租约，用于感知租约失效/换新（恢复后 workerId 可能变化）
     */
    private volatile WorkerIdLease activeLease;

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

        WorkerIdLease currentLease = workerIdManager.getLease();

        // 租约未就绪或已丢失：拒绝发号，避免与其他节点的 workerId 冲突产生重复 ID
        if (currentLease == null || !workerIdManager.isLeaseValid()) {
            this.snowflake = null;
            throw new IllegalStateException("WorkerId lease not ready, refuse to generate id");
        }

        SnowflakeIdGenerator current = snowflake;

        if (current != null && currentLease == activeLease) {
            return current;
        }

        synchronized (this) {
            if (snowflake == null || workerIdManager.getLease() != activeLease) {
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

        this.activeLease = lease;

        logger.info("Snowflake initialized workerId={}, datacenterId={}", workerId, datacenterId
        );
    }

    @Override
    public long nextId() {
        return getSnowflake().nextId();
    }
}