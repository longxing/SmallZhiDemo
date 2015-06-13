//ID20121010001 hujinrong begin
package com.base.util.remind;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemindRecogniseUtilForTime {
    private static Calendar mCalendar ;
    public static boolean _mIsSetTime = false ;
    private static Pattern mHourPatterns[] = {
            Pattern.compile(".*?(\\d+)个?小时(\\d+.*)?(之|过|以)?[后|後].*"),
            Pattern.compile(".*?(\\d+)个?小时(\\d+.*)?(之|以)?前.*"),
            Pattern.compile(".*?(\\d+)((点钟?)|时|：|:).*"), Pattern.compile(".*午时.*") ,
//ID20120831002 hujinrong begin
            Pattern.compile(".*?(\\d+)分钟?(之|过|以)?[后|後].*"),
            Pattern.compile(".*?(\\d+)分钟?(之|以)?前.*"),
            Pattern.compile(".*[过|待][一1]?会儿?.*"),
//ID20120831002 hujinrong end
//ID20120908002 hujinrong begin
            Pattern.compile(".*?个?小时[之|过|以]?[后|後].*"),
            Pattern.compile(".*?\\d个半小时[之|过|以]?[后|後].*"),
            Pattern.compile(".*半个小时[之|过|以]?[后|後].*"),
//ID20120908002 hujinrong end
//ID20120911001 hujinrong begin
            Pattern.compile(".*?(\\d+)刻钟?(之|过|以)?[后|後].*"),
            Pattern.compile(".*?(\\d+)刻钟?(之|以)?前.*"),
//ID20120911001 hujinrong end
//ID20121016004 hujinrong begin            
            Pattern.compile(".*?过(\\d+)刻钟?.*"),
            Pattern.compile(".*?过(\\d+)分钟?.*"),
            Pattern.compile(".*?过(\\d+)个半小时.*"),
            Pattern.compile(".*?过半个?小时.*"),
            
            Pattern.compile(".*?过(\\d+)个?小时.*")
//ID20121016004 hujinrong end
    };
    private static Pattern mMinutePatterns[] = {
            Pattern.compile(".*?(\\d+)分钟?(之|过|以)?[后|後].*"),
            Pattern.compile(".*?(\\d+)分钟?(之|以)?前.*"),
            Pattern.compile(".*?\\d+((点钟?)|时|:|：)(\\d+)分?.*"),
            Pattern.compile(".*?\\d+点半.*"),
            Pattern.compile(".*?\\d+((点钟?)|时)过?(\\d+)刻.*"),
            Pattern.compile(".*?\\d+((点钟?)|时)[过|待]?[一1]?会儿?.*"),
            Pattern.compile(".*[过|待][一1]?会儿?.*"),
//ID20120831002 hujinrong begin
            Pattern.compile(".*时(\\d+)刻.*"),
//ID20120831002 hujinrong begin
            Pattern.compile(".*?个?小时[之|过|以]?[后|後].*"),
//ID20120908002 hujinrong begin
            Pattern.compile(".*?(\\d+)个半小时[之|过|以]?[后|後].*"),
            Pattern.compile(".*半个?小时[之|过|以]?[后|後].*"),
//ID20120908002 hujinrong end
//ID20120911001 hujinrong begin
            Pattern.compile(".*?(\\d+)刻钟?(之|过|以)?[后|後].*"),
            Pattern.compile(".*?(\\d+)刻钟?(之|以)?前.*"),
//ID20120911001 hujinrong end
//ID20121016004 hujinrong begin            
            Pattern.compile(".*?过(\\d+)刻钟?.*"),
            Pattern.compile(".*?过(\\d+)分钟?.*"),
            Pattern.compile(".*?过(\\d+)个半小时.*"),
            Pattern.compile(".*?过半个?小时.*"),
            Pattern.compile(".*?过(\\d+)个?小时.*")
//ID20121016004 hujinrong end           
            
            

    };
    public static boolean mIsHourSetByAuther = false;
    public static boolean mIsMinuteSetByAnther = false;
	// ID20130809003 chenyuming	begin
    private static String mTimeSpecial[] = { "下午", "晚上", "傍晚", "黄昏", "夜里", "半夜" , "明晚" };
	// ID20130809003 chenyuming end
//ID20120831002 hujinrong begin    
    private static String mChineseTime[] = { "凌晨", "黎明", "拂晓", "清晨", "早晨", "上午", "中午", "下午", "晚上", "傍晚", "黄昏", "午夜", "半夜", "子时", "丑时", "寅时", "卯时",
            "辰时", "巳时", "未时", "申时", "酉时", "戌时", "亥时" };
    private static int mChineseTimeMapping[] = { 2, 4, 6, 7, 8, 9, 12, 15, 20, 16, 17, 24, 24, 23, 1, 3, 5, 7, 9, 13, 15, 17, 19, 21 };
//ID20120831002 hujinrong end
    public static int _mHour;
    public static int _mMinute;

    public static void recogniseHour(String content) {
//ID20120825001 hujinrong begin
        mCalendar = Calendar.getInstance();
//ID20120825001 hujinrong end
//        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int hour = 10;
        boolean flagForErrorConnect = true;
        Matcher m1 = mHourPatterns[0].matcher(content);
        Matcher m2 = mHourPatterns[1].matcher(content);
        Matcher m3 = mHourPatterns[2].matcher(content);
        Matcher m4 = mHourPatterns[3].matcher(content);
//ID20120831002 hujinrong  begin        
        Matcher m5 = mHourPatterns[4].matcher(content);
        Matcher m6 = mHourPatterns[5].matcher(content);
        Matcher m7 = mHourPatterns[6].matcher(content);
//ID20120831002 hujinrong end       
//ID20120908002 hujinrong begin        
        Matcher m8 = mHourPatterns[7].matcher(content);
        Matcher m9 = mHourPatterns[8].matcher(content);
        Matcher m10 = mHourPatterns[9].matcher(content);
//ID20120908002 hujinrong end        
//ID20120911001 hujinrong begin
        Matcher m11 = mHourPatterns[10].matcher(content);
        Matcher m12 = mHourPatterns[11].matcher(content);
//ID20120911001 hujinrong end
//ID20121016004 hujinrong begin        
        Matcher m13 = mHourPatterns[12].matcher(content);
        Matcher m14 = mHourPatterns[13].matcher(content);
        Matcher m15 = mHourPatterns[14].matcher(content);
        Matcher m16 = mHourPatterns[15].matcher(content);
        Matcher m17 = mHourPatterns[16].matcher(content);
        
        if (m1.matches()) {
            String matchGroup1 = m1.group(1);
            int hourStep = Integer.parseInt(matchGroup1);
            hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            hour += hourStep;
            _mIsSetTime = true;
        } else  if (m17.matches()) {
            String matchGroup1 = m17.group(1);
            int hourStep = Integer.parseInt(matchGroup1);
            hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            hour += hourStep;
            _mIsSetTime = true;
//ID20121016004 hujinrong begin
        } else if (m2.matches()) {
            String matchGroup1 = m2.group(1);
            int hourStep = Integer.parseInt(matchGroup1);
            hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            hour -= hourStep;
            _mIsSetTime = true;
        } else if (m3.matches()) {
            mIsHourSetByAuther = true;
            flagForErrorConnect = false;
            String matchGroup1 = m3.group(1);
            hour = Integer.parseInt(matchGroup1);
            if (hour <= 12) {
                for (int i = 0; i < mTimeSpecial.length; i++) {
                    if (content.contains(mTimeSpecial[i])) {
                        hour += 12;
                        break;
                    }
                }
            }
            _mHour = hour;
            _mIsSetTime = true;
        } else if (m4.matches()) {
            hour = 12;
            _mIsSetTime = true;
//ID20121016004 hujinrong begin
		} else if (m5.matches() || m6.matches() || m7.matches() || m8.matches()
				|| m9.matches() || m10.matches() || m11.matches()
				|| m12.matches() || m13.matches() || m14.matches()
				|| m15.matches() || m16.matches()) {
			// 取当前时间
			hour = mCalendar.get(Calendar.HOUR_OF_DAY);
			_mIsSetTime = true;
        } else {
            for (int i = 0; i < mChineseTime.length; i++) {
                if (content.contains(mChineseTime[i])) {
                    hour = mChineseTimeMapping[i];
                    _mIsSetTime = true;
                }
            }
//ID20121016004 hujinrong end
        }
        if (flagForErrorConnect) {
            int dateStep = 0;
            if (hour > 24 || hour < 0) {

                if (hour > 24) {
                    dateStep = hour / 24;
                    hour = hour % 24;
                } else if (hour < 0) {
                    dateStep = hour / 24 - 1;
                    hour = 24 + hour % 24;
                } else if (hour == 0) {
                    dateStep = 0;
                    hour = 24;
                }
            }
            if (hour == 24) {
                hour = 0;
                dateStep++;
            }
            _mHour = hour;

            RemindRecogniseUtilForDate.resetDate(dateStep);
        }

    }

    public static void resetHour(int hourStep) {
//ID20120825001 hujinrong begin 
        mCalendar = Calendar.getInstance();
//ID20120825001 hujinrong end
        boolean flag = RemindRecogniseUtilForTime.mIsHourSetByAuther;
        if (flag)
            return;

        int hour = _mHour;
        hour = hour + hourStep;
        int dateStep = 0;
        if (hour > 24 || hour < 0) {

            if (hour > 24) {
                dateStep = hour / 24;
                hour = hour % 24;
            } else if (hour < 0) {
                dateStep = hour / 24 - 1;
                hour = 24 + hour % 24;
            } else if (hour == 0) {
                dateStep = 0;
                hour = 24;
            }
        }
        if (hour == 24) {
            hour = 0;
            dateStep++;
        }
        _mHour = hour;
        RemindRecogniseUtilForDate.resetDate(dateStep);

    }

    public static void recogniseMinute(String content) {
        int minute = 0;
        int nowMinute = mCalendar.get(Calendar.MINUTE);
        boolean flagForErrorConnect = true;
        Matcher m1 = mMinutePatterns[0].matcher(content);
        Matcher m2 = mMinutePatterns[1].matcher(content);
        Matcher m3 = mMinutePatterns[2].matcher(content);
        Matcher m4 = mMinutePatterns[3].matcher(content);
        Matcher m5 = mMinutePatterns[4].matcher(content);
        Matcher m6 = mMinutePatterns[5].matcher(content);
        Matcher m7 = mMinutePatterns[6].matcher(content);
        Matcher m8 = mMinutePatterns[7].matcher(content);
        Matcher m9 = mMinutePatterns[8].matcher(content);
//ID20120908002 hujinrong begin 
        Matcher m10 = mMinutePatterns[9].matcher(content);
        Matcher m11 = mMinutePatterns[10].matcher(content);
//ID20120908002 hujinrong end      
//ID20120911001 hujinrong begin  
        Matcher m12 = mMinutePatterns[11].matcher(content);
        Matcher m13 = mMinutePatterns[12].matcher(content);
//ID20120911001 hujinrong end
//ID20121016004 hujinrong begin        
        Matcher m14 = mMinutePatterns[13].matcher(content);
        Matcher m15 = mMinutePatterns[14].matcher(content);
        Matcher m16 = mMinutePatterns[15].matcher(content);
        Matcher m17 = mMinutePatterns[16].matcher(content);
        Matcher m18 = mMinutePatterns[17].matcher(content);
        // System.out.println("true or false :"+m9.matches()+"    "+minutePatterns[8].pattern());
//ID20120926001 hujinrong begin
//delete
//ID20120926001 hujinrong end
        if (m1.matches()) {
            minute = nowMinute;
            String matchGroup1 = m1.group(1);
            int minuteStep = Integer.parseInt(matchGroup1);
            minute += minuteStep;
            _mIsSetTime = true;
        } else if (m15.matches()) {
            minute = nowMinute;
            String matchGroup1 = m15.group(1);

            int minuteStep = Integer.parseInt(matchGroup1);
            minute += minuteStep;
            _mIsSetTime = true;
        } else if (m2.matches()) {
            minute = nowMinute;
            String matchGroup1 = m2.group(1);
            int minuteStep = Integer.parseInt(matchGroup1);
            minute -= minuteStep;
            _mIsSetTime = true;
        } else if (m6.matches()) {
            minute = 8;
            _mIsSetTime = true;
        } else if (m5.matches()) {
            mIsMinuteSetByAnther = true;
            flagForErrorConnect = false;
            String matchGroup1 = m5.group(3);
            minute = 15 * Integer.parseInt(matchGroup1);
            _mMinute = minute;
            _mIsSetTime = true;
        } else if (m3.matches()) {
            mIsMinuteSetByAnther = true;
            flagForErrorConnect = false;
            String matchGroup1 = m3.group(3);
            minute = Integer.parseInt(matchGroup1);
            _mIsSetTime = true;
            _mMinute = minute;
        } else if (m4.matches()) {
            minute = 30;
            _mIsSetTime = true;
        } else if (m7.matches()) {
            minute = mCalendar.get(Calendar.MINUTE);
            minute = minute + 8;
            _mIsSetTime = true;
//ID20120908002 hujinrong begin
        } else if(m10.matches()) {
            int hourStep =Integer.parseInt( m10.group(1));
            minute = hourStep*60+nowMinute+30;
            _mIsSetTime = true;
        } else if(m16.matches()) {
            int hourStep =Integer.parseInt( m16.group(1));
            minute = hourStep*60+nowMinute+30;
            _mIsSetTime = true;
        } else if(m11.matches()||m17.matches()) {
            minute = nowMinute + 30 ;
            _mIsSetTime = true;
//ID20120908002 hujinrong end
//ID20120911001 hujinrong begin
        }else if(m12.matches()){
            int number = Integer.parseInt(m12.group(1));
            minute = nowMinute + number*15;
            _mIsSetTime = true;
        }else if(m14.matches()){
            int number = Integer.parseInt(m14.group(1));
            minute = nowMinute + number*15;
            _mIsSetTime = true;
        }else if(m13.matches()){
            int number = Integer.parseInt(m13.group(1));
            minute = nowMinute - number*15;
            _mIsSetTime = true;
//ID20120911001 hujinrong end
        } else if (m9.matches() || m18.matches()) {
            minute = nowMinute;
            _mIsSetTime = true;
//ID20120926001 hujinrong begin
        }else if (m8.matches()) {
            mIsMinuteSetByAnther = true;
            String matchGroup1 = m8.group(1);
            minute = Integer.parseInt(matchGroup1) * 15;
            _mMinute = minute;
            _mIsSetTime = true;
        }
//ID20120926001 hujinrong end

        if (flagForErrorConnect) {
            int hourStep = 0;
            if (minute >= 60 || minute < 0) {
                if (minute >= 60) {
                    hourStep = minute / 60;
                    minute = minute % 60;
                } else {
                    hourStep = minute / 60 - 1;
//ID20120911001 hujinrong begin
                    minute  = minute % 60;
//ID20120911001 hujinrong end
                    minute = (60 + minute) % 60;
                }
            }
            _mMinute = minute;
            resetHour(hourStep);
        }
    }
//ID20121016004 hujinrong end
    public static void resetMinute(int minStep) {
        boolean flag = RemindRecogniseUtilForTime.mIsMinuteSetByAnther;
        if (flag) {
            return;
        }
        int hourStep = 0;
        int minute = _mMinute;
        minute += minStep;
        if (minute > 60 || minute < 0) {
            if (minute > 60) {
                hourStep = minute / 60;
                minute = minute % 60;
            } else {
//ID20120911001 hujinrong begin
                hourStep = minute / 60 - 1;
                 minute  = minute % 60;
//ID20120911001 hujinrong end
                 minute = (60 + minute) % 60;
            }
        }
        _mMinute = minute;
        resetHour(hourStep);
    }
}
//ID20121010001 hujinrong end