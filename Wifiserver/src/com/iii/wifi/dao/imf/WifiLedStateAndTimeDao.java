package com.iii.wifi.dao.imf;

import android.content.Context;

import com.iii.wifi.dao.info.WifiLedTimeInfo;
import com.iii.wifi.dao.inter.IWifiLedTimeDao;
import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;

public class WifiLedStateAndTimeDao implements IWifiLedTimeDao {
	private static final String LED_TIME_TYPE = KeyList.PKEY_LED_TIME;
	private static final String LED_STATUS = KeyList.PKEY_LED_STATUS;
	private SuperBaseContext mPreferenceUtil;

	public WifiLedStateAndTimeDao(Context context) {
		mPreferenceUtil = new SuperBaseContext(context);

	}

	@Override
	public void add(WifiLedTimeInfo info) {
		// TODO Auto-generated method stub
		mPreferenceUtil.setPrefString(LED_TIME_TYPE, info.getLedName());
		mPreferenceUtil.setPrefBoolean(LED_STATUS, info.isOpen());
		String from = info.getLedName().substring(0, 4);
		String to = info.getLedName().substring(4);
		LogManager.e("from " + from + " to " + to+"isOpen:"+info.isOpen());
		KeyList.TTSUtil.setLightStateAndTime(from, to, info.isOpen());
	}

	@Override
	public void delete(WifiLedTimeInfo info) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updata(WifiLedTimeInfo info) {
		// TODO Auto-generated method stub
		mPreferenceUtil.setPrefString(LED_TIME_TYPE, info.getLedName());
		mPreferenceUtil.setPrefBoolean(LED_STATUS, info.isOpen());
		String from = info.getLedName().substring(0, 4);
		String to = info.getLedName().substring(4);
		KeyList.TTSUtil.setLightStateAndTime(from, to, info.isOpen());

	}

	@Override
	public WifiLedTimeInfo select() {
		// TODO Auto-generated method stub
		WifiLedTimeInfo info = new WifiLedTimeInfo();
		String time = mPreferenceUtil.getPrefString(LED_TIME_TYPE, "23000700");
		boolean isOpen = mPreferenceUtil.getPrefBoolean(LED_STATUS, false);
		if (time != null && !time.equals("")) {
			info.setLedName(time);
		}
		info.setOpen(isOpen);
		return info;
	}

}
