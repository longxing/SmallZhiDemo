// ID20121220001 hujinrong begin
package com.base.util.remind;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemindRecogniseUtilForYear {
    private static Pattern mPatterns[] = {
            Pattern.compile(".*?(\\d+)年(.*)(之|以)?前.*"),
            Pattern.compile(".*?(\\d+)年(.*)(过|之)?后.*") };
    private static Pattern mPattern = Pattern.compile(".*?(\\d+)年.*");
    private static Calendar mCalendar ;
    public static int _mYear;

    public static void recognizeYear(String content) {
    	mCalendar = Calendar.getInstance();
        int year = mCalendar.get(Calendar.YEAR);

        Matcher m1 = mPatterns[0].matcher(content);
        Matcher m2 = mPatterns[1].matcher(content);
        Matcher matcher = mPattern.matcher(content);

        if (content.contains("明年")) {
            year = year + 1;
        } else if (content.contains("大大前年")) {
            year = year - 4;
        } else if (content.contains("大大后年")) {
            year = year + 4;
        } else if (content.contains("大后年")) {
            year = year + 3;
        } else if (content.contains("大前年")) {
            year = year - 3;
        } else if (content.contains("后年")) {
            year = year + 2;
        } else if (content.contains("去年")) {
            year = year - 1;
        } else if (content.contains("前年")) {
            year = year - 2;
        } else if (m1.matches()) {
            String matchGroup1 = m1.group(1);
            int stepYear = Integer.parseInt(matchGroup1);
            year -= stepYear;
        } else if (m2.matches()) {
            String matchGroup1 = m2.group(1);
            int stepYear = Integer.parseInt(matchGroup1);
            year += stepYear;
        } else if (matcher.matches()) {

            // String strYear = matcher.group(1);
            // int yearFromString = Integer.parseInt(strYear);
            // if(yearFromString < 50){
            // yearFromString = yearFromString + 2000;
            // }else if(yearFromString >50 && yearFromString <100){
            // yearFromString = yearFromString =1900;
            // }
            // year = yearFromString;
            String strYear = matcher.group(1);
            year = Integer.parseInt(strYear);
        }
        _mYear = year;
    }

    public static void setYear(int step) {
        _mYear = _mYear + step;
    }
}
// ID20121220001 hujinrong end