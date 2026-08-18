package io.github.aicyi.commons.logging;

import io.github.aicyi.commons.core.logging.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Mr.Min
 * @description 日志工厂
 * @date 2019-06-23
 **/
public final class LoggerFactory {

    private LoggerFactory() {
    }

    private static final Logger ACCESS_LOGGER = new CommonLogger(LoggerType.ACCESS);
    private static final Logger CLIENT_LOGGER = new CommonLogger(LoggerType.CLIENT);
    private static final Logger PERFORMANCE_LOGGER = new CommonLogger(LoggerType.PERFORMANCE);
    private static final Logger SCHEDULE_LOGGER = new CommonLogger(LoggerType.SCHEDULE);
    private static final Logger MESSAGE_LOGGER = new CommonLogger(LoggerType.MESSAGE);
    private static final Logger BIZ_LOGGER = new CommonLogger(LoggerType.BIZ);
    private static final Logger DAO_LOGGER = new CommonLogger(LoggerType.DAO);

    /**
     * 按名称缓存 logger，避免重复创建
     */
    private static final ConcurrentMap<String, Logger> LOGGER_CACHE = new ConcurrentHashMap<>();

    public static Logger getLogger(LoggerType type) {
        switch (type) {
            case ACCESS:
                return ACCESS_LOGGER;
            case CLIENT:
                return CLIENT_LOGGER;
            case PERFORMANCE:
                return PERFORMANCE_LOGGER;
            case SCHEDULE:
                return SCHEDULE_LOGGER;
            case MESSAGE:
                return MESSAGE_LOGGER;
            case BIZ:
                return BIZ_LOGGER;
            case DAO:
                return DAO_LOGGER;
            default:
                // switch 已穷举枚举值，理论不可达
                throw new IllegalStateException("unreachable logger type: " + type.getName());
        }
    }

    public static Logger getLogger(final Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(String name) {
        return LOGGER_CACHE.computeIfAbsent(name, CommonLogger::new);
    }
}
