package com.iii360.box.set.third;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.iii.wifi.dao.info.HuanTengAccount;
import com.iii.wifi.dao.manager.WifiCRUDForHuanteng;
import com.iii.wifi.dao.manager.WifiCRUDForHuanteng.ResultForHuantengListener;
import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.MyProgressDialog;

public class ThirdSeeHuanTengActivity extends BaseActivity implements OnClickListener {
	private TextView accountTv;
	private TextView addTimeTv;
	private Button unbundBtn;
	private String account;
	private String addTime;
	private int colorDefault = Color.parseColor("#333333");
	private int colorBlue = Color.parseColor("#3898d9");
	private WifiCRUDForHuanteng wifiCRUDForHuanteng;
	protected HuanTengAccount huantengAccount;
	private MyProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_third_see_huanteng);
		wifiCRUDForHuanteng = new WifiCRUDForHuanteng(getBoxIp(), getBoxTcpPort());
		getIntentData();
		setupView();
		addListeners();
		initDatas();

	}

	private void getIntentData() {
		huantengAccount = (HuanTengAccount) getIntent().getSerializableExtra(KeyList.KEY_HUANTENG_BEAN_KEY_EXTRA);
	}

	private class DissmissDialogTask implements Runnable {
		private boolean isSetNull;

		public DissmissDialogTask(boolean isSetNull) {
			this.isSetNull = isSetNull;
		}

		@Override
		public void run() {
			if (dialog != null && !isFinishing()) {
				dialog.dismiss();
			}
			if (isSetNull)
				dialog = null;
		}
	}

	/***
	 * 恭喜SmallZhi 于20141212添加成功
	 */
	private void initDatas() {
		if (huantengAccount == null)
			return;
		// account = "SmallZhi";
		// addTime = "20141212";
		account = huantengAccount.getUsername();
		addTime = new SimpleDateFormat("yyyyMMdd").format(new Date(huantengAccount.getAddTime()));
		SpannableStringBuilder mSpannableStringBuilder = new SpannableStringBuilder("恭喜" + account);
		mSpannableStringBuilder.setSpan(new ForegroundColorSpan(colorDefault), 0, 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		mSpannableStringBuilder.setSpan(new ForegroundColorSpan(colorBlue), 2, ("恭喜" + account).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		accountTv.setText(mSpannableStringBuilder);
		addTimeTv.setTextColor(colorDefault);
		addTimeTv.setText("于" + addTime + "添加成功");
	}

	private void addListeners() {
		unbundBtn.setOnClickListener(this);
	}

	private void setupView() {
		this.setViewHead("查看幻腾");
		accountTv = (TextView) findViewById(R.id.huanteng_add_account);
		addTimeTv = (TextView) findViewById(R.id.huanteng_add_time);
		unbundBtn = (Button) findViewById(R.id.huanteng_unbund_btn);
		dialog = new MyProgressDialog(context);
		dialog.setMessage(getString(R.string.ba_update_date));
		dialog.setCanceledOnTouchOutside(false);
		unbundBtn.setText("解绑");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler.post(new DissmissDialogTask(true));
	}

	private Handler handler = new Handler();

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.huanteng_unbund_btn:

			// ToastUtils.show(getApplicationContext(), "huanteng_unbund_btn");
			wifiCRUDForHuanteng.deleteHuantengData(new ResultForHuantengListener() {

				@Override
				public void onResult(String type, String errorCode, HuanTengAccount userData) {
					// ToastUtils.show(getApplicationContext(), "" + errorCode);
					// Log.i("info", "" + errorCode);
					handler.post(new DissmissDialogTask(false));
					if (WifiCRUDUtil.isSuccessAll(errorCode)) {
						ToastUtils.show(context, "解绑成功");
						handler.post(new Runnable() {
							public void run() {
								finish();
							}
						});
					} else {
						ToastUtils.show(context, R.string.ba_get_data_error_toast);
					}

				}
			});
			break;
		}
	}
}
