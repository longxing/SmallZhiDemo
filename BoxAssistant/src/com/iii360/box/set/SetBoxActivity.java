package com.iii360.box.set;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.iii360.box.R;
import com.iii360.box.adpter.ListApdater;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.view.ListDialog;
import com.iii360.box.view.MyTimePickerDialog;

/**
 * 音箱设置
 * 
 * @author hefeng
 * 
 */
public class SetBoxActivity extends SetInitViewActivity {
    private boolean mLedSwtich;
    private boolean mWeatherSwtich;
    private String mTime;
    private String mSetTime;//0700
    private List<String> mListData;
    private SendSetBoxData mSendSetBoxData;
    private GetBoxDataHelper mGetBoxDataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initDatas() {
        // TODO Auto-generated method stub
        super.initDatas();
        
        mGetBoxDataHelper = new GetBoxDataHelper(context);
        mGetBoxDataHelper.setHandler(mHandler);
        mGetBoxDataHelper.pullBoxSetData();
        
        mSendSetBoxData = new SendSetBoxData(context);
        
        mHandler.sendEmptyMessage(GetBoxDataHelper.HADNLER_VOICE_MAN);
        mHandler.sendEmptyMessage(GetBoxDataHelper.HADNLER_LED_SWTICH);
        mHandler.sendEmptyMessage(GetBoxDataHelper.HADNLER_LED_TIME);
        mHandler.sendEmptyMessage(GetBoxDataHelper.HADNLER_WEATHER_SWTICH);
        mHandler.sendEmptyMessage(GetBoxDataHelper.HADNLER_WEATHER_TIME);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int what = msg.what;

            switch (what) {

            case GetBoxDataHelper.HADNLER_VOICE_MAN:
                //发音人
                mTime = getPrefString(KeyList.GKEY_VOICE_MAN, KeyList.GKEY_VOICE_MAN_ARRAY[0]);
                mVoiceManTv.setText(mTime);

                break;

            case GetBoxDataHelper.HADNLER_LED_SWTICH:
                //LED开关
                mLedSwtich = getPrefBoolean(KeyList.GKEY_LED_SWITCH, false);
                setShowView(mLedLineView, mLedTimeLayout, mLedSwitchIv, mLedSwtich);

                break;

            case GetBoxDataHelper.HADNLER_LED_TIME:
                if (mLedSwtich) {
                    refreshDatas(VIEW_LED);
                }
                break;

            case GetBoxDataHelper.HADNLER_WEATHER_SWTICH:
                //天气播报开关
                mWeatherSwtich = getPrefBoolean(KeyList.GKEY_WEATHER_SWITCH, false);
                setShowView(mWeahterLineView, mWeatherTimeLayout, mWeatherSwitchIv, mWeatherSwtich);

                break;

            case GetBoxDataHelper.HADNLER_WEATHER_TIME:
                if (mWeatherSwtich) {
                    refreshDatas(VIEW_WEATHER);
                }
                break;

            default:
                break;
            }

        }
    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == mVoiceManLayout) {
            showListDialog(KeyList.GKEY_VOICE_MAN_ARRAY, KeyList.GKEY_VOICE_MAN, mVoiceManTv);

        } else if (v == mLedLayout) {

            mLedSwtich = getPrefBoolean(KeyList.GKEY_LED_SWITCH, false);
            mLedSwtich = !mLedSwtich;
            setPrefBoolean(KeyList.GKEY_LED_SWITCH, mLedSwtich);
            setShowView(mLedLineView, mLedTimeLayout, mLedSwitchIv, mLedSwtich);
            refreshDatas(VIEW_LED);

            ledSwtich = getPrefBoolean(KeyList.GKEY_LED_SWITCH, false);
//            if (ledSwtich) {
                //注：关闭到打开前先发送设置的时间
                sendTime(KeyList.GKEY_LED_START_TIME,ledSwtich);
//            }
            mSendSetBoxData.sendLedSwitch(ledSwtich + "");

        } else if (v == mLedStartTimeBtn) {
            //LED开始时间
            mSetTime = getPrefString(KeyList.GKEY_LED_START_TIME, "2300");
            showTimeDialog(KeyList.GKEY_LED_START_TIME, mLedStartTimeBtn);

        } else if (v == mLedEndTimeBtn) {
            //LED灯结束时间
            mSetTime = getPrefString(KeyList.GKEY_LED_END_TIME, "0700");
            showTimeDialog(KeyList.GKEY_LED_END_TIME, mLedEndTimeBtn);

        } else if (v == mWeatherLayout) {
            //天气开关
            mWeatherSwtich = getPrefBoolean(KeyList.GKEY_WEATHER_SWITCH, false);
            mWeatherSwtich = !mWeatherSwtich;
            setPrefBoolean(KeyList.GKEY_WEATHER_SWITCH, mWeatherSwtich);
            setShowView(mWeahterLineView, mWeatherTimeLayout, mWeatherSwitchIv, mWeatherSwtich);
            refreshDatas(VIEW_WEATHER);

            weatherSwitch = getPrefBoolean(KeyList.GKEY_WEATHER_SWITCH, false);
            mSendSetBoxData.sendWeatherSwitch(weatherSwitch + "");

        } else if (v == mWeatherHolidayBtn) {
            showListDialog(KeyList.GKEY_HOLIDAY_ARRAY, KeyList.GKEY_WEATHER_HOLIDAY, mWeatherHolidayBtn);

        } else if (v == mWeatherTimeBtn) {
            mSetTime = getPrefString(KeyList.GKEY_WEATHER_TIME, "0700");
            showTimeDialog(KeyList.GKEY_WEATHER_TIME, mWeatherTimeBtn);

        }
    }

    /**
     * 显示列表对话框
     * 
     * @param data
     * @param key
     */
    private void showListDialog(String[] data, final String key, final TextView textView) {
        mListData = Arrays.asList(data);
        ListDialog dialog = new ListDialog(this);
        dialog.setAdpter(new ListApdater(context, mListData), new ListDialog.OnListItemClickListener() {
            @Override
            public void onListItemClick(int position) {
                // TODO Auto-generated method stub
                setPrefString(key, mListData.get(position));
                textView.setText(mListData.get(position));

                if (KeyList.GKEY_VOICE_MAN.equals(key)) {
                    voiceMan = getPrefString(KeyList.GKEY_VOICE_MAN, KeyList.GKEY_VOICE_MAN_ARRAY[0]);
                    voiceMan = TTSVoice.nameToIndex(voiceMan);
                    mSendSetBoxData.sendTtsPeople(voiceMan);

                } else if (KeyList.GKEY_WEATHER_HOLIDAY.equals(key)) {
                    weatherTime = getPrefString(KeyList.GKEY_WEATHER_HOLIDAY, "工作日") + getPrefString(KeyList.GKEY_WEATHER_TIME, "0700");
                    mSendSetBoxData.sendWeatherTime(weatherTime,true);
                }
            }
        });
        dialog.show();
    }

    /**
     * 设置显示的组件
     * 
     * @param line
     *            线
     * @param layout
     *            时间布局
     * @param image
     *            开关
     * @param isShow
     *            是否显示
     */
    private void setShowView(View line, LinearLayout layout, ImageView image, boolean isShow) {
        if (isShow) {
            line.setVisibility(View.VISIBLE);
            layout.setVisibility(View.VISIBLE);
            image.setImageResource(R.drawable.ba_switch_btn_on);
        } else {
            line.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);
            image.setImageResource(R.drawable.ba_switch_btn_off);
        }
    }

    private final static int VIEW_LED = 0;
    private final static int VIEW_WEATHER = 1;

    private void refreshDatas(int type) {
        switch (type) {

        case VIEW_LED:
            //LED开始时间
            mTime = getPrefString(KeyList.GKEY_LED_START_TIME, "2300");
            mLedStartTimeBtn.setText(GetBoxDataHelper.showTime(mTime));

            //LED灯结束时间
            mTime = getPrefString(KeyList.GKEY_LED_END_TIME, "0700");
            mLedEndTimeBtn.setText(GetBoxDataHelper.showTime(mTime));

            break;

        case VIEW_WEATHER:
            //天气播报假期时间
            mTime = getPrefString(KeyList.GKEY_WEATHER_HOLIDAY, "工作日");
            mWeatherHolidayBtn.setText(mTime);

            //天气播报时间
            mTime = getPrefString(KeyList.GKEY_WEATHER_TIME, "0700");
            mWeatherTimeBtn.setText(GetBoxDataHelper.showTime(mTime));

            break;

        default:
            break;
        }
    }

    /**
     * 显示设置时间对话框
     * 
     * @param key
     * @param button
     */
    private void showTimeDialog(final String key, final Button button) {
        createTimeDialog();
        setTimeListener(new TimeListener() {
            @Override
            public void onTime(String time) {
                // TODO Auto-generated method stub

                if (button == mLedStartTimeBtn) {
                    String mEndTime = mLedEndTimeBtn.getText().toString();
                    mEndTime = mEndTime.replace(":", "");

                    LogManager.i("set time=" + time + "||mEndTime=" + mEndTime);

                    if (time.equals(mEndTime)) {
                        ToastUtils.show(context, R.string.set_box_time_error_toast);
                        return;
                    }
                } else if (button == mLedEndTimeBtn) {
                    String mStartTime = mLedStartTimeBtn.getText().toString();
                    mStartTime = mStartTime.replace(":", "");
                    LogManager.i("set time=" + time + "||mStartTime=" + mStartTime);

                    if (time.equals(mStartTime)) {
                        ToastUtils.show(context, R.string.set_box_time_error_toast);
                        return;
                    }
                }

                setPrefString(key, mTime);
                button.setText(GetBoxDataHelper.showTime(time));
                sendTime(key,true);
            }
        });
    }

    private void sendTime(String key,boolean isOpen) {
        if (KeyList.GKEY_LED_START_TIME.equals(key) || KeyList.GKEY_LED_END_TIME.equals(key)) {
            ledTime = getPrefString(KeyList.GKEY_LED_START_TIME, "2300") + getPrefString(KeyList.GKEY_LED_END_TIME, "0700");
            mSendSetBoxData.sendLedTime(ledTime,isOpen);

        } else if (KeyList.GKEY_WEATHER_TIME.equals(key) || KeyList.GKEY_WEATHER_TIME.equals(key)) {
            weatherTime = getPrefString(KeyList.GKEY_WEATHER_HOLIDAY, "工作日") + getPrefString(KeyList.GKEY_WEATHER_TIME, "0700");
            mSendSetBoxData.sendWeatherTime(weatherTime,isOpen);

        }
    }

    /**
     * 创建时间对话框
     * 
     * @param key
     */
    private void createTimeDialog() {
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mSetTime.substring(0, 2)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(mSetTime.substring(2, 4)));
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            calendar = Calendar.getInstance();
        }

        TimePickerDialog tDialog = new MyTimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                 TODO Auto-generated method stub
                mTime = GetBoxDataHelper.compsiteTime(hourOfDay, minute);
                if (timeListener != null) {
                    timeListener.onTime(mTime);
                }
                LogManager.i("设置时间：" + mTime);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true); //是否为二十四制
        tDialog.show();
    }

    private TimeListener timeListener;

    public void setTimeListener(TimeListener timeListener) {
        this.timeListener = timeListener;
    }

    public interface TimeListener {
        public void onTime(String time);
    }

    String voiceMan;
    boolean ledSwtich;
    boolean weatherSwitch;
    String ledTime;
    String weatherTime;

}
