package com.base.util.remind;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iii360.sup.common.utl.stringPreHandlingModule;

import android.util.Log;

public class RemindRecogniser {
    private int[] mNumber = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 2 };
    private String[] mNumberStr = { "零", "一", "二", "三", "四", "五", "六", "七",
            "八", "九", "两" };
    private RemindObject mRemind;
    private String mMatchContent;

    public RemindRecogniser(String matchContent) {
        mRemind = new RemindObject();
        mMatchContent = matchContent;
        resetRecogniserParams();
        mMatchContent = mMatchContent.replaceAll("[!.,;'?！~、，。；‘？]", "");
        matchContent = stringToNumber(matchContent);
        RemindRecogniseUtilForYear.recognizeYear(matchContent);
        RemindRecogniseUtilForMonth.recognizeMonth(matchContent);
        RemindRecogniseUtilForDate.setDate(matchContent);
        RemindRecogniseUtilForTime.recogniseHour(matchContent);
        RemindRecogniseUtilForTime.recogniseMinute(matchContent);
        RemindRecogniseUtilForRepeatFlag.recogniseDayRepeatFlag(matchContent);
        RemindRecogniseUtilForRepeatFlag.recogniseMonthFlag(matchContent);
        RemindRecogniseUtilForRepeatFlag.recogniseWeekFlag(mMatchContent);
        reCalculateRemindTime();
        int year = RemindRecogniseUtilForYear._mYear;
        int month = RemindRecogniseUtilForMonth._mMonth;
        int date = RemindRecogniseUtilForDate._mDate;
        int hour = RemindRecogniseUtilForTime._mHour;
        int minute = RemindRecogniseUtilForTime._mMinute;
//ID20121010001 hujinrong begin
        boolean isSetDate = RemindRecogniseUtilForDate._mIsSetDate;
        boolean isSetTime = RemindRecogniseUtilForTime._mIsSetTime;
//ID20121010001 hujinrong end
        char[] flag = RemindRecogniseUtilForRepeatFlag._mFlag;
        mRemind.setTime(year, month, date, hour, minute, flag);
        if (isTimeCorrected()) {
            mRemind.assembleImformation();
        } else {
            mRemind.assembleWrongTime();
        }
//ID20121010001 hujinrong begin        
        //is content get date key word
        if(!isSetDate && !isSetTime){
        	mRemind._reminderDay = "999999";
        }
        //is content get time key word
        if(!isSetTime){
        	mRemind._reminderTime = "9999";
        }
//ID20121010001 hujinrong end        
    }

    public RemindObject getRemindObject() {
        return mRemind;
    }

    public String stringToNumber(String content) {
//ID201214001 hujinrong begin
        Pattern pattern = Pattern.compile(".*?([1234567890一二三四五六七八九十百千两]+)(.*)");
        String numberChinese = "一二三四五六七八九";
        String numberMath = "123456789" ;  
        String nextContent = content;
        Matcher matcher = pattern.matcher(nextContent);
        while(matcher.matches()){
            String matchGroup1 = matcher.group(1);
            String matchGroup2 = matcher.group(2);
            int number = 0;
            String numberStr = "";
            try {
                number =stringPreHandlingModule.getInt(matchGroup1);
                numberStr = number+ "";
            } catch (Exception e) {
            	//碰到“一八”上面会转义失败，这步转换成1,8
				String sourceMatcherGroup = matchGroup1;
				numberStr = sourceMatcherGroup;
				int numberChineseLength = numberChinese.length();
				for (int i = 0; i < numberChineseLength; i++) {
					char source = numberChinese.charAt(i);
					if (numberStr.contains(source + "")) {
//ID20121121001 hujinrong begin
						numberStr = numberStr.replace(source + "",
						        numberMath.charAt(i) + ",");
//ID20121121001 hujinrong end
					}
				}
				if (numberStr.endsWith(",")) {
					numberStr = numberStr.substring(0, numberStr.length() - 1);
				}
				matchGroup1 = sourceMatcherGroup;
            }
            content = content.replace(matchGroup1, numberStr);
            nextContent = matchGroup2;
            matcher = pattern.matcher(nextContent);
            
        }
//ID20130513001 hujinrong begin
        content = content.replaceAll("零点", "0点");
        content = content.replaceAll("零", "");
//ID20130513001 hujinrong end 
        return content;
//ID201214001 hujinrong end
    }

    /**
     * resetRecogniserParams
     */
    public void resetRecogniserParams() {

        RemindRecogniseUtilForYear._mYear = 0;
        RemindRecogniseUtilForMonth._mMonth = 0;
        RemindRecogniseUtilForMonth.mIsMonthSetByAuther = false;
        RemindRecogniseUtilForDate._mDate = 0;
        RemindRecogniseUtilForDate.mIsDateSetByAuther = false;
//ID20121010001 hujinrong begin
        RemindRecogniseUtilForDate._mIsSetDate = false;
//ID20121010001 hujinrong end
        RemindRecogniseUtilForTime._mHour = 0;
        RemindRecogniseUtilForTime.mIsHourSetByAuther = false;

        RemindRecogniseUtilForTime._mMinute = 0;
        RemindRecogniseUtilForTime.mIsMinuteSetByAnther = false;
//ID20121010001 hujinrong  begin        
        RemindRecogniseUtilForTime._mIsSetTime = false;
//ID20121010001 hujinrong  end
        char a[] = null;
        RemindRecogniseUtilForRepeatFlag._mFlag = a = new char[8];

        for (int i = 0; i < a.length; i++) {
            a[i] = '0';
        }

    }

    /**
     * reCalculateReimdTime by flag
     */
    public void reCalculateRemindTime() {

        int year = RemindRecogniseUtilForYear._mYear;
        int month = RemindRecogniseUtilForMonth._mMonth;
        int date = RemindRecogniseUtilForDate._mDate;
        int hour = RemindRecogniseUtilForTime._mHour;
        int minute = RemindRecogniseUtilForTime._mMinute;
        char[] flag = RemindRecogniseUtilForRepeatFlag._mFlag;

        Calendar remindTime = Calendar.getInstance();
        remindTime.set(Calendar.YEAR, year);
        remindTime.set(Calendar.MONTH, month - 1);
        remindTime.set(Calendar.DAY_OF_MONTH, date);
        remindTime.set(Calendar.HOUR_OF_DAY, hour);
        remindTime.set(Calendar.MINUTE, minute);

        Calendar nowTime = Calendar.getInstance();
        if (isTimeBefore(nowTime, remindTime)) {
            char first = flag[0];
            if (first != '0') {
                RemindRecogniseUtilForDate.mIsDateSetByAuther = false;
                if (first == '1') {
                    RemindRecogniseUtilForDate.resetDate(1);
                } else if (first == '2') {
                    int dayOfWeek = nowTime.get(Calendar.DAY_OF_WEEK);
                    char flag1[] = new char[7];
                    for (int i = 0; i < flag1.length; i++) {
                        flag1[i] = flag[i + 1];
                    }
                    char temp = flag1[0];
                    for (int k = 0; k < flag.length - 2; k++) {
                        flag1[k] = flag1[k + 1];
                    }
                    flag1[flag1.length - 1] = temp;
                    //
                    int i = (dayOfWeek) % 7;

                    int step = 0;
                    for (; step < 7;) {
                        step++;
                        if (flag1[i] == '1')
                            break;
                        i++;
                        i = i % 7;

                    }
                    RemindRecogniseUtilForDate.resetDate(step);
                } else {
                    month = month % 12;
                    // int nextMonthDay =
                    // RemindRecogniseUtilForMonth.dayNumberOfMonth[month];
                    // RemindRecogniseUtilForDate.resetDate(nextMonthDay);
                    RemindRecogniseUtilForMonth.resetMonth(1);
                }
            }
        }
    }

    /**
     * 
     * @param Calendar
     *            now
     * @param Calendar
     *            remind
     * @return check time of now is before time of remind.
     */
    private boolean isTimeBefore(Calendar now, Calendar remind) {
//ID20120825001 hujinrong begin
        String nowYear = now.get(Calendar.YEAR) + "", remindYear = remind.get(Calendar.YEAR) + "";
        String nowMonth = now.get(Calendar.MONTH) + "", remindMonth = remind.get(Calendar.MONTH) + "";
        String nowDate = now.get(Calendar.DAY_OF_MONTH) + "", remindDate = remind.get(Calendar.DAY_OF_MONTH) + "";
        String nowHour = now.get(Calendar.HOUR_OF_DAY) + "", remindHour = remind.get(Calendar.HOUR_OF_DAY) + "";
        String nowMinute = now.get(Calendar.MINUTE) + "", remindMinute = remind.get(Calendar.MINUTE) + "";

        String numbers[] = new String[] { nowMonth, nowDate, nowHour, nowMinute, remindMonth, remindDate, remindHour, remindMinute };

        nowMonth = nowMonth.length() == 2 ? nowMonth : "0" + nowMonth;
        nowDate = nowDate.length() == 2 ? nowDate : "0" + nowDate;
        nowHour = nowHour.length() == 2 ? nowHour : "0" + nowHour;
        nowMinute = nowMinute.length() == 2 ? nowMinute : "0" + nowMinute;

        remindMonth = remindMonth.length() == 2 ? remindMonth : "0" + remindMonth;
        remindDate = remindDate.length() == 2 ? remindDate : "0" + remindDate;
        remindHour = remindHour.length() == 2 ? remindHour : "0" + remindHour;
        remindMinute = remindMinute.length() == 2 ? remindMinute : "0" + remindMinute;

        String nowStr = "" + nowYear + nowMonth + nowDate + nowHour + nowMinute;
        String remindStr = "" + remindYear + remindMonth + remindDate + remindHour + remindMinute;

        long nowLong = Long.parseLong(nowStr);
        long remindLong = Long.parseLong(remindStr);

        if (nowLong >= remindLong)
            return true;
        else
            return false;
//ID20120825001 hujinrong end
    }
