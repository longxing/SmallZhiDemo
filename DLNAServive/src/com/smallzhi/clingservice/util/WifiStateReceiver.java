package com.smallzhi.clingservice.util;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.iii360.sup.common.utl.LogManager;

public class WifiStateReceiver extends BroadcastReceiver {
	private ActivityManager mActivityManager;
	private static final String TAG = "SmallZhiDLNA";
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
			// signal strength changed
		} else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {// wifi连接上与否
			LogManager.d(TAG,"wifi state changed");
			NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

			if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
				LogManager.d(TAG,"wifi disconnected");

			} else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {

				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				
				// get current wifi name
				LogManager.d(TAG,"connect to wifi is " + wifiInfo.getSSID());


			}

		} else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);

			if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
				LogManager.d(TAG, "system close wifi");
			} else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
				LogManager.d(TAG, "system open wifi");
			}
			
		}
	}
}
