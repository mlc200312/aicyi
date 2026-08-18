package io.github.aicyi.midware.redis.id;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.core.id.WorkerIdAllocator;
import io.github.aicyi.commons.lang.model.WorkerIdLease;
import org.springframework.context.SmartLifecycle;

/**
 * @author Mr.Min
 * @description 分布式唯一ID生成器
 * @date 2026/5/21
 **/
public class WorkerIdManager implements SmartLifecycle {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WorkerIdAllocator allocator;

    private final long heartbeatSeconds;

    private final boolean autoRecover;

    private volatile WorkerIdLease lease;

    private WorkerIdHeartbeat heartbeat;

    private volatile boolean running = false;

    /**
     * lease 是否仍有效
     */
    private volatile boolean leaseValid = false;

    public WorkerIdManager(WorkerIdAllocator allocator, Long heartbeatSeconds) {
        this.allocator = allocator;
        this.heartbeatSeconds = heartbeatSeconds;
        this.autoRecover = false;
    }

    public WorkerIdManager(WorkerIdAllocator allocator, Long heartbeatSeconds, Boolean autoRecover) {
        this.allocator = allocator;
        this.heartbeatSeconds = heartbeatSeconds;
        this.autoRecover = autoRecover;
    }

    public WorkerIdLease getLease() {
        return lease;
    }

    public boolean isLeaseValid() {
        return leaseValid;
    }

    private void markLeaseLost() {

        logger.error("WorkerId lease LOST! workerId={}", lease != null ? lease.getWorkerId() : null);

        leaseValid = false;

        if (autoRecover) {
            try {
                start();
                logger.info("WorkerId recovered, new workerId={}", lease.getWorkerId());
            } catch (Exception e) {
                logger.error("WorkerId auto recover failed", e);
            }
        }
    }

    /**
     * 幂等启动：重复调用会先停止旧心跳再重新申请租约
     */
    @Override
    public synchronized void start() {

        if (heartbeat != null) {
            // 租约丢失场景下 release 会被服务端以 token 不匹配拒绝，无害
            heartbeat.stop();
            heartbeat = null;
        }

        leaseValid = false;

        lease = allocator.allocate();

        heartbeat = new WorkerIdHeartbeat(
                allocator,
                lease,
                heartbeatSeconds,
                this::markLeaseLost
        );

        heartbeat.start();

        leaseValid = true;
        running = true;

        logger.info("WorkerIdManager started workerId={}", lease.getWorkerId());
    }

    @Override
    public synchronized void stop() {
        leaseValid = false;

        if (heartbeat != null) {
            heartbeat.stop();
            heartbeat = null;
        }

        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}