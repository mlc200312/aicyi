package com.aichuangyi.commons.logging;

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

    /**
     * 拼接日志
     *
     * @param obj
     * @return
     */
    protected abstract String formatMessage(Object obj);

    /**
     * 拼接日志
     *
     * @param format
     * @param args
     * @return
     */
    protected abstract String formatMessage(String format, Object... args);

    @Override
    public void trace(Object obj) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isTraceEnabled()) {
            logger.trace(formatMessage(obj));
        }
    }

    @Override
    public void trace(String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isTraceEnabled()) {
            logger.trace(formatMessage(format, args));
        }
    }

    @Override
    public void trace(Throwable cause, String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isTraceEnabled()) {
            logger.trace(formatMessage(format, args), cause);
        }
    }

    @Override
    public void debug(Object obj) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isDebugEnabled()) {
            logger.debug(formatMessage(obj));
        }
    }

    @Override
    public void debug(String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isDebugEnabled()) {
            logger.debug(formatMessage(format, args));
        }
    }

    @Override
    public void debug(Throwable cause, String format, Object... args) {
        org.slf4j.Logger logger = getLogger();
        if (logger.isDebugEnabled()) {
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
