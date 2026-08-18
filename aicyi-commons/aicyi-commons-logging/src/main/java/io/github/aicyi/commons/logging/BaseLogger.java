package io.github.aicyi.commons.logging;

import io.github.aicyi.commons.core.logging.Logger;

/**
 * @author Mr.Min
 * @description 日志基础抽象类
 * @date 2019-06-23
 **/
public abstract class BaseLogger implements Logger {
    protected String name;

    /**
     * 构造时持有 slf4j logger，避免每条日志重复查找
     */
    private final org.slf4j.Logger logger;

    public BaseLogger(String name) {
        this.name = name;
        this.logger = org.slf4j.LoggerFactory.getLogger(name);
    }

    public BaseLogger(Class<?> clazz) {
        this(clazz.getName());
    }

    public BaseLogger(LoggerType type) {
        this(type.getName());
    }

    protected org.slf4j.Logger getLogger() {
        return this.logger;
    }

    protected abstract String formatMessage(Object obj);

    protected abstract String formatMessage(String format, Object... args);

    @Override
    public boolean isDebugEnabled() {
        return getLogger().isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return getLogger().isTraceEnabled();
    }

    @Override
    public void trace(Object obj) {
        org.slf4j.Logger logger = getLogger();
        if (isTraceEnabled()) {
            logger.trace(formatMessage(obj));
        }
    }

    @Override
    public void trace(String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (isTraceEnabled()) {
            logger.trace(formatMessage(format, args));
        }
    }

    @Override
    public void trace(Throwable cause, String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (isTraceEnabled()) {
            logger.trace(formatMessage(format, args), cause);
        }
    }

    @Override
    public void debug(Object obj) {
        org.slf4j.Logger logger = getLogger();
        if (isDebugEnabled()) {
            logger.debug(formatMessage(obj));
        }
    }

    @Override
    public void debug(String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (isDebugEnabled()) {
            logger.debug(formatMessage(format, args));
        }
    }

    @Override
    public void debug(Throwable cause, String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (isDebugEnabled()) {
            logger.debug(formatMessage(format, args), cause);
        }
    }

    @Override
    public void info(Object obj) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isInfoEnabled()) {
            logger.info(formatMessage(obj));
        }
    }

    @Override
    public void info(String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isInfoEnabled()) {
            logger.info(formatMessage(format, args));
        }
    }

    @Override
    public void info(Throwable cause, String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isInfoEnabled()) {
            logger.info(formatMessage(format, args), cause);
        }
    }

    @Override
    public void warn(Object obj) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isWarnEnabled()) {
            logger.warn(formatMessage(obj));
        }
    }

    @Override
    public void warn(String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isWarnEnabled()) {
            logger.warn(formatMessage(format, args));
        }
    }

    @Override
    public void warn(Throwable cause, String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isWarnEnabled()) {
            logger.warn(formatMessage(format, args), cause);
        }
    }

    @Override
    public void error(Object obj) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isErrorEnabled()) {
            logger.error(formatMessage(obj));
        }
    }

    @Override
    public void error(String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isErrorEnabled()) {
            logger.error(formatMessage(format, args));
        }
    }

    @Override
    public void error(Throwable cause, String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isErrorEnabled()) {
            logger.error(formatMessage(format, args), cause);
        }
    }
}
