package com.iii360.box.set;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.view.IView;

/**
 * 初始化组件
 * @author hefeng
 *
 */
public class SetInitViewActivity extends BaseActivity implements IView, View.OnClickListener {
    protected LinearLayout mVoiceManLayout;
    protected LinearLayout mLedLayout;
    protected Button mLedStartTimeBtn;
    protected Button mLedEndTimeBtn;
    protected View mLedLineView;
    protected LinearLayout mLedTimeLayout;
    protected ImageView mLedSwitchIv;
    protected LinearLayout mSetTestLayout;

    protected LinearLayout mWeatherLayout;
    protected View mWeahterLineView;
    protected LinearLayout mWeatherTimeLayout;
    protected ImageView mWeatherSwitchIv;
    protected Button mWeatherHolidayBtn;
    protected Button mWeatherTimeBtn;
    protected TextView mVoiceManTv;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_box);
        this.initViews() ;
        this.initDatas() ;
    }

    @Override
    public void initViews() {
        // TODO Auto-generated method stub
        mVoiceManLayout = (LinearLayout) findViewById(R.id.set_voice_man_layout);

        mLedLayout = (LinearLayout) findViewById(R.id.set_led_layout);
        mLedStartTimeBtn = (Button) findViewById(R.id.set_led_time_start_btn);
        mLedEndTimeBtn = (Button) findViewById(R.id.set_led_time_end_btn);
        mLedLineView = findViewById(R.id.set_led_line_v);
        mLedTimeLayout = (LinearLayout) findViewById(R.id.set_led_time_layout);
        mLedSwitchIv = (ImageView) findViewById(R.id.set_led_swtich_iv);
        mSetTestLayout = (LinearLayout) findViewById(R.id.set_test_layout);

        mWeatherLayout = (LinearLayout) findViewById(R.id.set_weather_layout);
        mWeahterLineView = findViewById(R.id.set_weather_line_v);
        mWeatherTimeLayout = (LinearLayout) findViewById(R.id.set_weather_time_layout);
        mWeatherSwitchIv = (ImageView) findViewById(R.id.set_led_weather_iv);
        mWeatherHolidayBtn = (Button) findViewById(R.id.set_weather_holiday_btn);
        mWeatherTimeBtn = (Button) findViewById(R.id.set_weather_time_btn);

        mVoiceManTv = (TextView) findViewById(R.id.set_voice_man_tv);

        mVoiceManLayout.setOnClickListener(this);
        mLedLayout.setOnClickListener(this);
        mWeatherLayout.setOnClickListener(this);
        mLedStartTimeBtn.setOnClickListener(this);
        mLedEndTimeBtn.setOnClickListener(this);
        mWeatherLayout.setOnClickListener(this);
        mWeatherHolidayBtn.setOnClickListener(this);
        mWeatherTimeBtn.setOnClickListener(this);
        
        mSetTestLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                Intent i = new Intent();
//                i.setClass(context, TestModelActivity.class);
//                startActivity(i);
            }
        });
    }
    
    @Override
    public void initDatas() {
        // TODO Auto-generated method stub
        setViewHead(R.string.ba_box_set);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        
    }

}
