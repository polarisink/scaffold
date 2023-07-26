package github.polarisink.scaffold.infrastructure.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * {@link cn.hutool.core.date}包下有更详细的<br/> 这里是一个简化版的LocalDateTime工具类
 *
 * @author aries
 * @date 2022/4/29
 */
public class TimeUtil {
    private TimeUtil(){}

    /**
     * 上海zoneId
     */
    public static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_UNDERLINE_PATTERN = "yyyy-MM-dd_HH:mm:ss";
    public static final String DATETIME_SLASH_PATTERN = "yyyy-MM-dd/HHmmss";
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String DAY_PATTERN = "yyyy-MM-dd";
    public static final String MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_PATTERN = "MM-dd";
    public static final String TIME_ZONE = "GMT+8";
    public static final String CHINESE_DATE_PATTERN = "MM月dd日HH时mm分";
    public static final String CHINESE_MINUTES_PATTERN = "HH时mm分";

    /**
     * =================================================================================================================================
     * yyyy-MM-dd HH:mm:ss
     */
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
    /**
     * yyyy-MM-dd_HH:mm:ss，带下划线
     */
    public static final DateTimeFormatter DATETIME_FORMATTER_UNDERLINE = DateTimeFormatter.ofPattern(DATETIME_UNDERLINE_PATTERN);
    /**
     * yyyy-MM-dd/HHmmss，带斜杠
     */
    public static final DateTimeFormatter DATETIME_FORMATTER_SLASH = DateTimeFormatter.ofPattern(DATETIME_SLASH_PATTERN);
    /**
     * 精确到天
     */
    public static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern(DAY_PATTERN);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);
    /**
     * HH:mm:ss格式化
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    /**
     * 精确到ms
     */
    public static final DateTimeFormatter MS_FORMATTER = DateTimeFormatter.ofPattern(MS_PATTERN);
    /**
     * MM月dd日HH时mm分,用于目录分割
     */
    public static final DateTimeFormatter CHINESE_DATE_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(CHINESE_DATE_PATTERN);
    /**
     * HH时mm分
     */
    public static final DateTimeFormatter CHINESE_MINUTES_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(CHINESE_MINUTES_PATTERN);

    /**
     * * =================================================================================================================================
     *
     * @param time
     * @return
     */
    public static String toDatetime(LocalDateTime time) {
        return DATETIME_FORMATTER.format(time);
    }

    public static String toDatetimeUnderline(LocalDateTime time) {
        return DATETIME_FORMATTER_UNDERLINE.format(time);
    }

    public static String toMsDatetime(LocalDateTime time) {
        return MS_FORMATTER.format(time);
    }

    public static String toDayStr(LocalDateTime time) {
        return DAY_FORMATTER.format(time);
    }

    public static String toDateStr(LocalDateTime time) {
        return DATE_FORMATTER.format(time);
    }

    public static String toDatetimeSlashStr(LocalDateTime time) {
        return DATETIME_FORMATTER_SLASH.format(time);
    }

    public static String toChineseDateStr(LocalDateTime time) {
        return CHINESE_DATE_PATTERN_FORMATTER.format(time);
    }

    public static String toChineseMinutesDateStr(LocalDateTime time) {
        return CHINESE_MINUTES_PATTERN_FORMATTER.format(time);
    }

    public static LocalDateTime toDatetime(String pattern) {
        return LocalDateTime.parse(pattern, DATETIME_FORMATTER);
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
