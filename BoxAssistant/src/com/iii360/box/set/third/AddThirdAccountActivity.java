package com.iii360.box.set.third;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iii.wifi.dao.info.HuanTengAccount;
import com.iii.wifi.dao.manager.WifiCRUDForHuanteng;
import com.iii.wifi.dao.manager.WifiCRUDForHuanteng.ResultForHuantengListener;
import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.MyProgressDialog;

public class AddThirdAccountActivity extends BaseActivity implements OnClickListener {
	private RelativeLayout huantengBtn, weixinBtn;
	private TextView huantengSwitchTv, weixinSwitchTv;
	private ImageView huantengSwitchIv, weixinSwitchIv;
	private int seeSource, addSource;
	private WifiCRUDForHuanteng wifiCRUDForHuanteng;
	private HuanTengAccount account;
	private MyProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_third_account);
		wifiCRUDForHuanteng = new WifiCRUDForHuanteng(getBoxIp(), getBoxTcpPort());
		setupView();
		addListeners();
		initData();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		initData();
	}

	private void initData() {
		huantengBtn.setClickable(false);
		account = null;
		dialog.show();
		wifiCRUDForHuanteng.getHuantengData(new ResultForHuantengListener() {
			public void onResult(String type, String errorCode, HuanTengAccount userData) {
				// ToastUtils.show(getApplicationContext(), "" + errorCode);
				// Log.i("info", "" + errorCode);
				handler.post(new DissmissDialogTask(false));
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					if (!isEmpty(userData.getUsername()) && userData.getAddTime() != 0)
						account = userData;
					handler.post(updateViewTaskHuanTeng);
				} else {
					// ToastUtils.show(getApplicationContext(),
					// "errorCode---"+errorCode);
					ToastUtils.show(context, R.string.ba_get_data_error_toast);
				}
			}
		});

		// weixinSwitchTv.setText("查看");
		// weixinSwitchIv.setImageResource(seeSource);
	}

	private Runnable updateViewTaskHuanTeng = new Runnable() {

		@Override
		public void run() {
			huantengBtn.setClickable(true);
			if (account != null) {
				huantengSwitchTv.setText("查看");
				huantengSwitchIv.setImageResource(seeSource);
			} else {
				huantengSwitchTv.setText("添加");
				huantengSwitchIv.setImageResource(addSource);
			}
		}
	};

	private void addListeners() {
		huantengBtn.setOnClickListener(this);
		weixinBtn.setOnClickListener(this);
	}

	private void setupView() {
		seeSource = R.drawable.online_box_item_ico;
		addSource = R.drawable.set_third_add_account;
		this.setViewHead("添加账号");
		((TextView) findViewById(R.id.main_set_third_huanteng_tv)).setText("幻腾");
		dialog = new MyProgressDialog(context);
		dialog.setMessage(getString(R.string.ba_update_date));
		dialog.setCanceledOnTouchOutside(false);
		((TextView) findViewById(R.id.main_set_third_weixin_tv)).setText("微信");
		huantengBtn = (RelativeLayout) findViewById(R.id.main_set_third_huanteng_relative);
		weixinBtn = (RelativeLayout) findViewById(R.id.main_set_third_weixin_relative);
		huantengSwitchTv = (TextView) findViewById(R.id.main_set_third_huanteng_switch_tv);
		weixinSwitchTv = (TextView) findViewById(R.id.main_set_third_weixin_switch_tv);
		huantengSwitchIv = (ImageView) findViewById(R.id.main_set_third_huanteng_iv);
		huantengSwitchIv.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
		weixinSwitchIv = (ImageView) findViewById(R.id.main_set_third_weixin_iv);
		weixinSwitchIv.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
	}

	private Handler handler = new Handler();

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.post(new DissmissDialogTask(true));
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_set_third_huanteng_relative:
			if (account != null) {
				// startToActvitiyNoFinish(ThirdSeeHuanTengActivity.class);
				Intent intent = new Intent(getApplicationContext(), ThirdSeeHuanTengActivity.class);
				intent.putExtra(KeyList.KEY_HUANTENG_BEAN_KEY_EXTRA, account);
				startActivity(intent);
			} else {
				startToActvitiyNoFinish(ThirdAddHuanTengActivity.class);
			}

			// startToActvitiyNoFinish(ThirdSeeHuanTengActivity.class);
			// Toast.makeText(getApplicationContext(),
			// "main_set_third_huanteng_relative", Toast.LENGTH_LONG).show();
			break;
		case R.id.main_set_third_weixin_relative:

			Toast.makeText(getApplicationContext(), "main_set_third_weixin_relative", Toast.LENGTH_LONG).show();
			break;
		}
	}
}
