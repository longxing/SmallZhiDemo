package com.voice.assistant.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


import com.iii360.sup.common.utl.LogManager;

public class WifiReceiver extends BroadcastReceiver {
	private ActivityManager mActivityManager;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
			// signal strength changed
		} else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {// wifi连接上与否
			LogManager.e("网络状态改变");
			NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

			if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
				LogManager.e("wifi网络连接断开");

			} else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {

				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				// 获取当前wifi名称
				LogManager.e("连接到网络 " + wifiInfo.getSSID());

			}

		} else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {// wifi打开与否
			int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);

			if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
				LogManager.e("系统关闭wifi");
			} else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
				LogManager.e("系统开启wifi");
			}
			
		}
	}
}
