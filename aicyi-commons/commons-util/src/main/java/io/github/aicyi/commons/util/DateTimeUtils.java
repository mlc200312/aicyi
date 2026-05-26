package io.github.aicyi.commons.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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

    // ========================= formatter =========================

    private static final Map<String, DateTimeFormatter> FORMATTERS = new ConcurrentHashMap<>(8);

    static {
        register(DATE_PATTERN);
        register(DATE_TIME_PATTERN);
        register(DATE_TIME_MILLIS_PATTERN);
    }

    private static void register(String pattern) {
        FORMATTERS.put(pattern, DateTimeFormatter.ofPattern(pattern));
    }

    private static DateTimeFormatter formatter(String pattern) {
        DateTimeFormatter formatter = FORMATTERS.get(pattern);

        if (formatter == null) {
            throw new IllegalArgumentException(
                    "Unsupported pattern: " + pattern
            );
        }

        return formatter;
    }

    // ========================= clock =========================

    private static volatile Clock CLOCK = Clock.systemDefaultZone();

    public static void setClock(Clock clock) {
        CLOCK = Objects.requireNonNull(clock);
    }

    public static void resetClock() {
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

    public static LocalDate today() {
        return LocalDate.now(CLOCK);
    }

    // ========================= convert =========================

    public static LocalDateTime toLocalDateTime(Instant instant) {
        Objects.requireNonNull(instant);

        return LocalDateTime.ofInstant(
                instant,
                zone()
        );
    }

    public static Instant toInstant(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime);

        return localDateTime
                .atZone(zone())
                .toInstant();
    }

    public static long toEpochMilli(LocalDateTime localDateTime) {
        return toInstant(localDateTime).toEpochMilli();
    }

    // ========================= parse =========================

    public static LocalDateTime parse(
            String text,
            String pattern
    ) {

        Objects.requireNonNull(text);

        return LocalDateTime.parse(
                text.trim(),
                formatter(pattern)
        );
    }

    public static LocalDateTime parseAuto(String text) {

        Objects.requireNonNull(text);

        String value = text.trim();

        int len = value.length();

        try {

            // yyyy-MM-dd
            if (len == 10) {
                return LocalDate
                        .parse(value, formatter(DATE_PATTERN))
                        .atStartOfDay();
            }

            // yyyy-MM-dd HH:mm:ss
            if (len == 19 && value.charAt(10) == ' ') {
                return LocalDateTime.parse(
                        value,
                        formatter(DATE_TIME_PATTERN)
                );
            }

            // yyyy-MM-dd HH:mm:ss.SSS
            if (len >= 21 && value.contains(".")) {
                return LocalDateTime.parse(
                        value,
                        formatter(DATE_TIME_MILLIS_PATTERN)
                );
            }

            // ISO
            if (value.contains("T")) {
                return OffsetDateTime
                        .parse(value)
                        .toLocalDateTime();
            }

        } catch (Exception e) {
            throw new DateTimeParseException(
                    "Unsupported datetime format",
                    value,
                    0
            );
        }

        throw new DateTimeParseException(
                "Unsupported datetime format",
                value,
                0
        );
    }

    // ========================= format =========================

    public static String format(
            LocalDateTime time,
            String pattern
    ) {

        Objects.requireNonNull(time);

        return formatter(pattern).format(time);
    }

    // ========================= compare =========================

    public static boolean isBetween(
            LocalDateTime source,
            LocalDateTime start,
            LocalDateTime end
    ) {

        Objects.requireNonNull(source);
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);

        return !source.isBefore(start) && !source.isAfter(end);
    }

    // ========================= truncate =========================

    public static LocalDateTime truncateToMinute(LocalDateTime time) {

        Objects.requireNonNull(time);

        return time.truncatedTo(ChronoUnit.MINUTES);
    }

}