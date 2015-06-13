package com.base.util.remind;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemindRecogniseUtilForMonth {
    public static boolean mIsMonthSetByAuther = false;
    public static int _mMonth;
    private static Calendar mCalendar ;
    private static Pattern[] patterns = {
// ID20121220001 hujinrong begin
            Pattern.compile(".*?(\\d+)个?月(.*)(之|过)?(!最)后.*"),
// ID20121220001 hujinrong end
            Pattern.compile(".*?(\\d+)个?月(.*)(之|以)?前.*") };
    private static Pattern pattern = Pattern.compile(".*?(\\d+)月.*");

    public static void recognizeMonth(String content) {
//ID20120825001 hujinrong begin        
        mCalendar = Calendar.getInstance();
//ID20120825001 hujinrong end        
        int month = mCalendar.get(Calendar.MONTH) + 1;
        boolean flagForConnect = true;
        Matcher m1 = patterns[0].matcher(content);
        Matcher m2 = patterns[1].matcher(content);

        Matcher matcher = pattern.matcher(content);

        if (content.contains("下月") || content.contains("下个月")) {
            month += 1;
        } else if (content.contains("下下月") || content.contains("下下个月")) {
            month += 2;
        } else if (content.contains("上个月") || content.contains("上月")) {
            month -= 1;
        } else if (content.contains("上上个月") || content.contains("上上个月")) {
            month -= 2;
        } else if (m1.matches()) {
            String matchGroup1 = m1.group(1);
            int monthStep = Integer.parseInt(matchGroup1);
            month += monthStep;
        } else if (m2.matches()) {
            String matchGroup1 = m2.group(1);
            int monthStep = Integer.parseInt(matchGroup1);
            month -= monthStep;
        } else if (matcher.matches()) {
            flagForConnect = false;
            mIsMonthSetByAuther = true;
            String matchGroup1 = matcher.group(1);
            month = Integer.parseInt(matchGroup1);
            _mMonth = month;
        }

        // reset month and year;
        if (flagForConnect) {
            int stepYear = 0;
            if (month > 12 || month < 1) {

                if (month > 12) {
                    stepYear = month / 12;
                    month = month % 12;
                } else {
                    stepYear = month / 12;
                    month = 12 + month % 12;
                }

            }
            _mMonth = month;
            RemindRecogniseUtilForYear.setYear(stepYear);
        }
    }

    public static void resetMonth(int stepMonth) {
        boolean flag = RemindRecogniseUtilForMonth.mIsMonthSetByAuther;
        if (flag)
            return;
        int month = _mMonth;
        month += stepMonth;
        int stepYear = 0;
        if (month > 12 || month < 1) {

            if (month > 12) {
                stepYear = month / 12;
                month = month % 12;
            } else {
                stepYear = month / 12;
                month = 12 + month % 12;
            }

        }
        _mMonth = month;
        RemindRecogniseUtilForYear.setYear(stepYear);
    }

    public static int[] dayNumberOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31,
            30, 31, 30, 31 };
    // jisuan runnian.
    static {
        int year = RemindRecogniseUtilForYear._mYear;
        if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
            dayNumberOfMonth[1] = 29;
        }
    }
}
