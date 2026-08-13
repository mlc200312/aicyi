package io.github.aicyi.commons.core.logging;

/**
 * @author Mr.Min
 * @description 通用接日志口
 * @date 2019-06-23
 **/
public interface Logger {

    /**
     * 是否启用了调试
     *
     * @return boolean
     */
    boolean isDebugEnabled();

    /**
     * 是否启用了跟踪
     *
     * @return boolean
     */
    boolean isTraceEnabled();

    /**
     * 跟踪
     *
     * @param obj
     */
    void trace(Object obj);

    /**
     * 跟踪
     *
     * @param format 格式
     * @param arg    参数
     */
    void trace(String format, Object... arg);

    /**
     * 跟踪
     *
     * @param cause  异常
     * @param format 格式
     * @param arg    参数
     */
    void trace(Throwable cause, String format, Object... arg);

    /**
     * 调试
     *
     * @param obj
     */
    void debug(Object obj);

    /**
     * 调试
     *
     * @param format 格式
     * @param arg    参数
     */
    void debug(String format, Object... arg);

    /**
     * 调试
     *
     * @param cause  异常
     * @param format 格式
     * @param arg    参数
     */
    void debug(Throwable cause, String format, Object... arg);

    /**
     * 信息
     *
     * @param obj
     */
    void info(Object obj);

    /**
     * 信息
     *
     * @param format 格式
     * @param arg    参数
     */
    void info(String format, Object... arg);

    /**
     * 信息
     *
     * @param cause  异常
     * @param format 格式
     * @param arg    参数
     */
    void info(Throwable cause, String format, Object... arg);

    /**
     * 警告
     *
     * @param obj
     */
    void warn(Object obj);

    /**
     * 警告
     *
     * @param format 格式
     * @param arg    参数
     */
    void warn(String format, Object... arg);

    /**
     * 警告
     *
     * @param cause  异常
     * @param format 格式
     * @param arg    参数
     */
    void warn(Throwable cause, String format, Object... arg);

    /**
     * 错误
     *
     * @param obj
     */
    void error(Object obj);

    /**
     * 错误
     *
     * @param format 格式
     * @param arg    参数
     */
    void error(String format, Object... arg);

    /**
     * 错误
     *
     * @param cause  异常
     * @param format 格式
     * @param arg    参数
     */
    void error(Throwable cause, String format, Object... arg);
}
