package github.polarisink.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date工具类
 */
public class ThreadLocalDateUtil {

    private static final String date_format = "yyyy-MM-dd HH:mm:ss";
    private static final String day_format = "yyyy-MM-dd";
    private static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<SimpleDateFormat> dayFormatThreadLocal = new ThreadLocal<>();

    public static SimpleDateFormat getDateFormat() {
        SimpleDateFormat df = dateFormatThreadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(date_format);
            dateFormatThreadLocal.set(df);
        }
        return df;
    }

    public static SimpleDateFormat getDayFormat() {
        SimpleDateFormat df = dayFormatThreadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(day_format);
            dateFormatThreadLocal.set(df);
        }
        return df;
    }

    public static String formatDate(Date date) {
        return getDateFormat().format(date);
    }

    public static String formatDay(Date date) {
        return getDayFormat().format(date);
    }

    public static Date parseDate(String strDate) throws ParseException {
        return getDateFormat().parse(strDate);
    }

    public static Date parseDay(String strDate) throws ParseException {
        return getDayFormat().parse(strDate);
    }


}
