package com.iii360.box.set;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iii360.box.MainTagActivity;
import com.iii360.box.R;
import com.iii360.box.about.MainContactUsActivity;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.connect.GuideActivity;
import com.iii360.box.set.third.AddThirdAccountActivity;
import com.iii360.box.util.AdaptUtil;
import com.iii360.box.view.MyProgressDialog;

/**
 * @author terry
 * 
 */
public class MainSetActivity extends BaseActivity implements OnClickListener {
	private TextView aboutBack;
	private RelativeLayout myInfoBtn, lightBtn, tianQiBtn, directionBtn, contactUsBtn, myTagBtn, thirdBtn, ttsVolumnBtn;
	private View volumnAboveView;
	private MyProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_set);
		setupView();
		addListener();
		showData();
	}

	private void showData() {
		((TextView) findViewById(R.id.main_set_myinfo_tv)).setText("我的信息");
//		((TextView) findViewById(R.id.main_set_add_nexbox_tv)).setText("连接新音箱");
		// ((TextView) findViewById(R.id.main_set_share_tv)).setText("分享设置");
		((TextView) findViewById(R.id.main_set_light_tv)).setText("夜间模式");
		((TextView) findViewById(R.id.main_set_tianqi_tv)).setText("定时播报天气");
		((TextView) findViewById(R.id.main_set_direction_tv)).setText("使用说明");
		((TextView) findViewById(R.id.main_set_contact_us_tv)).setText("关于我们");
		((TextView) findViewById(R.id.main_set_tag_tv)).setText("我的标签");
		((TextView) findViewById(R.id.main_set_third_tv)).setText("添加第三方账号(幻腾)");
		((TextView) findViewById(R.id.main_set_tts_volumn_tv)).setText("音箱播报音量");
		if (!AdaptUtil.isNewProtocol252()) {
			ttsVolumnBtn.setVisibility(View.GONE);
			volumnAboveView.setVisibility(View.GONE);
		}else{
			ttsVolumnBtn.setVisibility(View.VISIBLE);
			volumnAboveView.setVisibility(View.VISIBLE);
		}
	}

	private class DissmissDialog implements Runnable {
		private boolean isSetNull;

		public DissmissDialog(boolean isSetNull) {
			this.isSetNull = isSetNull;
		}

		@Override
		public void run() {
			if (mProgressDialog != null && !isFinishing())
				mProgressDialog.dismiss();
			if (isSetNull)
				mProgressDialog = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.post(new DissmissDialog(true));
	}

	private Handler handler = new Handler();

	private void addListener() {
		myInfoBtn.setOnClickListener(this);
//		AddBoxBtn.setOnClickListener(this);
		// shareBtn.setOnClickListener(this);
		lightBtn.setOnClickListener(this);
		tianQiBtn.setOnClickListener(this);
		directionBtn.setOnClickListener(this);
		contactUsBtn.setOnClickListener(this);
		myTagBtn.setOnClickListener(this);
		thirdBtn.setOnClickListener(this);
		ttsVolumnBtn.setOnClickListener(this);
	}

	private void setupView() {
		((TextView) findViewById(R.id.head_title_tv)).setText("设置");
		aboutBack = (TextView) findViewById(R.id.head_left_textview);
		aboutBack.setOnClickListener(this);
		myInfoBtn = (RelativeLayout) findViewById(R.id.main_set_myinfo_relative);
//		AddBoxBtn = (RelativeLayout) findViewById(R.id.main_set_add_nexbox_relative);
		// shareBtn = (RelativeLayout)
		// findViewById(R.id.main_set_share_relative);
		lightBtn = (RelativeLayout) findViewById(R.id.main_set_light_relative);
		tianQiBtn = (RelativeLayout) findViewById(R.id.main_set_tianqi_relative);
		directionBtn = (RelativeLayout) findViewById(R.id.main_set_direction_relative);
		contactUsBtn = (RelativeLayout) findViewById(R.id.main_set_contact_us_relative);
		myTagBtn = (RelativeLayout) findViewById(R.id.main_set_tag_relativelayout);
		thirdBtn = (RelativeLayout) findViewById(R.id.main_set_third_relative);
		ttsVolumnBtn = (RelativeLayout) findViewById(R.id.main_set_tts_volumn_relative);
		volumnAboveView = findViewById(R.id.main_set_tts_volumn_view);
		mProgressDialog = new MyProgressDialog(context);
		mProgressDialog.setMessage(getString(R.string.ba_update_date));
		mProgressDialog.setCanceledOnTouchOutside(false);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_textview:
			finish();
			// ToastUtils.show(this, "back");
			break;
		case R.id.main_set_myinfo_relative:
			Intent intent = new Intent(this, UserInfoActivity.class);
			intent.putExtra("modify", "yes");
			startActivity(intent);
			break;
//		case R.id.main_set_add_nexbox_relative:
//			intent = new Intent(this, OnLineBoxListActivity.class);
//			intent.putExtra("inner", "yes");
//			startActivity(intent);
//			break;
		// case R.id.main_set_share_relative:
		// break;
		case R.id.main_set_light_relative:
			startToActvitiyNoFinish(MainSetLEDActivity.class);

			break;
		case R.id.main_set_tianqi_relative:
			startToActvitiyNoFinish(MainSetWeatherActivity.class);
			break;
		case R.id.main_set_direction_relative:
			intent = new Intent(this, GuideActivity.class);
			intent.putExtra("inner", "yes");
			startActivity(intent);
			break;
		case R.id.main_set_contact_us_relative:
			startToActvitiyNoFinish(MainContactUsActivity.class);
			break;
		case R.id.main_set_tag_relativelayout:
			startToActvitiyNoFinish(MainTagActivity.class);
			break;
		case R.id.main_set_third_relative:
			startToActvitiyNoFinish(AddThirdAccountActivity.class);
			break;
		case R.id.main_set_tts_volumn_relative:
			startToActvitiyNoFinish(TTSVolumnActivity.class);
			break;
		}
	}
}
