package io.github.aicyi.commons.util.id;

/**
 * @author Mr.Min
 * @description 雪花算法
 * @date 2025/8/5
 **/
public class Snowflake {
    private final long epoch = 1609459200000L; // 2021-01-01 00:00:00 UTC
    private final int workerIdBits = 5;
    private final int dataCenterIdBits = 5;
    private final int sequenceBits = 12;
    private final int maxWorkerId = -1 ^ (-1 << workerIdBits);
    private final int maxDataCenterId = -1 ^ (-1 << dataCenterIdBits);
    private final int maxSequence = -1 ^ (-1 << sequenceBits);
    private final int workerIdShift = sequenceBits;
    private final int dataCenterIdShift = sequenceBits + workerIdBits;
    private final int timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
    private final int sequenceMask = maxSequence;
    private long workerId;
    private long dataCenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public Snowflake(long workerId, long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("Invalid worker ID: " + workerId);
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException("Invalid data center ID: " + dataCenterId);
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public long getDataCenterId() {
        return dataCenterId;
    }

    public void setDataCenterId(long dataCenterId) {
        this.dataCenterId = dataCenterId;
    }

    public int getMaxWorkerId() {
        return maxWorkerId;
    }

    public long nextId() {
        long timestamp = System.currentTimeMillis() - epoch;
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID for " + (lastTimestamp - timestamp) + " milliseconds.");
        }
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp << timestampLeftShift) | (dataCenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence);
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis() - epoch;
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis() - epoch;
        }
        return timestamp;
    }
}