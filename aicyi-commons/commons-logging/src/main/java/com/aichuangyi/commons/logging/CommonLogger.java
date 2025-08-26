package com.aichuangyi.commons.logging;

import com.aichuangyi.commons.util.StringFormat;

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
    protected String concatMsg(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    @Override
    protected String concatMsg(String format, Object... args) {
        return StringFormat.format1(format, args);
    }

    @Override
    protected String concatMsg(Throwable cause, String format, Object... args) {
        return String.format("%s ==> %s", StringFormat.format1(format, args), cause.getMessage());
    }
}
