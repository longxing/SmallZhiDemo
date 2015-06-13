package com.iii.wifi.dao.imf;

import android.content.Context;

import com.iii.wifi.dao.info.WifiWeatherStatusInfo;
import com.iii.wifi.dao.inter.IWifiWeatherStatusDao;
import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;

public class WifiWeatherStatusDao implements IWifiWeatherStatusDao {
	private static final String LED_WEATHER_STATUS = KeyList.PKEY_WEATHER_STATUS;
	private SuperBaseContext mPreferenceUtil;

	public WifiWeatherStatusDao(Context context) {
		mPreferenceUtil = new SuperBaseContext(context);
	}

	@Override
	public void add(final WifiWeatherStatusInfo info) {
		KeyList.messageQueue.post(new Runnable() {
			
			public void run() {
				mPreferenceUtil.setPrefString(LED_WEATHER_STATUS, info.getLedName());
				KeyList.TTSUtil.setWeatherEnable(info.getLedName().equals("true"));
			}
		});
	}

	@Override
	public void delete(WifiWeatherStatusInfo info) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updata(final WifiWeatherStatusInfo info) {
		KeyList.messageQueue.post(new Runnable() {
			
			public void run() {
				mPreferenceUtil.setPrefString(LED_WEATHER_STATUS, info.getLedName());
				LogManager.e(info.getLedName());
				KeyList.TTSUtil.setWeatherEnable(info.getLedName().equals("true"));
			}
		});
	}

	@Override
	public WifiWeatherStatusInfo select() {
		// TODO Auto-generated method stub
		WifiWeatherStatusInfo info = new WifiWeatherStatusInfo();
		String weather = mPreferenceUtil.getPrefString(LED_WEATHER_STATUS, "false");
		if (weather != null && !weather.equals("")) {
			info.setLedName(weather);
		}
		return info;
	}

}
