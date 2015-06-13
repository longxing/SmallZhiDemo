package com.iii360.box.view;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.iii360.box.R;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.TimeUtil;

/**
 * 设置日期和时间的对话框
 * 
 * @author hefeng
 * 
 */
public class DateTimePickerDialog implements OnDateChangedListener, OnTimeChangedListener {
    private TimePicker mTimePicker;
    private DatePicker mDatePicker;
    private Context context;
    private long milliseconds;

    public DateTimePickerDialog(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.init();
    }

    public DateTimePickerDialog(Context context, long milliseconds) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.milliseconds = milliseconds;
        this.init();
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        // TODO Auto-generated method stub
//        LogManager.i("TimePicker " + hourOfDay + ":" + minute);
        mTimePicker.setCurrentMinute(minute);

        if (mOnTimeChangedListener != null) {
            mOnTimeChangedListener.onTimeChanged(view, hourOfDay, minute);
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // TODO Auto-generated method stub
//        LogManager.i("DatePicker " + year + "-" + monthOfYear + "-" + dayOfMonth);
        if (mOnDateChangedListener != null) {
            mOnDateChangedListener.onDateChanged(view, year, monthOfYear, dayOfMonth);
        }
    }

    private void init() {
        if (milliseconds == 0) {
            milliseconds = System.currentTimeMillis();
        }
        View v = LayoutInflater.from(context).inflate(R.layout.view_date_time_picker_dialog, null);
        mDatePicker = (DatePicker) v.findViewById(R.id.datePicker);
        mTimePicker = (TimePicker) v.findViewById(R.id.timePicker);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setCurrentHour(Integer.parseInt(TimeUtil.getHour(milliseconds)));
        mTimePicker.setCurrentMinute(Integer.parseInt(TimeUtil.getMintue(milliseconds)));

        mDatePicker.init(Integer.parseInt(TimeUtil.getYear(milliseconds)), Integer.parseInt(TimeUtil.getMonth(milliseconds)) - 1,
                Integer.parseInt(TimeUtil.getDay(milliseconds)), this);
        mTimePicker.setOnTimeChangedListener(this);
        createDialog(v);
    }

    private Calendar mCalendar;

    private void createDialog(View v) {
        Builder dialog = new AlertDialog.Builder(context);
        dialog.setView(v);
        dialog.setPositiveButton("设置", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                mDatePicker.clearFocus();
                mTimePicker.clearFocus();

                int year = mDatePicker.getYear();
                int monthOfYear = mDatePicker.getMonth();// 这地方一定要注意,Calendar获取到的月份多1，添加月份则少1
                int dayOfMonth = mDatePicker.getDayOfMonth();
                int hourOfDay = mTimePicker.getCurrentHour();
                int minute = mTimePicker.getCurrentMinute();

                LogManager.i("Set time : " + year + "-" + monthOfYear + "-" + dayOfMonth + "  " + hourOfDay + ":" + minute);

                mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);

                if (mOnDateTimeSetListener != null) {
                    mOnDateTimeSetListener.onDateTimeSet(mCalendar.getTimeInMillis());
                }

            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.show();
    }

    private OnDateChangedListener mOnDateChangedListener;

    public void setOnDateChangedListener(OnDateChangedListener OnDateChangedListener) {
        mOnDateChangedListener = OnDateChangedListener;
    }

    // callbacks
    private OnTimeChangedListener mOnTimeChangedListener;

    /**
     * Set the callback that indicates the time has been adjusted by the user.
     * 
     * @param onTimeChangedListener
     *            the callback, should not be null.
     */
    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        mOnTimeChangedListener = onTimeChangedListener;
    }

    private OnDateTimeSetListener mOnDateTimeSetListener;

    public void setOnDateTimeSetListener(OnDateTimeSetListener onDateTimeSetListener) {
        this.mOnDateTimeSetListener = onDateTimeSetListener;
    }

    public interface OnDateTimeSetListener {
        public void onDateTimeSet(long milliseconds);
    }
}