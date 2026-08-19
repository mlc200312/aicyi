package io.github.aicyi.midware.db.commons.ibatis;

import io.github.aicyi.commons.core.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.logging.LoggerType;
import org.apache.ibatis.logging.Log;

/**
 * @author Mr.Min
 * @description Mybatis 日志实现
 * @date 2025/10/20
 **/
public class IbatisLogger implements Log {

    private final String className;
    private final Logger logger; // 使用你喜欢的日志框架

    public IbatisLogger(String className) {
        this.className = className;
        // 初始化日志框架
        this.logger = LoggerFactory.getLogger(LoggerType.DAO);
    }

    @Override
    public boolean isDebugEnabled() {
        // 委托 DAO logger 的真实级别判断：日志级别关闭时 MyBatis 不产出 debug 日志，避免生产全量 SQL 落盘
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void error(String s, Throwable cause) {
        logger.error(buildMessage(s), cause);
    }

    @Override
    public void error(String s) {
        logger.error(buildMessage(s));
    }

    @Override
    public void debug(String s) {
        // 按 debug 级别输出，由 logback 对 dao 日志的级别配置统一控制，不再升为 info
        logger.debug(buildMessage(s));
    }

    @Override
    public void trace(String s) {
        logger.trace(buildMessage(s));
    }

    @Override
    public void warn(String s) {
        logger.warn(buildMessage(s));
    }

    private String buildMessage(String message) {
        // 自定义日志格式
        return String.format("[MyBatis] %s - %s", className, message);
    }
}