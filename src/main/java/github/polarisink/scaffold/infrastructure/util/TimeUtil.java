package github.polarisink.scaffold.infrastructure.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * {@link cn.hutool.core.date}包下有更详细的<br/> 这里是一个简化版的LocalDateTime工具类
 *
 * @author aries
 * @date 2022/4/29
 */
public class TimeUtil {

    /**
     * 上海zoneId
     */
    public static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");
    public static final String S_F_STR = "yyyy-MM-dd HH:mm:ss";
    public static final String S_F_STR2 = "yyyy-MM-dd_HH:mm:ss";
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String DAY_F_STR = "yyyy-MM-dd";
    public static final String MS_F_STR = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_F_STR = "MM-dd";
    public static final String DAY_SEP = "yyyy-MM-dd/HHmmss";
    public static final String TIME_ZONE = "GMT+8";

    public static final String CHINESE_DATE_PATTERN = "MM月dd日HH时mm分";
    public static final String CHINESE_MINUTES_PATTERN = "HH时mm分";


    /**
     * 精确到s
     */
    public static final DateTimeFormatter SF = DateTimeFormatter.ofPattern(S_F_STR);
    /**
     * 精确到s,控股替换为下划线
     */
    public static final DateTimeFormatter SF_ = DateTimeFormatter.ofPattern(S_F_STR2);
    /**
     * 精确到天
     */
    public static final DateTimeFormatter DAY_F = DateTimeFormatter.ofPattern(DAY_F_STR);

    public static final DateTimeFormatter TIME_F = DateTimeFormatter.ofPattern(TIME_PATTERN);


    /**
     * 只带日期
     */
    public static final DateTimeFormatter DATE_F = DateTimeFormatter.ofPattern(DATE_F_STR);
    /**
     * 精确到ms
     */
    public static final DateTimeFormatter MS_F = DateTimeFormatter.ofPattern(MS_F_STR);
    /**
     * 用于目录分割
     */
    public static final DateTimeFormatter DAY_SEP_F = DateTimeFormatter.ofPattern(DAY_SEP);
    public static final DateTimeFormatter CHINESE_DATE_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(CHINESE_DATE_PATTERN);
    public static final DateTimeFormatter CHINESE_MINUTES_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(CHINESE_MINUTES_PATTERN);


    public static String format2s(LocalDateTime time) {
        return SF.format(time);
    }

    public static String format2s_(LocalDateTime time) {
        return SF_.format(time);
    }

    public static String format2ms(LocalDateTime time) {
        return MS_F.format(time);
    }

    public static String format2day(LocalDateTime time) {
        return DAY_F.format(time);
    }

    public static String format2date(LocalDateTime time) {
        return DATE_F.format(time);
    }

    public static String formatSep(LocalDateTime time) {
        return DAY_SEP_F.format(time);
    }

    public static String format2chineseDate(LocalDateTime time) {
        return CHINESE_DATE_PATTERN_FORMATTER.format(time);
    }

    public static String format2chineseMinutes(LocalDateTime time) {
        return CHINESE_MINUTES_PATTERN_FORMATTER.format(time);
    }

    public static String formatSep() {
        return DAY_SEP_F.format(LocalDateTime.now());
    }

    public static LocalDateTime sStr2time(String pattern) {
        return LocalDateTime.parse(pattern, SF);
    }

    public static LocalDateTime msStr2time(String pattern) {
        return LocalDateTime.parse(pattern, MS_F);
    }

    public static LocalDateTime dayStr2time(String pattern) {
        return LocalDateTime.parse(pattern, DAY_F);
    }

    /**
     * LocalDateTime转毫秒值
     *
     * @param time
     * @param zoneId
     * @return
     */
    public static long time2msValue(LocalDateTime time, ZoneId zoneId) {
        return time.atZone(zoneId).toInstant().toEpochMilli();
    }

    /**
     * LocalDateTime转毫秒值,使用上海时区
     *
     * @param time
     * @return
     */
    public static long time2msValue(LocalDateTime time) {
        return time.atZone(SHANGHAI).toInstant().toEpochMilli();
    }

    /**
     * 毫秒时间戳转LocalDateTime
     *
     * @param milliseconds
     * @return
     */
    public static LocalDateTime longMs2time(Long milliseconds) {
        // 将时间戳转为当前时间:我们使用东八区
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneOffset.of("+8"));
    }

    /**
     * 秒时间戳转localDateTime
     *
     * @param seconds
     * @return
     */
    public static LocalDateTime longS2time(Long seconds) {
        // 将时间戳转为当前时间:我们使用东八区
        return LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.ofHours(8));
    }

    public static Date localDateTime2date(LocalDateTime localDateTime) {
        ZonedDateTime zdt = localDateTime.atZone(SHANGHAI);
        return Date.from(zdt.toInstant());
    }

    public static LocalDateTime date2localDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }
}
