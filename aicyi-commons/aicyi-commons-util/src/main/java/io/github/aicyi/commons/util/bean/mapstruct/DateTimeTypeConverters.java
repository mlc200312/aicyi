package io.github.aicyi.commons.util.bean.mapstruct;

import io.github.aicyi.commons.util.date.DateUtils;
import io.github.aicyi.commons.util.date.DateTimeUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期时间类型转换器（供各 MapStruct Mapper 通过 {@code uses} 引用，
 * 以具体类形态提供，MapStruct 通过无参构造实例化）
 * <p>
 * 等价于原 Orika 的 LocalDateTimeMapperConverter / LocalDateMapperConverter / DateMapperConverter /
 * TimestampMapperConverter / Date2LocalDateTimeMapperConverter / Timestamp2LocalDateTimeMapperConverter，
 * 字符串格式与原实现保持一致：
 * <ul>
 *     <li>LocalDateTime 与 String：yyyy-MM-dd HH:mm:ss（解析自动兼容 .SSS / epoch 毫秒等）</li>
 *     <li>LocalDate 与 String：yyyy-MM-dd</li>
 *     <li>Date 与 String：yyyy-MM-dd HH:mm:ss</li>
 *     <li>Timestamp 与 String：epoch 毫秒数字符串</li>
 * </ul>
 *
 * @author Mr.Min
 * @date 2026-09-02
 */
public class DateTimeTypeConverters {

    // ==================== LocalDateTime <-> String ====================

    public String formatDateTime(LocalDateTime value) {
        return value == null ? null : DateTimeUtils.format(value, DateTimeUtils.DATE_TIME_PATTERN);
    }

    public LocalDateTime parseDateTime(String value) {
        return DateTimeUtils.parseAuto(value);
    }

    // ==================== LocalDate <-> String ====================

    public String formatLocalDate(LocalDate value) {
        return value == null ? null : value.format(DateTimeFormatter.ofPattern(DateTimeUtils.DATE_PATTERN));
    }

    public LocalDate parseLocalDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(value.trim(), DateTimeFormatter.ofPattern(DateTimeUtils.DATE_PATTERN));
    }

    // ==================== Date <-> String ====================

    public String formatJavaDate(Date value) {
        return DateUtils.formatDate(value);
    }

    public Date parseJavaDate(String value) {
        return DateUtils.parseDate(value);
    }

    // ==================== Timestamp <-> String ====================

    public String formatTimestamp(Timestamp value) {
        return value == null ? null : String.valueOf(value.getTime());
    }

    public Timestamp parseTimestamp(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return new Timestamp(Long.parseLong(value.trim()));
    }

    // ==================== Date <-> LocalDateTime ====================

    public LocalDateTime toLocalDateTime(Date value) {
        return value == null ? null : DateTimeUtils.toLocalDateTime(value.toInstant());
    }

    public Date toDate(LocalDateTime value) {
        return value == null ? null : new Date(DateTimeUtils.toEpochMilli(value));
    }

    // ==================== Timestamp <-> LocalDateTime ====================

    public LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }

    public Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : new Timestamp(DateTimeUtils.toEpochMilli(value));
    }
}
