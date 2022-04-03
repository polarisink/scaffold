package github.polarisink.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime工具类
 * @author lqs
 */
public class TimeUtils {


    public static final String sStr = "yyyy-MM-dd HH:mm:ss";
    public static final String dayStr = "yyyy-MM-dd";
    public static final String msStr = "yyyy-MM-dd HH:mm:ss.SSS";
    /**
     * 精确到s
     */
    public static final DateTimeFormatter sF = DateTimeFormatter.ofPattern(sStr);
    /**
     * 精确到天
     */
    public static final DateTimeFormatter dayF = DateTimeFormatter.ofPattern(dayStr);
    /**
     * 精确到ms
     */
    public static final DateTimeFormatter msF = DateTimeFormatter.ofPattern(msStr);

    public static String format2s(LocalDateTime time) {
        return sF.format(time);
    }

    public static String format2ms(LocalDateTime time) {
        return msF.format(time);
    }

    public static LocalDateTime s2time(String pattern) {
        return LocalDateTime.parse(pattern, sF);
    }

    public static LocalDateTime ms2time(String pattern) {
        return LocalDateTime.parse(pattern, msF);
    }
}
