package io.github.aicyi.test.commons.util;

import io.github.aicyi.commons.util.DateTimeUtils;
import org.junit.After;
import org.junit.Test;

import java.time.*;

import static org.junit.Assert.*;

/**
 * DateTimeUtils 测试类
 *
 * @author Mr.Min
 */
public class DateTimeUtilsTest {

    // ========================= clock =========================

    @After
    public void tearDown() {

//        DateTimeUtils.resetClock();
    }

    /**
     * 测试固定时间
     */
    @Test
    public void testFixedClock() {

//        Instant fixedInstant = Instant.parse("2026-05-26T10:15:30Z");
//
//        Clock fixedClock = Clock.fixed(
//                fixedInstant,
//                ZoneId.of("Asia/Shanghai")
//        );
//
//        DateTimeUtils.setClock(fixedClock);
//
//        assertEquals(
//                fixedInstant,
//                DateTimeUtils.now()
//        );
//
//        assertEquals(
//                2026,
//                DateTimeUtils.today().getYear()
//        );
    }

    // ========================= now =========================

    /**
     * 测试当前毫秒时间
     */
    @Test
    public void testNowMillis() {

        long millis = DateTimeUtils.nowMillis();

        assertTrue(millis > 0);
    }

    /**
     * 测试当前秒时间
     */
    @Test
    public void testNowSeconds() {

        long seconds = DateTimeUtils.nowSeconds();

        assertTrue(seconds > 0);
    }

    // ========================= convert =========================

    /**
     * Instant 转 LocalDateTime
     */
    @Test
    public void testToLocalDateTime() {

        Instant instant = Instant.parse("2026-05-26T12:00:00Z");

        LocalDateTime dateTime = DateTimeUtils.toLocalDateTime(instant);

        assertNotNull(dateTime);
    }

    /**
     * LocalDateTime 转 Instant
     */
    @Test
    public void testToInstant() {

        LocalDateTime time = LocalDateTime.of(
                2026,
                5,
                26,
                12,
                0
        );

        Instant instant = DateTimeUtils.toInstant(time);

        assertNotNull(instant);
    }

    /**
     * LocalDateTime 转 epoch milli
     */
    @Test
    public void testToEpochMilli() {

        LocalDateTime time = LocalDateTime.of(
                2026,
                5,
                26,
                12,
                0
        );

        long epochMilli = DateTimeUtils.toEpochMilli(time);

        assertTrue(epochMilli > 0);
    }

    // ========================= parse =========================

    /**
     * 解析日期
     */
    @Test
    public void testParseDate() {

        LocalDate date = DateTimeUtils.parseDate(
                "2026-05-26",
                DateTimeUtils.DATE_PATTERN
        );

        assertEquals(2026, date.getYear());
        assertEquals(5, date.getMonthValue());
        assertEquals(26, date.getDayOfMonth());
    }

    /**
     * 解析时间
     */
    @Test
    public void testParseDateTime() {

        LocalDateTime time = DateTimeUtils.parseDateTime(
                "2026-05-26 12:30:45",
                DateTimeUtils.DATE_TIME_PATTERN
        );

        assertEquals(12, time.getHour());
        assertEquals(30, time.getMinute());
        assertEquals(45, time.getSecond());
    }

    /**
     * 自动解析 yyyy-MM-dd
     */
    @Test
    public void testParseAutoDate() {

        LocalDateTime time = DateTimeUtils.parseAuto(
                "2026-05-26"
        );

        assertEquals(0, time.getHour());
        assertEquals(0, time.getMinute());
    }

    /**
     * 自动解析 yyyy-MM-dd HH:mm:ss
     */
    @Test
    public void testParseAutoDateTime() {

        LocalDateTime time = DateTimeUtils.parseAuto(
                "2026-05-26 12:30:45"
        );

        assertEquals(12, time.getHour());
    }

    /**
     * 自动解析毫秒格式
     */
    @Test
    public void testParseAutoMillis() {

        LocalDateTime time = DateTimeUtils.parseAuto(
                "2026-05-26 12:30:45.123"
        );

        assertEquals(
                123_000_000,
                time.getNano()
        );
    }

