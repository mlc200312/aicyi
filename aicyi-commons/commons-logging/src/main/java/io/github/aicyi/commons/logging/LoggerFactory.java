package io.github.aicyi.commons.logging;

/**
 * @author Mr.Min
 * @description 日志工厂
 * @date 2019-06-23
 **/
public class LoggerFactory {
    private static final Logger ACCESS_LOGGER = new CommonLogger(LoggerType.ACCESS);
    private static final Logger CLIENT_LOGGER = new CommonLogger(LoggerType.CLIENT);
    private static final Logger PERFORMANCE_LOGGER = new CommonLogger(LoggerType.PERFORMANCE);
    private static final Logger SCHEDULE_LOGGER = new CommonLogger(LoggerType.SCHEDULE);
    private static final Logger MESSAGE_LOGGER = new CommonLogger(LoggerType.MESSAGE);
    private static final Logger BIZ_LOGGER = new CommonLogger(LoggerType.BIZ);
    private static final Logger DAO_LOGGER = new CommonLogger(LoggerType.DAO);

    public static Logger getLogger(LoggerType type) {
        switch (type) {
            case ACCESS:
                return ACCESS_LOGGER;
            case SCHEDULE:
                return SCHEDULE_LOGGER;
            case CLIENT:
                return CLIENT_LOGGER;
            case PERFORMANCE:
                return PERFORMANCE_LOGGER;
            case MESSAGE:
                return MESSAGE_LOGGER;
            case BIZ:
                return BIZ_LOGGER;
            case DAO:
                return DAO_LOGGER;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static Logger getLogger(final Class<?> clazz) {
        return new CommonLogger(clazz);
    }
}
