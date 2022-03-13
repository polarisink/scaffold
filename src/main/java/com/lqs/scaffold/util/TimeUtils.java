package com.lqs.scaffold.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime工具类
 */
public class TimeUtils {
    /**
     * 精确到s
     */
    public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * 精确到天
     */
    public static final DateTimeFormatter dayFm = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    /**
     * 精确到ms
     */
    public static final DateTimeFormatter dtfm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static String format2s(LocalDateTime time) {
        return dtf.format(time);
    }

    public static String format2ms(LocalDateTime time) {
        return dtfm.format(time);
    }

    public static LocalDateTime s2time(String pattern) {
        return LocalDateTime.parse(pattern, dtf);
    }

    public static LocalDateTime ms2time(String pattern) {
        return LocalDateTime.parse(pattern, dtfm);
    }
}