    /**
     * 自动解析 ISO 时间
     */
    @Test
    public void testParseAutoIso() {

        LocalDateTime time = DateTimeUtils.parseAuto(
                "2026-05-26T12:30:45+08:00"
        );

        assertEquals(12, time.getHour());
    }

    /**
     * 自动解析 epoch milli
     */
    @Test
    public void testParseAutoEpochMilli() {

        long millis = System.currentTimeMillis();

        LocalDateTime time = DateTimeUtils.parseAuto(
                String.valueOf(millis)
        );

        assertNotNull(time);
    }

    /**
     * 自动解析 epoch second
     */
    @Test
    public void testParseAutoEpochSecond() {

        long seconds = Instant.now().getEpochSecond();

        LocalDateTime time = DateTimeUtils.parseAuto(
                String.valueOf(seconds)
        );

        assertNotNull(time);
    }

    /**
     * 解析非法时间
     */
    @Test
    public void testParseInvalid() {

        assertNull(
                DateTimeUtils.parseAuto(
                        "abc"
                )
        );
    }

    // ========================= format =========================

    /**
     * 格式化时间
     */
    @Test
    public void testFormat() {

        LocalDateTime time = LocalDateTime.of(
                2026,
                5,
                26,
                12,
                30,
                45
        );

        String value = DateTimeUtils.format(time, DateTimeUtils.DATE_TIME_PATTERN);

        assertEquals("2026-05-26 12:30:45", value);
    }

    // ========================= compare =========================

    /**
     * 测试时间区间
     */
    @Test
    public void testIsBetween() {

        LocalDateTime source = LocalDateTime.of(
                2026,
                5,
                26,
                12,
                0
        );

        LocalDateTime start = LocalDateTime.of(
                2026,
                5,
                26,
                10,
                0
        );

        LocalDateTime end = LocalDateTime.of(
                2026,
                5,
                26,
                14,
                0
        );

        assertTrue(
                DateTimeUtils.isBetween(
                        source,
                        start,
                        end
                )
        );
    }

    /**
     * 测试是否今天
     */
    @Test
    public void testIsToday() {

        assertTrue(
                DateTimeUtils.isToday(LocalDateTime.now())
        );
    }

    // ========================= duration =========================

    /**
     * 测试相差天数
     */
    @Test
    public void testBetweenDays() {

        long days =
                DateTimeUtils.betweenDays(
                        LocalDate.of(2026, 5, 1),
                        LocalDate.of(2026, 5, 10)
                );

        assertEquals(9, days);
    }

    /**
     * 测试相差秒数
     */
    @Test
    public void testBetweenSeconds() {

        LocalDateTime start = LocalDateTime.of(
                2026,
                5,
                26,
                12,
                0
        );

        LocalDateTime end = LocalDateTime.of(
                2026,
                5,
                26,
                12,
                1
        );

        long seconds = DateTimeUtils.betweenSeconds(
                start,
                end
        );

        assertEquals(60, seconds);
    }

    // ========================= truncate =========================

    /**
     * 截断到分钟
     */
    @Test
    public void testTruncateToMinute() {

        LocalDateTime time = LocalDateTime.of(
                2026,
                5,
                26,
                12,
                30,
                45
        );

        LocalDateTime result = DateTimeUtils.truncateToMinute(time);

        assertEquals(0, result.getSecond());
    }

    /**
     * 获取开始时间
     */
    @Test
    public void testStartOfDay() {

        LocalDateTime start = DateTimeUtils.startOfDay(
                LocalDate.of(2026, 5, 26)
        );

        assertEquals(0, start.getHour());
        assertEquals(0, start.getMinute());
    }

    /**
     * 获取结束时间
     */
    @Test
    public void testEndOfDay() {

        LocalDateTime end = DateTimeUtils.endOfDay(
                LocalDate.of(2026, 5, 26)
        );

        assertEquals(23, end.getHour());
        assertEquals(59, end.getMinute());
    }

}