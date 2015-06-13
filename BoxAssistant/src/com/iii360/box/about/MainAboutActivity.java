package com.iii360.box.about;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iii.wifi.dao.info.WifiBoxSystemInfo;
import com.iii.wifi.dao.newmanager.WifiCRUDForBoxSystem;
import com.iii360.box.MyApplication;
import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.music.MusicPlayListActivity;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogUtil;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.MyProgressDialog;
import com.iii360.box.view.NewViewHead;

public class MainAboutActivity extends BaseActivity implements OnClickListener {
	private TextView ramTvKey, ramTvValue, romTvKey, romTvValue, songNumKey, songNumValue, versionKey, versionValue, macTvKey, macTvValue, ipTvKey, ipTvValue, serialTvkey, serialTvValue,
			songNumKeyNew;
	private TextView batteryTvState;
	private RelativeLayout SongBtn, songLayout;
	private static final int HANDLE_GET_DATA_SUCCESS = 1;
	private static final int HANDLE_GET_DATA_FAIL = 2;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (mProgressDialog != null && !MainAboutActivity.this.isFinishing())
				mProgressDialog.dismiss();
			switch (msg.what) {
			case HANDLE_GET_DATA_SUCCESS:
				showSystemInfo();
				break;
			case HANDLE_GET_DATA_FAIL:
				ToastUtils.show(context, R.string.ba_get_info_error_toast);
				break;
			}
		};
	};
	private WifiBoxSystemInfo systemInfo;
	private MyProgressDialog mProgressDialog;
	private TextView batteryTvKey;
	private TextView batteryTvValue;

	@Override
	protected void onDestroy() {
		if (mProgressDialog != null && !MainAboutActivity.this.isFinishing())
			mProgressDialog.dismiss();
		mProgressDialog = null;
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_about);
		NewViewHead.showLeft(context, "关于音箱");
		setupView();
		addListener();
		mProgressDialog = new MyProgressDialog(context);
		mProgressDialog.setMessage(getString(R.string.ba_update_date));
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();

		WifiCRUDForBoxSystem sys = new WifiCRUDForBoxSystem(BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
		sys.getSystemInfo(new WifiCRUDForBoxSystem.ResultListener() {
			@Override
			public void onResult(String code, WifiBoxSystemInfo info) {

				if (WifiCRUDUtil.isSuccessAll(code)) {
					systemInfo = info;
					handler.sendEmptyMessage(HANDLE_GET_DATA_SUCCESS);
				} else {
					handler.sendEmptyMessage(HANDLE_GET_DATA_FAIL);
				}
				// TODO Auto-generated method stub
				Log.e("hefeng", "code=" + code);
				if (info != null) {
					LogUtil.e(info.getAvailableRomSize() + "/" + info.getRomTotalSize());
					LogUtil.e(info.getAvailableRamSize() + "/" + info.getRamTotalSize());

					LogUtil.e("battery:" + info.getBattery());
					LogUtil.e("ip&mac=" + info.getIp() + "||" + info.getMac());

					LogUtil.e("serial=" + info.getSerial());
					LogUtil.e("version=" + info.getVersionCode());
					LogUtil.e("state=" + info.getCharg_state());
				}
			}
		});

	}

	private void addListener() {
		SongBtn.setOnClickListener(this);

	}

	protected void showSystemInfo() {
		initData();
		ramTvValue.setText(systemInfo.getAvailableRamSize() + "/" + systemInfo.getRamTotalSize());
		romTvValue.setText(systemInfo.getAvailableRomSize() + "/" + systemInfo.getRomTotalSize());
		versionValue.setText(systemInfo.getVersionCode() + "");
		macTvValue.setText(systemInfo.getMac() + "");
		ipTvValue.setText(systemInfo.getIp() + "");
		serialTvValue.setText(systemInfo.getSerial() + "");
		songNumValue.setText("200首");
		if (systemInfo.getBattery() == null) {
			findViewById(R.id.main_about_battery_relative).setVisibility(View.GONE);
			findViewById(R.id.main_about_battery_view).setVisibility(View.GONE);
		}
		if (systemInfo.getCharg_state() == null || "未充电".equals(systemInfo.getCharg_state())) {
			batteryTvState.setVisibility(View.GONE);
			batteryTvValue.setVisibility(View.VISIBLE);
		} else {
			batteryTvState.setVisibility(View.VISIBLE);
			batteryTvValue.setVisibility(View.GONE);
			batteryTvState.setText("" + systemInfo.getCharg_state());
			try {
				int battery = Integer.parseInt(systemInfo.getBattery());
				if (battery >= 99) {
					batteryTvState.setText("已充满");
				}
			} catch (Exception e) {
			}
		}
		batteryTvValue.setText(systemInfo.getBattery() == null ? "100%" : (systemInfo.getBattery().equals("99") ? "100%" : systemInfo.getBattery() + "%"));
		if (MyApplication.isNeedJudgeVersion && (systemInfo.getVersionCode() != null ? systemInfo.getVersionCode() : "").compareTo(KeyList.KEY_HARDVERSION252) <= 0) {
			songLayout.setVisibility(View.VISIBLE);
			SongBtn.setVisibility(View.GONE);
		} else {
			songLayout.setVisibility(View.GONE);
			SongBtn.setVisibility(View.VISIBLE);
		}
		LogUtil.d("" + systemInfo.getVersionCode() + "==" + (systemInfo.getVersionCode() != null ? systemInfo.getVersionCode() : "").compareTo(KeyList.KEY_HARDVERSION252));
	}

	private void initData() {
		ramTvKey.setText("运行内存");
		romTvKey.setText("机身内存");
		songNumKey.setText("本地歌曲数量");
		songNumKeyNew.setText("预置歌曲");
		versionKey.setText("固件版本号");
		macTvKey.setText("MAC地址");
		ipTvKey.setText("IP地址");
		serialTvkey.setText("序列号");
		batteryTvKey.setText("电池电量");
	}

	private void setupView() {
		ramTvKey = (TextView) findViewById(R.id.main_about_ram_tv_key);
		ramTvValue = (TextView) findViewById(R.id.main_about_ram_tv_value);
		romTvKey = (TextView) findViewById(R.id.main_about_rom_tv_key);
		romTvValue = (TextView) findViewById(R.id.main_about_rom_tv_value);
		songNumKey = (TextView) findViewById(R.id.main_about_song_num_tv_key);
		songNumValue = (TextView) findViewById(R.id.main_about_song_num_tv_value);
		versionKey = (TextView) findViewById(R.id.main_about_version_tv_key);
		versionValue = (TextView) findViewById(R.id.main_about_version_tv_value);
		macTvKey = (TextView) findViewById(R.id.main_about_mac_tv_key);
		macTvValue = (TextView) findViewById(R.id.main_about_mac_tv_value);
		ipTvKey = (TextView) findViewById(R.id.main_about_ip_tv_key);
		ipTvValue = (TextView) findViewById(R.id.main_about_ip_tv_value);
		serialTvkey = (TextView) findViewById(R.id.main_about_serial_tv_key);
		serialTvValue = (TextView) findViewById(R.id.main_about_serial_tv_value);
		batteryTvKey = (TextView) findViewById(R.id.main_about_battery_tv_key);
		batteryTvValue = (TextView) findViewById(R.id.main_about_battery_tv_value);
		batteryTvState = (TextView) findViewById(R.id.main_about_battery_tv_state);
		SongBtn = (RelativeLayout) findViewById(R.id.main_about_song_btn);
		songNumKeyNew = (TextView) findViewById(R.id.main_about_song_num_tv_key_new);
		songLayout = (RelativeLayout) findViewById(R.id.main_about_song_show_layout);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_about_song_btn:
			Intent intent = new Intent(getApplicationContext(), MusicPlayListActivity.class);
			intent.putExtra(KeyList.KEY_ISLOCALMUSIC_EXTRA_BOOLEAN, true);
			startActivity(intent);
			break;
		}
	}
}
