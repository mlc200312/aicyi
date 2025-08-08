package com.aichuangyi.base.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author Mr.Min
 * @description LocalDateTime 工具类
 * @date 2025/8/7
 **/
public class DateTimeUtils {
    // 日期格式
    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_TIME_PATTERN = DEFAULT_PATTERN;
    public static final String ISO_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * String 转换为 LocalDateTime
     *
     * @param dateTime
     * @return
     */
    public static LocalDateTime toLDateTime(String dateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dateTimeFormatter);
        return localDateTime;
    }

    /**
     * String 转换为 LocalDateTime
     *
     * @param dateTime
     * @return
     */
    public static LocalDateTime toLDateTime(String dateTime) {
        return toLDateTime(dateTime, DEFAULT_PATTERN);
    }

    /**
     * long（毫秒数）转换为 LocalDateTime
     *
     * @param dateTime
     * @return
     */
    public static LocalDateTime toLDateTime(long dateTime) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.systemDefault());
    }

    /**
     * Date 转换为 LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime toLDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * LocalDate 转换为 LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime toLDateTime(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.parse("00:00:00"));
    }

    /**
     * 格式化为字符串
     *
     * @param dateTime
     * @param pattern
     * @return
     */
    public static String formatLDateTime(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return dateTimeFormatter.format(dateTime);
    }

    /**
     * 格式化为字符串
     *
     * @param dateTime
     * @return
     */
    public static String formatLDateTime(LocalDateTime dateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);
        return dateTimeFormatter.format(dateTime);
    }

    /**
     * 转换为毫秒数
     *
     * @param dateTime
     * @return
     */
    public static long toLong(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 转换为 Date
     *
     * @param dateTime
     * @return
     */
    public static Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}