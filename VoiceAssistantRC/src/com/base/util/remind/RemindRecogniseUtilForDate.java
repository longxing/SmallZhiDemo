// ID20121220001 hujinrong begin
package com.base.util.remind;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemindRecogniseUtilForDate {
    private static Calendar mCalendar ;
    public static int _mDate;
//ID20121010001 hujinrong begin    
    public static boolean _mIsSetDate;
//ID20121010001 hujinrong  end
    public static boolean mIsDateSetByAuther = false;
    public static boolean _mIsobscureDayOfMonth = false;
    private static Pattern patterns[] = {
            Pattern.compile(".*?(\\d+)(天|日)(.*)(过|之)?后.*"),
            Pattern.compile(".*?(\\d+)(天|日)(.*)(之|以)?前.*"),
//ID20121107003 hujinrong begin
            Pattern.compile(".*?月(\\d+)(日|号)?.*") ,
//ID20121107003 hujinrong end
    		Pattern.compile(".*?(\\d+)(日|号).*"),
    		Pattern.compile(".*?月(月底|底|最后1天).*"),
    };

    public static void setDate(String content) {
        //不能在静态地方初始化，否则取得的日期有偏差...
        mCalendar = Calendar.getInstance();
        int date = mCalendar.get(Calendar.DATE);
        int nowDate = mCalendar.get(Calendar.DATE);
        int month = RemindRecogniseUtilForMonth._mMonth - 1;
        boolean flagForErrorConnect = true;

        Matcher m1 = patterns[0].matcher(content);
        Matcher m2 = patterns[1].matcher(content);
        Matcher m3 = patterns[2].matcher(content);

        Matcher m4 = patterns[3].matcher(content);
        Matcher m5 = patterns[4].matcher(content);
        
        if (content.contains("明天")||content.contains("明早")||content.contains("明晚")) {
            date += 1;
//ID20121010001 hujinrong begin
            _mIsSetDate = true;
        } else if (content.contains("大大前天")) {
            date -= 4;
            _mIsSetDate = true;
        } else if (content.contains("大大后天")) {
            date += 4;
            _mIsSetDate = true;
        } else if (content.contains("大前天")) {
            date -= 3;
            _mIsSetDate = true;
        } else if (content.contains("大后天")) {
            date += 3;
            _mIsSetDate = true;
        } else if (content.contains("昨天")) {
            date -= 1;
            _mIsSetDate = true;
        } else if (content.contains("后天")) {
            date += 2;
            _mIsSetDate = true;
        } else if (content.contains("前天")) {
            date -= 2;
            _mIsSetDate = true;
        } else if (m1.matches()) {
            String matchGroup1 = m1.group(1);
            int dayStep = Integer.parseInt(matchGroup1);
            date += dayStep;
            _mIsSetDate = true;
        } else if (m2.matches()) {
            String matchGroup1 = m2.group(1);
            int dayStep = Integer.parseInt(matchGroup1);
            date = -dayStep;
            _mIsSetDate = true;
        } else if (m3.matches()) {
            mIsDateSetByAuther = true;
            flagForErrorConnect = false;
            String matchGroup1 = m3.group(1);
            date = Integer.parseInt(matchGroup1);
            _mDate = date;
            _mIsSetDate = true;
        } else if (m4.matches()) {
        	 String matchGroup1 = m4.group(1);
        	 date = Integer.parseInt(matchGroup1);
        	 flagForErrorConnect = false;
        	 _mDate = date ; 
        	 _mIsSetDate = true;
        	 mIsDateSetByAuther = true;
        } else if( m5.matches()) {
        	int year = RemindRecogniseUtilForYear._mYear ;
        	if(month == 1){
        		if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
                    RemindRecogniseUtilForMonth.dayNumberOfMonth[1] = 29;
                } else {
                    RemindRecogniseUtilForMonth.dayNumberOfMonth[1] = 28;
                }
        	}
        	 flagForErrorConnect = false;
        	_mDate = RemindRecogniseUtilForMonth.dayNumberOfMonth[month];
        	_mIsSetDate = true;
        	mIsDateSetByAuther = true;
        	_mIsobscureDayOfMonth = true;
        }
//ID20121010001 hujinrong begin
        if (flagForErrorConnect) {
            int monthStep = 0;
            // puan duan riqi you meiyou yichu .
            if (date > 0) {
                while (date > RemindRecogniseUtilForMonth.dayNumberOfMonth[month]) {
                    date -= RemindRecogniseUtilForMonth.dayNumberOfMonth[month];
                    monthStep++;
                    month++;
                    month = month % 12;
                }
            } else {

                int remindMonth = RemindRecogniseUtilForMonth._mMonth;
                int monthI = remindMonth - 1;

                monthStep = 0;
                int sum = nowDate;

                monthI = monthI - 1;
                monthI = monthI == -1 ? 11 : monthI;
                date = -date;
                while (sum < date) {
                    sum = sum
                            + RemindRecogniseUtilForMonth.dayNumberOfMonth[monthI];
                    monthI = monthI - 1;
                    monthStep--;
                    monthI = monthI == -1 ? 11 : monthI;
                }
                if (sum == date) {
                    date = RemindRecogniseUtilForMonth.dayNumberOfMonth[monthI];
                    monthStep--;
                } else {
                    date = sum - date;
                }
            }
            _mDate = date;
            RemindRecogniseUtilForMonth.resetMonth(monthStep);
        }

    }

    public static void resetDate(int dateStep) {
        boolean flag = RemindRecogniseUtilForDate.mIsDateSetByAuther;
        if (flag == true)
            return;
        int date = _mDate;
        mCalendar = Calendar.getInstance();
        int nowDate = mCalendar.get(Calendar.DATE);
        int month = RemindRecogniseUtilForMonth._mMonth - 1;
        int monthStep = 0;
        date += dateStep;
        // puan duan riqi you meiyou yichu .
        if (date > 0) {
            while (date > RemindRecogniseUtilForMonth.dayNumberOfMonth[month]) {
                date -= RemindRecogniseUtilForMonth.dayNumberOfMonth[month];
                monthStep++;
                month++;
                month = month % 12;
            }
        } else {

            int remindMonth = RemindRecogniseUtilForMonth._mMonth;
            int monthI = remindMonth - 1;

            monthStep = 0;
            int sum = nowDate;

            monthI = monthI - 1;
            monthI = monthI == -1 ? 11 : monthI;
            date = -date;
            while (sum < date) {
                sum = sum
                        + RemindRecogniseUtilForMonth.dayNumberOfMonth[monthI];
                monthI = monthI - 1;
                monthStep--;
                monthI = monthI == -1 ? 11 : monthI;
            }
            if (sum == date) {
                date = RemindRecogniseUtilForMonth.dayNumberOfMonth[monthI];
                monthStep--;
            } else {
                date = sum - date;
            }
        }
        _mDate = date;
        RemindRecogniseUtilForMonth.resetMonth(monthStep);
    }

}
// ID20121220001 hujinrong end