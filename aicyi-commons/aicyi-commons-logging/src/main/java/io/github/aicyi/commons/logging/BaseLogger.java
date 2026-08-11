package io.github.aicyi.commons.logging;

import io.github.aicyi.commons.core.logging.Logger;

/**
 * @author Mr.Min
 * @description 日志基础抽象类
 * @date 2019-06-23
 **/
public abstract class BaseLogger implements Logger {
    protected String name;

    public BaseLogger(String name) {
        this.name = name;
    }

    public BaseLogger(Class<?> clazz) {
        this.name = clazz.getName();
    }

    public BaseLogger(LoggerType type) {
        this.name = type.getName();
    }

    protected org.slf4j.Logger getLogger() {
        return org.slf4j.LoggerFactory.getLogger(this.name);
    }

    protected abstract String formatMessage(Object obj);

    protected abstract String formatMessage(String format, Object... args);

    private boolean isInfoEnabled() {
        return getLogger().isInfoEnabled();
    }

    private boolean isWarnEnabled() {
        return getLogger().isWarnEnabled();
    }

    private boolean isErrorEnabled() {
        return getLogger().isErrorEnabled();
    }

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
        if (isInfoEnabled()) {
            logger.info(formatMessage(obj));
        }
    }

    @Override
    public void info(String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (isInfoEnabled()) {
            logger.info(formatMessage(format, args));
        }
    }

    @Override
    public void info(Throwable cause, String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (isInfoEnabled()) {
            logger.info(formatMessage(format, args), cause);
        }
    }

    @Override
    public void warn(Object obj) {
        org.slf4j.Logger logger = getLogger();
        if (isWarnEnabled()) {
            logger.warn(formatMessage(obj));
        }
    }

    @Override
    public void warn(String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (isWarnEnabled()) {
            logger.warn(formatMessage(format, args));
        }
    }

    @Override
    public void warn(Throwable cause, String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (isWarnEnabled()) {
            logger.warn(formatMessage(format, args), cause);
        }
    }

    @Override
    public void error(Object obj) {
        org.slf4j.Logger logger = getLogger();
        if (isErrorEnabled()) {
            logger.error(formatMessage(obj));
        }
    }

    @Override
    public void error(String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (isErrorEnabled()) {
            logger.error(formatMessage(format, args));
        }
    }

    @Override
    public void error(Throwable cause, String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (isErrorEnabled()) {
            logger.error(formatMessage(format, args), cause);
        }
    }
}
