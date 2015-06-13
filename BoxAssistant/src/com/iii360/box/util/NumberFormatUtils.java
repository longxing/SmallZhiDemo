package com.iii360.box.util;

public class NumberFormatUtils {

    /**
     * @param number
     * @return 一周中文数字
     */
    public static String getZhWeekNumber(int number) {
        String zh;

        switch (number) {

        case 1:
            zh = "一";

            break;
        case 2:
            zh = "二";

            break;
        case 3:
            zh = "三";

            break;
        case 4:
            zh = "四";

            break;

        case 5:
            zh = "五";

            break;
        case 6:
            zh = "六";

            break;
        case 7:
            zh = "日";

            break;
        default:
            zh = "" + number;
            break;
        }

        return zh;
    }

    /**
     * @param number
     * @return 一周中文数字,按照西方转换
     */
    public static String getZhWeek(int number) {
        String zh;

        switch (number) {
        case 1:
            zh = "日";

            break;

        case 2:
            zh = "一";

            break;
        case 3:
            zh = "二";

            break;
        case 4:
            zh = "三";

            break;
        case 5:
            zh = "四";

            break;

        case 6:
            zh = "五";

            break;
            
        case 7:
            zh = "六";

            break;

        default:
            zh = "" + number;
            break;
        }

        return zh;
    }
}
