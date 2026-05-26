package io.github.aicyi.commons.util.id;

import io.github.aicyi.commons.core.id.IdGenerator;
import io.github.aicyi.commons.lang.exception.ClockMovedBackwardsException;
import io.github.aicyi.commons.lang.exception.SnowflakeException;

/**
 * Snowflake ID生成器
 * <p>
 * 结构：
 * 1 bit   - 符号位（固定0）
 * 41 bits - 时间戳
 * 5 bits  - datacenterId
 * 5 bits  - workerId
 * 12 bits - sequence
 *
 * @author Mr.Min
 * @description 雪花ID生成器
 * @date 2025/8/5
 */
public class SnowflakeIdGenerator implements IdGenerator {

    /**
     * workerId bit数
     */
    private static final long WORKER_ID_BITS = 5L;

    /**
     * datacenterId bit数
     */
    private static final long DATACENTER_ID_BITS = 5L;

    /**
     * sequence bit数
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * 最大workerId
     */
    public static final long MAX_WORKER_ID = (1L << WORKER_ID_BITS) - 1;

    /**
     * 最大datacenterId
     */
    public static final long MAX_DATACENTER_ID = (1L << DATACENTER_ID_BITS) - 1;

    /**
     * sequence最大值
     */
    private static final long SEQUENCE_MASK = (1L << SEQUENCE_BITS) - 1;

    /**
     * 位移
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    /**
     * 默认epoch（2024-01-01 00:00:00）
     */
    public static final long DEFAULT_EPOCH = 1704067200000L;

    /**
     * 小回拨最大等待时间
     */
    private static final long MAX_BACKWARD_WAIT_MILLIS = 5L;

    /**
     * workerId
     */
    private final long workerId;

    /**
     * 数据中心ID
     */
    private final long datacenterId;

    /**
     * 自定义起始时间
     */
    private final long epoch;

    /**
     * 时钟回拨是否允许等待
     */
    private final boolean clockBackwardWait;

    /**
     * 时钟回拨容忍时间
     */
    private long clockBackwardToleranceMs;

    /**
     * 序列号
     */
    private long sequence = 0L;

    /**
     * 上次时间戳
     */
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        this(workerId, datacenterId, DEFAULT_EPOCH, true, MAX_BACKWARD_WAIT_MILLIS);
    }

    public SnowflakeIdGenerator(
            long workerId,
            long datacenterId,
            long epoch,
            long clockBackwardToleranceMs
    ) {
        this(workerId, datacenterId, epoch, true, clockBackwardToleranceMs);
    }

    public SnowflakeIdGenerator(
            long workerId,
            long datacenterId,
            long epoch,
            boolean clockBackwardWait,
            long clockBackwardToleranceMs
    ) {
        validate(workerId, datacenterId, epoch, clockBackwardToleranceMs);

        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.epoch = epoch;
        this.clockBackwardWait = clockBackwardWait;
        this.clockBackwardToleranceMs = clockBackwardToleranceMs;
    }

    @Override
    public synchronized long nextId() {
        long timestamp = currentTimeMillis();

        // 时钟回拨处理
        if (timestamp < lastTimestamp) {
            timestamp = handleClockBackward(timestamp);
        }

        if (timestamp == lastTimestamp) {
            // 同毫秒内递增
            sequence = (sequence + 1) & SEQUENCE_MASK;

            // sequence溢出
            if (sequence == 0L) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 新毫秒随机起点可选，这里用0更稳定
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 时钟回拨处理
     */
    private long handleClockBackward(long currentTimestamp) {
        long offset = lastTimestamp - currentTimestamp;

        if (!clockBackwardWait) {
            throw new ClockMovedBackwardsException(lastTimestamp, currentTimestamp);
        }

        // 小回拨等待
        if (offset <= clockBackwardToleranceMs) {
            sleep(offset);
            long timestamp = currentTimeMillis();

            if (timestamp < lastTimestamp) {
                throw new ClockMovedBackwardsException(lastTimestamp, timestamp);
            }

            return timestamp;
        }

        throw new ClockMovedBackwardsException(lastTimestamp, currentTimestamp);
    }

    /**
     * 等待下一毫秒
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();

        while (timestamp <= lastTimestamp) {
            timestamp = currentTimeMillis();
        }

        return timestamp;
    }

    /**
     * 当前时间
     */
    protected long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 睡眠
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SnowflakeException("Thread interrupted while waiting clock recovery", e);
        }
    }

    /**
     * 参数校验
     */
    private void validate(long workerId, long datacenterId, long epoch, long clockBackwardToleranceMs) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException(
                    String.format("workerId must be between 0 and %d", MAX_WORKER_ID)
            );
        }

        if (datacenterId < 0 || datacenterId > MAX_DATACENTER_ID) {
            throw new IllegalArgumentException(
                    String.format("datacenterId must be between 0 and %d", MAX_DATACENTER_ID)
            );
        }

        if (epoch <= 0) {
            throw new IllegalArgumentException("epoch must be greater than 0");
        }

        if (epoch > System.currentTimeMillis()) {
            throw new IllegalArgumentException("epoch must not be in the future");
        }

        if (clockBackwardToleranceMs <= 0) {
            throw new IllegalArgumentException("clockBackwardToleranceMs must be greater than 0");
        }
    }

    public long getWorkerId() {
        return workerId;
    }

    public long getDatacenterId() {
        return datacenterId;
    }

    public long getEpoch() {
        return epoch;
    }
}