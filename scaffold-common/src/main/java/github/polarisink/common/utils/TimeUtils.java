package github.polarisink.common.utils;

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
public class TimeUtils {

  /**
   * 上海zoneId
   */
  public static final ZoneId shanghai = ZoneId.of("Asia/Shanghai");
  public static final String sFStr = "yyyy-MM-dd HH:mm:ss";
  public static final String sFStr_ = "yyyy-MM-dd_HH:mm:ss";
  public static final String dayFStr = "yyyy-MM-dd";
  public static final String msFStr = "yyyy-MM-dd HH:mm:ss.SSS";
  public static final String dateFStr = "MM-dd";
  public static final String daySep = "yyyy-MM-dd/HHmmss";

  /**
   * 精确到s
   */
  public static final DateTimeFormatter sF = DateTimeFormatter.ofPattern(sFStr);
  /**
   * 精确到s,控股替换为下划线
   */
  public static final DateTimeFormatter sF_ = DateTimeFormatter.ofPattern(sFStr_);
  /**
   * 精确到天
   */
  public static final DateTimeFormatter dayF = DateTimeFormatter.ofPattern(dayFStr);

  /**
   * 只带日期
   */
  public static final DateTimeFormatter dateF = DateTimeFormatter.ofPattern(dateFStr);
  /**
   * 精确到ms
   */
  public static final DateTimeFormatter msF = DateTimeFormatter.ofPattern(msFStr);
  /**
   * 用于目录分割
   */
  public static final DateTimeFormatter daySepF = DateTimeFormatter.ofPattern(daySep);


  public static String format2s(LocalDateTime time) {
    return sF.format(time);
  }

  public static String format2s_(LocalDateTime time) {
    return sF_.format(time);
  }

  public static String format2ms(LocalDateTime time) {
    return msF.format(time);
  }

  public static String format2day(LocalDateTime time) {
    return dayF.format(time);
  }

  public static String format2date(LocalDateTime time) {
    return dateF.format(time);
  }

  public static String formatSep(LocalDateTime time) {
    return daySepF.format(time);
  }

  public static String formatSep() {
    return daySepF.format(LocalDateTime.now());
  }

  public static LocalDateTime sStr2time(String pattern) {
    return LocalDateTime.parse(pattern, sF);
  }

  public static LocalDateTime msStr2time(String pattern) {
    return LocalDateTime.parse(pattern, msF);
  }

  public static LocalDateTime dayStr2time(String pattern) {
    return LocalDateTime.parse(pattern, dayF);
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
    return time.atZone(shanghai).toInstant().toEpochMilli();
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
    ZonedDateTime zdt = localDateTime.atZone(shanghai);
    return Date.from(zdt.toInstant());
  }
}
