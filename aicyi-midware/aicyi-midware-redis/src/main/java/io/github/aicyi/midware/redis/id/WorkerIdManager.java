package io.github.aicyi.midware.redis.id;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.util.id.WorkerIdAllocator;
import io.github.aicyi.commons.util.id.WorkerIdLease;
import org.springframework.context.SmartLifecycle;

/**
 * @author Mr.Min
 * @description 分布式唯一ID生成器
 * @date 2026/5/21
 **/
public class WorkerIdManager implements SmartLifecycle {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WorkerIdAllocator allocator;

    private volatile WorkerIdLease lease;

    private WorkerIdHeartbeat heartbeat;

    private volatile boolean running = false;

    /**
     * lease 是否仍有效
     */
    private volatile boolean leaseValid = false;

    public WorkerIdManager(WorkerIdAllocator allocator) {
        this.allocator = allocator;
    }

    public WorkerIdLease getLease() {
        return lease;
    }

    @Override
    public void start() {

        lease = allocator.allocate();

        heartbeat = new WorkerIdHeartbeat(
                allocator,
                lease,
                this::markLeaseLost
        );

        heartbeat.start();

        leaseValid = true;
        running = true;

        logger.info("WorkerIdManager started workerId={}", lease.getWorkerId());
    }

    @Override
    public void stop() {
        leaseValid = false;

        if (heartbeat != null) {
            heartbeat.stop();
        }

        running = false;
    }

    public boolean isLeaseValid() {
        return leaseValid;
    }

    private void markLeaseLost() {
        leaseValid = false;

        logger.error("WorkerId lease LOST! workerId={}", lease.getWorkerId());
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}