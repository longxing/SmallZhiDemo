package com.iii360.sup.common.utl.net;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

import com.iii360.sup.common.utl.LogManager;

public class WTBroadcast extends BroadcastReceiver {

	private static final String Tag = " WTBroadcast";

	public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();

	public void onReceive(Context paramContext, Intent paramIntent) {
		if (paramIntent.getAction().equals("android.net.wifi.SCAN_RESULTS")) {
			LogManager.d(Tag, "=====>>android.net.wifi.SCAN_RESULTS");
			for (int j = 0; j < ehList.size(); j++)
				((EventHandler) ehList.get(j)).scanResultsAvailable();
		} else if (paramIntent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {
			LogManager.d(Tag, " -------->>> android.net.wifi.WIFI_STATE_CHANGED");
			LogManager.printExceptionToServer("android.net.wifi.WIFI_STATE_CHANGED", "onReceive", "WTBroadcast");
			for (int j = 0; j < ehList.size(); j++)
				((EventHandler) ehList.get(j)).wifiStatusNotification();
		} else if (paramIntent.getAction().equals("android.net.wifi.STATE_CHANGE")) {
			Parcelable parcelable = paramIntent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			NetworkInfo networkInfo = (NetworkInfo) parcelable;
			State state = networkInfo.getState();
			LogManager.d(Tag, "---->>>> wifi.STATE_CHANGE  state:" + state.toString());
			LogManager.printExceptionToServer("android.net.wifi.STATE_CHANGE state:" + state.toString(), "onReceive", "WTBroadcast");
			for (int i = 0; i < ehList.size(); i++)
				((EventHandler) ehList.get(i)).handleConnectChange(state);
		}

	}

	public static abstract interface EventHandler {
		public abstract void handleConnectChange(State status);

		public abstract void scanResultsAvailable();

		public abstract void wifiStatusNotification();
	}
}