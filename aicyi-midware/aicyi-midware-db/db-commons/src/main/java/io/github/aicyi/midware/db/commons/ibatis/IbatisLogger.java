package io.github.aicyi.midware.db.commons.ibatis;

import io.github.aicyi.commons.logging.Logger;
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
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void error(String s, Throwable e) {
        logger.error(buildMessage(s), e);
    }

    @Override
    public void error(String s) {
        logger.error(buildMessage(s));
    }

    @Override
    public void debug(String s) {
        logger.info(buildMessage(s));
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