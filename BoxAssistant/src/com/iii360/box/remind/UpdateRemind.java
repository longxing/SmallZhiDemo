package com.iii360.box.remind;

import java.util.Calendar;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.TimePicker;

import com.iii.wifi.dao.info.WifiRemindInfos;
import com.iii.wifi.dao.manager.WifiCRUDForRemind;
import com.iii.wifi.dao.manager.WifiCRUDForRemind.ResultForRemindListener;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.TimeUtil;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.DateTimePickerDialog;
import com.iii360.box.view.DateTimePickerDialog.OnDateTimeSetListener;
import com.iii360.box.view.MyTimePickerDialog;
import com.voice.common.util.Remind;

public class UpdateRemind {
    private Calendar mCalendar;
    private Remind remind;
    private Context context;

    public UpdateRemind(Remind remind, Context context) {
        // TODO Auto-generated constructor stub
        this.remind = remind;
        this.context = context;
    }

    public final static int REMIND_TYPE_TIME = 0;
    public final static int REMIND_TYPE_DATE_TIME = 1;

    /**
     * 创建时间对话框
     * 
     * @param key
     */
    public void createTimeDialog(int type, boolean loopTime) {
        long milliseconds = remind.BaseTime;
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(milliseconds);

        switch (type) {

        case REMIND_TYPE_TIME:
            this.setTime(loopTime);

            break;
        case REMIND_TYPE_DATE_TIME:
            this.setDateTime();

            break;

        default:
            break;
        }

    }

    /**
     * 设置时间，如7:20
     */
    private void setTime(final boolean loopTime) {
        TimePickerDialog dialog = new MyTimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);
                long newTime = mCalendar.getTimeInMillis();
                LogManager.i("设置备忘时间 ：" + TimeUtil.getTime(newTime));

                if (loopTime || newTime > System.currentTimeMillis()) {
                    remind.BaseTime = newTime;
                    remind.creatTime = System.currentTimeMillis();
                    updateRemindTime(remind);
                } else {
                    ToastUtils.show(context, "您设置的时间必须大于当前时间");
                }
            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true); //是否为二十四制
        dialog.setButton(TimePickerDialog.BUTTON_POSITIVE, "确定", dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 设置日期和时间,如2014-04-10 14:20
     */
    private void setDateTime() {
        DateTimePickerDialog dialog = new DateTimePickerDialog(context, mCalendar.getTimeInMillis());
        dialog.setOnDateTimeSetListener(new OnDateTimeSetListener() {
            @Override
            public void onDateTimeSet(long milliseconds) {
                // TODO Auto-generated method stub
                if (milliseconds > System.currentTimeMillis()) {
                    remind.BaseTime = milliseconds;
                    remind.creatTime = System.currentTimeMillis();
                    updateRemindTime(remind);
                } else {
                    ToastUtils.show(context, "您设置的时间必须大于当前时间");
                }
            }
        });
    }

    /**
     * 更新备忘时间
     * 
     * @param remind
     */
    private void updateRemindTime(final Remind remind) {
        LogManager.i("正在更新备忘....");

        final WifiCRUDForRemind mWifiCRUDForRemind = new WifiCRUDForRemind(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mWifiCRUDForRemind.updateRemind(remind, new ResultForRemindListener() {
                    @Override
                    public void onResult(String type, String errorCode, WifiRemindInfos infos) {
                        // TODO Auto-generated method stub
                        if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                            ToastUtils.show(context, "设置成功");
                            context.sendBroadcast(new Intent(KeyList.AKEY_GET_REMIND_DATA));

                        } else {
                            ToastUtils.show(context, "设置失败");
                        }
                    }
                });
            }
        }).start();
    }

}
