package io.github.aicyi.commons.util.date;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Mr.Min
 * @description 时间工具类（旧 Date API 兼容层，内部统一委托 {@link DateTimeUtils}，新代码请直接使用 DateTimeUtils）。
 * <p>
 * 注意：不再继承 org.apache.commons.lang3.time.DateUtils（其构造器自 commons-lang3 3.14.0 起已废弃，官方不鼓励继承）；
 * 如需 lang3 的日期计算能力（addDays、truncate 等），请直接使用 org.apache.commons.lang3.time.DateUtils 的静态方法。
 * @date 14:27
 **/
public final class DateUtils {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private DateUtils() {
    }

    public static Date parseDate(String date, String pattern) {
        if (date == null || date.trim().isEmpty()) {
            return null;
        }
        try {
            LocalDateTime dateTime;
            try {
                dateTime = DateTimeUtils.parseDateTime(date, pattern);
            } catch (DateTimeException e) {
                // pattern 不含时间部分（如 yyyy-MM-dd）：按日期解析并补零点
                dateTime = DateTimeUtils.parseDate(date, pattern).atStartOfDay();
            }
            return Date.from(dateTime.atZone(DateTimeUtils.zone()).toInstant());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "parse date error, value: " + date + ", pattern: " + pattern, e);
        }
    }

    public static Date parseDate(String date) {
        return parseDate(date, DEFAULT_PATTERN);
    }

    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), DateTimeUtils.zone());
        return DateTimeUtils.format(dateTime, pattern);
    }

    public static String formatDate(Date date) {
        return formatDate(date, DEFAULT_PATTERN);
    }
}
