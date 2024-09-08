package com.scaffold.core.base.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static com.scaffold.core.base.constant.GlobalConstant.*;

/**
 * {@link cn.hutool.core.date}包下有更详细的<br/> 这里是一个简化版的LocalDateTime工具类
 * todo 替换几个字段
 *
 * @author aries
 * @since 2022/4/29
 */
public interface TimeUtil {

    /**
     * 精确到s
     */
    DateTimeFormatter SF = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);

    /**
     * 精确到天
     */
    DateTimeFormatter DAY_F = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);

    DateTimeFormatter TIME_F = DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT);

    static String format2s(LocalDateTime time) {
        return SF.format(time);
    }


    static String format2day(LocalDateTime time) {
        return DAY_F.format(time);
    }


    static LocalDateTime sStr2time(String pattern) {
        return LocalDateTime.parse(pattern, SF);
    }


    static LocalDateTime dayStr2time(String pattern) {
        return LocalDateTime.parse(pattern, DAY_F);
    }

    /**
     * LocalDateTime转毫秒值
     *
     * @param time
     * @param zoneId
     * @return
     */
    static long time2msValue(LocalDateTime time, ZoneId zoneId) {
        return time.atZone(zoneId).toInstant().toEpochMilli();
    }

    /**
     * LocalDateTime转毫秒值,使用上海时区
     *
     * @param time
     * @return
     */
    static long time2msValue(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 毫秒时间戳转LocalDateTime
     *
     * @param milliseconds
     * @return
     */
    static LocalDateTime longMs2time(Long milliseconds) {
        // 将时间戳转为当前时间:我们使用东八区
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneOffset.of("+8"));
    }

    /**
     * 秒时间戳转localDateTime
     *
     * @param seconds
     * @return
     */
    static LocalDateTime longS2time(Long seconds) {
        // 将时间戳转为当前时间:我们使用东八区
        return LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.ofHours(8));
    }
}
