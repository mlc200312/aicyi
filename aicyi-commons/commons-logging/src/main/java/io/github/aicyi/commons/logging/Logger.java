package io.github.aicyi.commons.logging;

/**
 * @author Mr.Min
 * @description 通用接日志口
 * @date 2019-06-23
 **/
public interface Logger {

    /**
     * 跟踪
     *
     * @param obj
     */
    void trace(Object obj);

    /**
     * 跟踪
     *
     * @param format
     * @param arg
     */
    void trace(String format, Object... arg);

    /**
     * 跟踪
     *
     * @param cause
     * @param format
     * @param arg
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
     * @param format
     * @param arg
     */
    void debug(String format, Object... arg);

    /**
     * 调试
     *
     * @param cause
     * @param format
     * @param arg
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
     * @param format
     * @param arg
     */
    void info(String format, Object... arg);

    /**
     * 信息
     *
     * @param cause
     * @param format
     * @param arg
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
     * @param format
     * @param arg
     */
    void warn(String format, Object... arg);

    /**
     * 警告
     *
     * @param cause
     * @param format
     * @param arg
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
     * @param format
     * @param arg
     */
    void error(String format, Object... arg);

    /**
     * 错误
     *
     * @param cause
     * @param format
     * @param arg
     */
    void error(Throwable cause, String format, Object... arg);
}
