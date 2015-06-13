package com.base.util.remind;
// ID20121220001 hujinrong begin
import java.util.Calendar;
// ID20121220001 hujinrong end
public class RemindObject {
    public String _reminderDay = "";

    public String _reminderTime = "";

    public String _repeatFlag = "00000000";

    
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    char[] des = new char[8];
// ID20121220001 hujinrong begin
    public void set(int what,int num) {
    	switch(what){
    	case Calendar.DATE:
    		mDay = num;
    		break;
    	case Calendar.YEAR:
    		mYear = num;
    		break;
    	case Calendar.MONTH:
    		mMonth = num;
    		break;
    	case Calendar.HOUR:
    		mHour = num;
    		break;
    	case Calendar.MINUTE:
    		mMinute = num;
    		break;
    	}
    }
// ID20121220001 hujinrong end
    public void setTime(int year, int month, int day, int hour, int minute,
            char a[]) {
        mYear = year;
        mMonth = month;
        mDay = day;
        mHour = hour;
        mMinute = minute;

        for (int i = 0; i < a.length; i++) {
            des[i] = a[i];
        }

    }

    public void assembleWrongTime() {
        _reminderDay = "19000000";
        _reminderTime = "0000";
        _repeatFlag = new String(des);
    }

    public void assembleImformation() {
        String year = mYear + "";
        String month = mMonth + "";
        String day = mDay + "";
        String hour = mHour + "";
        String minute = mMinute + "";
        if (this.mMonth < 10) {
            month = "0" + mMonth;
        }
        if (this.mDay < 10) {
            day = "0" + mDay;
        }
        if (this.mHour < 10) {
            hour = "0" + mHour;
        }
        if (this.mMinute < 10) {
            minute = "0" + mMinute;
        }
        _reminderDay = year + month + day;
        _reminderTime = hour + minute;
        _repeatFlag = new String(des);

    }

}
