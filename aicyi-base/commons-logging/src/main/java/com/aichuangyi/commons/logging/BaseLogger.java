package com.aichuangyi.commons.logging;

/**
 * @author Mr.Min
 * @description 功能描述
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
    protected abstract String concatMsg(Object obj);

    /**
     * 拼接日志
     *
     * @param format
     * @param args
     * @return
     */
    protected abstract String concatMsg(String format, Object... args);

    /**
     * 拼接异常日志
     *
     * @param cause
     * @param format
     * @param args
     * @return
     */
    protected abstract String concatMsg(Throwable cause, String format, Object... args);

    @Override
    public void trace(Object obj) {
        org.slf4j.Logger logger = this.getLogger();
        if (logger.isTraceEnabled()) {
            logger.trace(this.concatMsg(obj));
        }
    }

    @Override
    public void trace(String format, Object... args) {
        org.slf4j.Logger logger = this.getLogger();
        if (logger.isTraceEnabled()) {
            logger.trace(this.concatMsg(format, args));
        }
    }

    @Override
    public void trace(Throwable cause, String format, Object... args) {
        org.slf4j.Logger logger = this.getLogger();
        if (logger.isTraceEnabled()) {
            logger.trace(this.concatMsg(cause, format, args), cause);
        }
    }

    @Override
    public void debug(Object obj) {
        org.slf4j.Logger logger = this.getLogger();
        if (logger.isDebugEnabled()) {
            logger.debug(this.concatMsg(obj));
        }
    }

    @Override
    public void debug(String format, Object... args) {
        org.slf4j.Logger logger = this.getLogger();
        if (logger.isDebugEnabled()) {
            logger.debug(this.concatMsg(format, args));
        }
    }

    @Override
    public void debug(Throwable cause, String format, Object... args) {
        org.slf4j.Logger logger = this.getLogger();
        if (logger.isDebugEnabled()) {
            logger.debug(this.concatMsg(format, args), cause);
        }
    }

    @Override
    public void info(Object obj) {
        org.slf4j.Logger logger = this.getLogger();
        if (logger.isInfoEnabled()) {
            logger.info(this.concatMsg(obj));
        }
    }

    @Override
    public void info(String format, Object... args) {
        org.slf4j.Logger logger = this.getLogger();
        if (logger.isInfoEnabled()) {
            logger.info(this.concatMsg(format, args));
        }
    }

    @Override
    public void info(Throwable cause, String format, Object... args) {
        org.slf4j.Logger logger = this.getLogger();
        if (logger.isInfoEnabled()) {
            logger.info(this.concatMsg(format, args), cause);
        }
    }

    @Override
    public void warn(Object obj) {
        org.slf4j.Logger logger = this.getLogger();
        if (logger.isWarnEnabled()) {
            logger.warn(this.concatMsg(obj));
        }
    }

    @Override
    public void warn(String format, Object... args) {
        org.slf4j.Logger logger = this.getLogger();
        if (logger.isWarnEnabled()) {
            logger.warn(this.concatMsg(format, args));
        }
    }

    @Override
    public void warn(Throwable cause, String format, Object... args) {
        org.slf4j.Logger logger = this.getLogger();
        if (logger.isWarnEnabled()) {
            logger.warn(this.concatMsg(format, args), cause);
        }
    }

    @Override
    public void error(Object obj) {
        org.slf4j.Logger logger = this.getLogger();
        if (logger.isErrorEnabled()) {
            logger.error(this.concatMsg(obj));
        }
    }

    @Override
    public void error(String format, Object... args) {
        org.slf4j.Logger logger = this.getLogger();
        if (logger.isErrorEnabled()) {
            logger.error(this.concatMsg(format, args));
        }
    }

    @Override
    public void error(Throwable cause, String format, Object... args) {
        org.slf4j.Logger logger = this.getLogger();
        if (logger.isErrorEnabled()) {
            logger.error(this.concatMsg(format, args), cause);
        }
    }
}