// ID20121220001 hujinrong begin
    /**
     * 
     * @return is time generateed Right;
     */
    public boolean isTimeCorrected() {
        int year = RemindRecogniseUtilForYear._mYear;
        int month = RemindRecogniseUtilForMonth._mMonth - 1;
        int date = RemindRecogniseUtilForDate._mDate;
        int hour = RemindRecogniseUtilForTime._mHour;
        int minute = RemindRecogniseUtilForTime._mMinute;
        char flag[] = RemindRecogniseUtilForRepeatFlag._mFlag;

        if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
            RemindRecogniseUtilForMonth.dayNumberOfMonth[1] = 29;
        } else {
            RemindRecogniseUtilForMonth.dayNumberOfMonth[1] = 28;
        }
//ID20130513001 hujinrong begin
        if (month >= 12 || month < 0 || hour > 24 || hour < 0 || minute < 0
                || minute > 60) {
            return false;
        }
//ID20130513001 hujinrong end
        if (date <= 0
                || date > RemindRecogniseUtilForMonth.dayNumberOfMonth[month]) {
        	if(flag[0] == '3'&& (date == 30 || date == 31)){
        		do{
        			month += 1;
        		} while(date > RemindRecogniseUtilForMonth.dayNumberOfMonth[month]);
        		mRemind.set(Calendar.MONTH, month+1);
        		return true;
        	}
            return false;
        }
        return true;
    }
