package com.iii360.box.connect;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.receiver.WifiReceiver;
import com.iii360.box.receiver.WifiReceiver.WifiStateListener;
import com.iii360.box.util.KeyList;

public class UnConnectWifiActivity extends BaseActivity {

	private Button mCheckWifiBtn;
	private boolean entryNoWifi ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_check_wifi);
		entryNoWifi = getIntent().getBooleanExtra(KeyList.PKEY_BOOLEAN_APP_START_NO_WIFI, false);
		mCheckWifiBtn = (Button) findViewById(R.id.main_check_wifi);
		mCheckWifiBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
				startActivity(intent);
			}
		});
		WifiReceiver.setWifiListener(new WifiStateListener() {
			public void onConnect(boolean isConnect, String ssid) {
				if(isConnect){
					if(!isFinishing()){
						if(entryNoWifi){
							startToActvitiyNoFinish(BootActivity.class);
						}
						finish();
						WifiReceiver.setWifiListener(null);
					}
				}
			}
		});
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		WifiReceiver.setWifiListener(null);
	}
}
