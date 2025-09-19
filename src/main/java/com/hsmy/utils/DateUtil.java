package com.hsmy.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * 日期时间转换工具类
 * 提供Date与LocalDate、LocalDateTime之间的互转功能
 *
 * @author HSMY
 * @date 2025/09/19
 */
public class DateUtil {

    /**
     * 默认时区，使用系统默认时区
     */
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    /**
     * Date转LocalDate
     *
     * @param date 待转换的Date对象
     * @return LocalDate对象，如果输入为null则返回null
     */
    public static LocalDate dateToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(DEFAULT_ZONE_ID)
                .toLocalDate();
    }

    /**
     * LocalDate转Date
     *
     * @param localDate 待转换的LocalDate对象
     * @return Date对象，如果输入为null则返回null
     */
    public static Date localDateToDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * Date转LocalDateTime
     *
     * @param date 待转换的Date对象
     * @return LocalDateTime对象，如果输入为null则返回null
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(DEFAULT_ZONE_ID)
                .toLocalDateTime();
    }

    /**
     * LocalDateTime转Date
     *
     * @param localDateTime 待转换的LocalDateTime对象
     * @return Date对象，如果输入为null则返回null
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * LocalDate转LocalDateTime（时间设为当天开始：00:00:00）
     *
     * @param localDate 待转换的LocalDate对象
     * @return LocalDateTime对象，如果输入为null则返回null
     */
    public static LocalDateTime localDateToLocalDateTime(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay();
    }

    /**
     * LocalDate转LocalDateTime（指定时分秒）
     *
     * @param localDate 待转换的LocalDate对象
     * @param hour      小时（0-23）
     * @param minute    分钟（0-59）
     * @param second    秒（0-59）
     * @return LocalDateTime对象，如果输入为null则返回null
     */
    public static LocalDateTime localDateToLocalDateTime(LocalDate localDate, int hour, int minute, int second) {
        if (localDate == null) {
            return null;
        }
        return localDate.atTime(hour, minute, second);
    }

    /**
     * LocalDateTime转LocalDate
     *
     * @param localDateTime 待转换的LocalDateTime对象
     * @return LocalDate对象，如果输入为null则返回null
     */
    public static LocalDate localDateTimeToLocalDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toLocalDate();
    }

    /**
     * 根据指定时区进行Date转LocalDate
     *
     * @param date   待转换的Date对象
     * @param zoneId 时区ID
     * @return LocalDate对象，如果输入为null则返回null
     */
    public static LocalDate dateToLocalDate(Date date, ZoneId zoneId) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(zoneId)
                .toLocalDate();
    }

    /**
     * 根据指定时区进行LocalDate转Date
     *
     * @param localDate 待转换的LocalDate对象
     * @param zoneId    时区ID
     * @return Date对象，如果输入为null则返回null
     */
    public static Date localDateToDate(LocalDate localDate, ZoneId zoneId) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(zoneId).toInstant());
    }

    /**
     * 根据指定时区进行Date转LocalDateTime
     *
     * @param date   待转换的Date对象
     * @param zoneId 时区ID
     * @return LocalDateTime对象，如果输入为null则返回null
     */
    public static LocalDateTime dateToLocalDateTime(Date date, ZoneId zoneId) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(zoneId)
                .toLocalDateTime();
    }

    /**
     * 根据指定时区进行LocalDateTime转Date
     *
     * @param localDateTime 待转换的LocalDateTime对象
     * @param zoneId        时区ID
     * @return Date对象，如果输入为null则返回null
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime, ZoneId zoneId) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }

    /**
     * 获取当前日期的Date对象
     *
     * @return 当前日期的Date对象
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * 获取当前日期的LocalDate对象
     *
     * @return 当前日期的LocalDate对象
     */
    public static LocalDate getCurrentLocalDate() {
        return LocalDate.now();
    }

    /**
     * 获取当前日期时间的LocalDateTime对象
     *
     * @return 当前日期时间的LocalDateTime对象
     */
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 将时间戳转换为Date
     *
     * @param timestamp 时间戳（毫秒）
     * @return Date对象
     */
    public static Date timestampToDate(long timestamp) {
        return new Date(timestamp);
    }

    /**
     * 将时间戳转换为LocalDateTime
     *
     * @param timestamp 时间戳（毫秒）
     * @return LocalDateTime对象
     */
    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), DEFAULT_ZONE_ID);
    }

    /**
     * 将时间戳转换为LocalDate
     *
     * @param timestamp 时间戳（毫秒）
     * @return LocalDate对象
     */
    public static LocalDate timestampToLocalDate(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), DEFAULT_ZONE_ID).toLocalDate();
    }

    /**
     * 将Date转换为时间戳
     *
     * @param date Date对象
     * @return 时间戳（毫秒），如果输入为null则返回0
     */
    public static long dateToTimestamp(Date date) {
        if (date == null) {
            return 0L;
        }
        return date.getTime();
    }

    /**
     * 将LocalDateTime转换为时间戳
     *
     * @param localDateTime LocalDateTime对象
     * @return 时间戳（毫秒），如果输入为null则返回0
     */
    public static long localDateTimeToTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return 0L;
        }
        return localDateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    /**
     * 将LocalDate转换为时间戳（当天开始时间）
     *
     * @param localDate LocalDate对象
     * @return 时间戳（毫秒），如果输入为null则返回0
     */
    public static long localDateToTimestamp(LocalDate localDate) {
        if (localDate == null) {
            return 0L;
        }
        return localDate.atStartOfDay(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }
}