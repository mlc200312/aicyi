package com.aichuangyi.base.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mr.Min
 * @description 时间工具类
 * @date 14:27
 **/
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static Date parseDate(String date, String pattern) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            throw new UnsupportedOperationException("parse date error", e);
        }
    }

    public static Date parseDate(String date) {
        return parseDate(date, DEFAULT_PATTERN);
    }

    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static String formatDate(Date date) {
        return formatDate(date, DEFAULT_PATTERN);
    }
}
