package com.iii.wifi.connect;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public abstract class AbsWifiManager {
	protected static String WIFI_HEAD = "WifiControl AbsWifiManager";
	protected Context context;
	protected WifiManager mWifiManager;
	protected WifiInfo mWifiInfo;

	public AbsWifiManager(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		this.mWifiInfo = mWifiManager.getConnectionInfo();
	}

	public WifiInfo getWifiInfo() {
		return mWifiManager.getConnectionInfo();
	}

	public abstract void destroy();
}
