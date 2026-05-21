package io.github.aicyi.midware.redis.id;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.core.id.WorkerIdAllocator;
import io.github.aicyi.commons.core.id.WorkerIdLease;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 分布式雪花ID生成器工作节点ID分配器
 * @date 2026/5/21
 **/
public class WorkerIdHeartbeat {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final WorkerIdAllocator allocator;
    private final WorkerIdLease lease;
    private final long heartbeatSeconds;
    private final Runnable onLeaseLost;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(
            r -> new Thread(r, "worker-id-heartbeat")
    );

    private ScheduledFuture<?> future;

    public WorkerIdHeartbeat(WorkerIdAllocator allocator, WorkerIdLease lease, Runnable onLeaseLost) {
        this.allocator = allocator;
        this.lease = lease;
        this.onLeaseLost = onLeaseLost;
        this.heartbeatSeconds = Math.max(lease.getTtlSeconds() / 3, 5);
    }

    public WorkerIdHeartbeat(WorkerIdAllocator allocator, WorkerIdLease lease, long heartbeatSeconds, Runnable onLeaseLost) {
        this.allocator = allocator;
        this.lease = lease;
        this.heartbeatSeconds = heartbeatSeconds;
        this.onLeaseLost = onLeaseLost;
    }

    public void start() {
        long interval = heartbeatSeconds;

        future = scheduler.scheduleAtFixedRate(() -> {
            try {
                boolean ok = allocator.renew(lease);

                if (!ok) {
                    logger.error("WorkerId renew failed, lease lost. workerId={}", lease.getWorkerId());

                    onLeaseLost.run();

                    stopWithoutRelease();
                }
            } catch (Exception e) {
                logger.error("WorkerId heartbeat exception", e);
            }
        }, interval, interval, TimeUnit.SECONDS);

        logger.info("Heartbeat started workerId={}", lease.getWorkerId());
    }

    public void stop() {
        if (future != null) {
            future.cancel(true);
        }

        scheduler.shutdownNow();

        try {
            allocator.release(lease);
        } catch (Exception e) {
            logger.warn("release failed", e);
        }
    }

    /**
     * lease 已经丢了，不能 release（防误删）
     */
    private void stopWithoutRelease() {
        if (future != null) {
            future.cancel(true);
        }

        scheduler.shutdownNow();
    }
}