package cc.buddies.component.common.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public static final String DATE_FORMAT_LINE = "yyyy-MM-dd";
    public static final String DATE_FORMAT_SLASH = "yyyy/MM/dd";
    public static final String DATE_FORMAT_LINE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_SLASH_TIME = "yyyy/MM/dd HH:mm:ss";

    private static final Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;

    /**
     * 获取当前时间戳
     *
     * @return long
     */
    public static long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取格式化的当前系统时间
     *
     * @return String
     */
    public static String getCurrentDateStr() {
        return getFormatDate(getCurrentTimeStamp(), DATE_FORMAT_LINE);
    }

    /**
     * 获取格式化时间
     *
     * @param timeStamp 时间戳
     * @param pattern   格式化格式
     * @return String
     */
    public static String getFormatDate(long timeStamp, String pattern) {
        if (TextUtils.isEmpty(pattern)) {
            pattern = DATE_FORMAT_LINE_TIME;
        }

        if (Long.toString(Math.abs(timeStamp)).length() < 11) {
            timeStamp *= 1000;
        }

        SimpleDateFormat format = new SimpleDateFormat(pattern, DEFAULT_LOCALE);
        format.setTimeZone(TimeZone.getDefault());
        return format.format(timeStamp);
    }

    /**
     * 根据字符串时间获取时间戳
     *
     * @param stringDate 日期
     * @param pattern    格式
     * @return Date
     */
    public static Date getFormatDate(String stringDate, String pattern) {
        if (TextUtils.isEmpty(stringDate)) {
            return null;
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, DEFAULT_LOCALE);
            return simpleDateFormat.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化返回日期时间
     *
     * @param stringDate 日期
     * @param inPattern  日期格式
     * @param outPattern 输入格式
     * @return String
     */
    public static String getFormatDate(String stringDate, String inPattern, String outPattern) {
        if (TextUtils.isEmpty(stringDate) || TextUtils.isEmpty(inPattern)) {
            return "";
        }

        Date formatDate = getFormatDate(stringDate, inPattern);
        if (formatDate == null) return "";

        SimpleDateFormat format = new SimpleDateFormat(outPattern, DEFAULT_LOCALE);
        return format.format(formatDate);
    }

}
