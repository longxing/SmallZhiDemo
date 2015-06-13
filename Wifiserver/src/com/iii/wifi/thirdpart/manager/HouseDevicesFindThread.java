package com.iii.wifi.thirdpart.manager;

import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiDeviceInfos;
import com.iii.wifi.thirdpart.inter.JSSearchDevice;
import com.iii.wifi.util.HardwareUtils;
import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;
import com.iii360.sup.common.utl.net.UdpClient;

public class HouseDevicesFindThread extends Thread {
	private SuperBaseContext mContext;
	final HashMap<String, String> tempHash = new HashMap<String, String>();
	private boolean isRuning = false;
	private JSSearchDevice mJSSearchDevice = null;
	private WifiDeviceInfos mWifiDeviceInfos = null;
	private String mResult = "";
	private Gson gson = new Gson();
	private List<WifiDeviceInfo> mCurrentDeviceList = null;
	private String mNewDeviceGson = "";

	public HouseDevicesFindThread(Context context) {
		mContext = new SuperBaseContext(context);
		mJSSearchDevice = new JSSearchDevice(context);
		mWifiDeviceInfos = new WifiDeviceInfos();
	}

	public void unRegistHuanTengReceiver() {
		if (mJSSearchDevice != null) {
			mJSSearchDevice.unRegistHuanTengReceiver();
		}
	}

	@Override
	public synchronized void run() {
		isRuning = true;
		long StartTime = System.currentTimeMillis();
		while (isRuning) {
			long t1 = System.currentTimeMillis();
			mCurrentDeviceList = mJSSearchDevice.getUnConfigedDeviceList();
			if (mCurrentDeviceList != null && !mCurrentDeviceList.isEmpty()) {

				mWifiDeviceInfos.setWifiInfo(mCurrentDeviceList);
				mNewDeviceGson = gson.toJson(mWifiDeviceInfos);
				mResult = HardwareUtils.ACTION_DEVIDE_LIST_HEAD + mNewDeviceGson;
				UdpClient.getInstance(mContext, true).sendBroadcast(mResult.getBytes());

			} else {
				UdpClient.getInstance(mContext, true).sendBroadcast(HardwareUtils.ACTION_DEVIDE_LIST_HEAD.getBytes());
			}
			LogManager.d("search third house deivces usedTimes:" + (System.currentTimeMillis() - t1));

			while (System.currentTimeMillis() - t1 < 2500) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if ((System.currentTimeMillis() - StartTime) < KeyList.SHORT_DELAY_TIME) {
				mContext.setGlobalBoolean(KeyList.GKEY_WIFI_DOG_FOUNDED, true);
			}
		}
		notify();
	}

	public void stopRuning() {
		isRuning = false;
	}
}
