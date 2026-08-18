package io.github.aicyi.commons.util;

import io.github.aicyi.commons.lang.Assert;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.math.NumberUtils.isDigits;

/**
 * @author Mr.Min
 * @description 时间工具类
 * @date 2026/5/26
 **/
public final class DateTimeUtils {

    private DateTimeUtils() {
    }

    // ========================= patterns =========================

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_TIME_MILLIS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";


    // ========================= formatter cache =========================

    private static final String[] PATTERNS = {
            DATE_PATTERN,
            DATE_TIME_PATTERN,
            DATE_TIME_MILLIS_PATTERN
    };

    private static final Set<String> CACHED_PATTERNS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(PATTERNS)));

    private static final Map<String, DateTimeFormatter> FORMATTERS = new ConcurrentHashMap<>(8);

    private static DateTimeFormatter formatter(String pattern) {

        // 仅内置 pattern 入缓存，避免外部任意 pattern 撑大缓存
        if (CACHED_PATTERNS.contains(pattern)) {

            return FORMATTERS.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
        }

        return DateTimeFormatter.ofPattern(pattern);
    }

    // ========================= clock =========================

    private static volatile Clock CLOCK = Clock.systemDefaultZone();

    static void setClock(Clock clock) {
        CLOCK = clock;
    }

    static void resetClock() {
        CLOCK = Clock.systemDefaultZone();
    }

    public static ZoneId zone() {
        return CLOCK.getZone();
    }

    // ========================= now =========================

    public static Instant now() {
        return CLOCK.instant();
    }

    public static long nowMillis() {
        return CLOCK.millis();
    }

    public static long nowSeconds() {
        return now().getEpochSecond();
    }

    public static LocalDate today() {
        return LocalDate.now(CLOCK);
    }

    public static LocalDateTime nowDateTime() {
        return LocalDateTime.now(CLOCK);
    }

    // ========================= convert =========================

    public static LocalDateTime toLocalDateTime(Instant instant) {

        return LocalDateTime.ofInstant(instant, zone());
    }

    public static Instant toInstant(LocalDateTime time) {

        return time.atZone(zone()).toInstant();
    }

    public static long toEpochMilli(LocalDateTime time) {

        return toInstant(time).toEpochMilli();
    }

    // ========================= parse =========================

    public static LocalDate parseDate(String text, String pattern) {

        return LocalDate.parse(text.trim(), formatter(pattern));
    }

    public static LocalDateTime parseDateTime(String text, String pattern) {

        return LocalDateTime.parse(text.trim(), formatter(pattern));
    }

    /**
     * 自动解析
     */
    public static LocalDateTime parseAuto(String value) {

        if (value == null || value.isEmpty()) {
            return null;
        }

        String text = value.trim();

        int len = text.length();

        // epoch milli
        if (len == 13 && isDigits(text)) {

            return Instant.ofEpochMilli(Long.parseLong(text))
                    .atZone(zone())
                    .toLocalDateTime();
        }

        // epoch second
        if (len == 10 && isDigits(text)) {

            return Instant.ofEpochSecond(Long.parseLong(text))
                    .atZone(zone())
                    .toLocalDateTime();
        }

        // yyyy-MM-dd
        if (len == 10) {

            try {

                return LocalDate.parse(text, formatter(DATE_PATTERN)).atStartOfDay();

            } catch (DateTimeParseException e) {

                return null;
            }
        }

        // yyyy-MM-dd HH:mm:ss
        if (len == 19) {

            return parseQuietly(text, DATE_TIME_PATTERN);
        }

        // yyyy-MM-dd HH:mm:ss.SSS
        if (len == 23) {

            return parseQuietly(text, DATE_TIME_MILLIS_PATTERN);
        }

        // offset datetime
        try {

            return OffsetDateTime
                    .parse(text)
                    .atZoneSameInstant(zone())
                    .toLocalDateTime();

        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * 自动解析内部使用：解析失败返回 null，不向上抛异常
     */
    private static LocalDateTime parseQuietly(String text, String pattern) {

        try {

            return LocalDateTime.parse(text, formatter(pattern));

        } catch (DateTimeParseException e) {

            return null;
        }
    }

    // ========================= format =========================

    public static String format(LocalDateTime time, String pattern) {

        Assert.notNull(time, "time");

        return formatter(pattern).format(time);
    }

    public static String formatNow(String pattern) {

        return format(nowDateTime(), pattern);
    }

    // ========================= compare =========================

    public static boolean isBetween(LocalDateTime source, LocalDateTime start, LocalDateTime end) {

        return !source.isBefore(start) && !source.isAfter(end);
    }

    public static boolean isToday(LocalDateTime time) {

        return time.toLocalDate().equals(today());
    }

    // ========================= calculate =========================

    public static long betweenDays(LocalDate start, LocalDate end) {

        return ChronoUnit.DAYS.between(start, end);
    }

    public static long betweenSeconds(LocalDateTime start, LocalDateTime end) {

        return ChronoUnit.SECONDS.between(start, end);
    }

    // ========================= truncate =========================

    public static LocalDateTime truncateToMinute(LocalDateTime time) {

        return time.truncatedTo(ChronoUnit.MINUTES);
    }

    public static LocalDateTime startOfDay(LocalDate date) {

        return date.atStartOfDay();
    }

    public static LocalDateTime endOfDay(LocalDate date) {

        return date.atTime(LocalTime.of(23, 59, 59, 999));
    }
}