package com.iii.wifi.dao.imf;

import android.content.Context;

import com.iii.wifi.dao.info.WifiLedTimeInfo;
import com.iii.wifi.dao.inter.IWifiLedTimeDao;
import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;

public class WifiLedTimeDao implements IWifiLedTimeDao {
	private static final String LED_TIME_TYPE = KeyList.PKEY_LED_TIME;
	private SuperBaseContext mPreferenceUtil;

	public WifiLedTimeDao(Context context) {
		mPreferenceUtil = new SuperBaseContext(context);

	}

	@Override
	public void add(WifiLedTimeInfo info) {
		// TODO Auto-generated method stub
		mPreferenceUtil.setPrefString(LED_TIME_TYPE, info.getLedName());
		String from = info.getLedName().substring(0, 4);
		String to = info.getLedName().substring(4);
		LogManager.e("from " + from + " to " + to);
		KeyList.TTSUtil.setLightTime(from, to);
	}

	@Override
	public void delete(WifiLedTimeInfo info) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updata(WifiLedTimeInfo info) {
		// TODO Auto-generated method stub
		mPreferenceUtil.setPrefString(LED_TIME_TYPE, info.getLedName());
		String from = info.getLedName().substring(0, 4);
		String to = info.getLedName().substring(4);
		KeyList.TTSUtil.setLightTime(from, to);

	}

	@Override
	public WifiLedTimeInfo select() {
		// TODO Auto-generated method stub
		WifiLedTimeInfo info = new WifiLedTimeInfo();
		String time = mPreferenceUtil.getPrefString(LED_TIME_TYPE, "23000700");
		if (time != null && !time.equals("")) {
			info.setLedName(time);
		}
		return info;
	}

}
