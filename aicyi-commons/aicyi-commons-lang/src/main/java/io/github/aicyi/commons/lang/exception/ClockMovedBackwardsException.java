package io.github.aicyi.commons.lang.exception;

/**
 * @author Mr.Min
 * @description 时间回拨异常
 * @date 2026/5/21
 **/
public class ClockMovedBackwardsException extends SnowflakeException {

    public ClockMovedBackwardsException(long lastTimestamp, long currentTimestamp) {
        super(String.format(
                "Clock moved backwards. Refusing to generate id for %d ms (last=%d, current=%d)",
                lastTimestamp - currentTimestamp,
                lastTimestamp,
                currentTimestamp
        ));
    }
}