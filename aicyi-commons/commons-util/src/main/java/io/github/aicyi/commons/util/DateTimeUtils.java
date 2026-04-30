package io.github.aicyi.commons.util;

import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * @author Mr.Min
 * @description LocalDateTime 工具类
 * @date 2025/8/7
 **/
public class DateTimeUtils {
    // 日期格式
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String ISO_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_PATTERN = DATE_TIME_PATTERN;

    /**
     * String 转换为 LocalDateTime
     *
     * @param dateTime
     * @return
     */
    public static LocalDateTime toLDateTime(String dateTime, String pattern) {
        if (dateTime == null || dateTime.trim().isEmpty()) {
            return null;
        }
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
        if (Objects.isNull(date)) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * LocalDate 转换为 LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime toLDateTime(LocalDate date) {
        if (Objects.isNull(date)) {
            return null;
        }
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
        if (Objects.isNull(dateTime)) {
            return null;
        }
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
        return formatLDateTime(dateTime, DEFAULT_PATTERN);
    }

    /**
     * 转换为毫秒数
     *
     * @param dateTime
     * @return
     */
    public static Long toLong(LocalDateTime dateTime) {
        if (Objects.isNull(dateTime)) {
            return null;
        }
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 转换为 Date
     *
     * @param dateTime
     * @return
     */
    public static Date toDate(LocalDateTime dateTime) {
        if (Objects.isNull(dateTime)) {
            return null;
        }
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}