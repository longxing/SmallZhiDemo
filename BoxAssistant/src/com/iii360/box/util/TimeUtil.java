package com.iii360.box.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hefeng
 * 
 */
public class TimeUtil {
    public static final SimpleDateFormat TIME_YEAR_FORMAT = new SimpleDateFormat("yyyy");
//    public static final SimpleDateFormat TIME_MONTH_FORMAT = new SimpleDateFormat("MM");
    public static final SimpleDateFormat TIME_MONTH_FORMAT = new SimpleDateFormat("M");
//    public static final SimpleDateFormat TIME_DAY_FORMAT = new SimpleDateFormat("dd");
    public static final SimpleDateFormat TIME_DAY_FORMAT = new SimpleDateFormat("d");
    public static final SimpleDateFormat TIME_HOUR_FORMAT = new SimpleDateFormat("HH");
    public static final SimpleDateFormat TIME_MINUTE_FORMAT = new SimpleDateFormat("mm");
    public static final SimpleDateFormat TIME_SECOND_FORMAT = new SimpleDateFormat("ss");

    public static final SimpleDateFormat TIME_WEEK_FORMAT = new SimpleDateFormat("E");

    public static final SimpleDateFormat TIME_DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//    public static final SimpleDateFormat TIME_DEFAULT_FORMAT = new SimpleDateFormat("yyyy/MM/dd/HH/mm");

    public static String getDate(long milliseconds, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(milliseconds));
    }

    public static String getTime(long milliseconds) {
        return getDate(milliseconds, TIME_DEFAULT_FORMAT);
    }

    /**
     * @param milliseconds
     * @return 年
     */
    public static String getYear(long milliseconds) {
        return getDate(milliseconds, TIME_YEAR_FORMAT);
    }

    /**
     * @param milliseconds
     * @return 月
     */
    public static String getMonth(long milliseconds) {
        return getDate(milliseconds, TIME_MONTH_FORMAT);
    }

    /**
     * @param milliseconds
     * @return 日
     */
    public static String getDay(long milliseconds) {
        return getDate(milliseconds, TIME_DAY_FORMAT);
    }

    /**
     * @param milliseconds
     * @return 时
     */
    public static String getHour(long milliseconds) {
        return getDate(milliseconds, TIME_HOUR_FORMAT);
    }

    /**
     * @param milliseconds
     * @return 分
     */
    public static String getMintue(long milliseconds) {
        return getDate(milliseconds, TIME_MINUTE_FORMAT);
    }

    /**
     * @param milliseconds
     * @return 秒
     */
    public static String getSecond(long milliseconds) {
        return getDate(milliseconds, TIME_SECOND_FORMAT);
    }

    /**
     * @param milliseconds
     * @return 周几（从周日（0）开始）
     */
    public static String getWeek(long milliseconds) {
        return getDate(milliseconds, TIME_WEEK_FORMAT);
    }

    /**
     * @return 当前时间
     */
    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

//    public final static long TIME_ONE_MONTH = 2*30 * 1 * 24 * 60 * 60 * 1000;
}
