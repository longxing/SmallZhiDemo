package com.voice.common.util;

import com.iii360.sup.common.utl.LogManager;

import android.content.Context;
import android.net.wifi.WifiManager;

public class AutoConnectWifi {
	private WifiManager wifiManager;
	private Context mContext;
	private boolean flagWifi = false;
	private boolean isOpenByUs = false;

	public AutoConnectWifi(Context context) {
		mContext = context;
	}

	public boolean checkWifi() {
		wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.isWifiEnabled();
	}

	public void OpenWifi() {

		wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
			flagWifi = true;
			isOpenByUs = true;
			LogManager.d(" open wifi ");
		}

	}

	public void resetWifi() {
		flagWifi = checkWifi();
		if (flagWifi && isOpenByUs) {
			OffWifi();
		}
	}

	public void OffWifi() {
		wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(false);
			isOpenByUs = false;
			LogManager.d("off wifi! ");
		}
	}
}
