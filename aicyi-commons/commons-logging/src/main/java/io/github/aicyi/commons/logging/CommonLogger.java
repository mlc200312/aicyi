package io.github.aicyi.commons.logging;

/**
 * @author Mr.Min
 * @description 通用日志实现
 * @date 2019-05-27
 **/
public class CommonLogger extends BaseLogger {

    public CommonLogger(String name) {
        super(name);
    }

    public CommonLogger(Class<?> clazz) {
        super(clazz);
    }

    public CommonLogger(LoggerType type) {
        super(type);
    }

    @Override
    protected String formatMessage(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    @Override
    protected String formatMessage(String format, Object... args) {
        return StringFormat.format1(format, args);
    }
}