// ID20121220001 hujinrong end
    // public static void main(String args[]){
    // // new RemindRecogniser("每天早上7点30分找我");
    // // new RemindRecogniser("12个小时之后叫我");
    // // for(int i=1000;i<5000;i=i+1){
    // // new MyRemindRecogniser(i+"分钟后是什么日子");
    // // }
    // // new RemindRecogniser("每天晚上8点30分找我");
    // // Pattern pattern = Pattern.compile(".*?\\d+((点钟?)|时)(\\d+)分?.*");
    // // Matcher matcher = pattern.matcher("8点30分叫我");
    // // if(matcher.matches()){
    // // for(int i=0;i<matcher.groupCount()+1;i++){
    // // System.out.println(matcher.group(i));
    // // }
    // ////// }
    // String
    // []tests={"明天上午八点提醒我","中午12点提醒我回家吃饭","下午五点过一会儿提醒我","晚上五点3刻提醒我","每月3号下午三点提醒我",
    // "每周二周三周四周五晚上六点叫我","每周二三四五晚上6点叫我","午时三刻提醒我","明天晚上八点半提醒我","大后天晚上10点半找我","大大后天早上六点叫醒我",
    // "周二34五晚上六点叫我","周二周三45晚上六点叫我","周二周三456,7点教我","每天晚上7点找我","每周三周四周五晚上7点3刻提醒我","周三周四周五每天晚上8点叫我",
    // "每月3号晚上7点过一会儿提醒我","下午3点10分找我","每天晚上8点30分找我","每天早上7点30分找我","明天早上八点半的时候找我","125个小时之后叫我","下午3点2刻加我"};
    //
    // for(int i=0;i<tests.length;i++){
    // new RemindRecogniser(tests[i]);
    // }
    // // Pattern pattern =
    // Pattern.compile(".*?((每?(周|星期|礼拜)[123456789一二三四五六七日])+[123456789一二三四五六七日]*)(\\d(点|时候?))?.*");
    // // Matcher matcher = pattern.matcher("周二周三周三晚上八点教我");
    // // if(matcher.matches()){
    // // for(int i=0;i<matcher.groupCount()+1;i++){
    // // System.out.println(matcher.group(i));
    // // }
    // // }
    // }

}
