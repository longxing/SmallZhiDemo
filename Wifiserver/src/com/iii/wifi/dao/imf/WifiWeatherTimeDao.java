package com.iii.wifi.dao.imf;

import android.content.Context;

import com.iii.wifi.dao.info.WifiWeatherTimeInfo;
import com.iii.wifi.dao.inter.IWifiWeatherTimeDao;
import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;

public class WifiWeatherTimeDao implements IWifiWeatherTimeDao {
	private static final String LED_WEATHER_TIME = KeyList.PKEY_WEATHER_TIME;
	private SuperBaseContext mPreferenceUtil;

	public WifiWeatherTimeDao(Context context) {
		mPreferenceUtil = new SuperBaseContext(context);
	}

	@Override
	public void add(final WifiWeatherTimeInfo info) {
		KeyList.messageQueue.post(new Runnable() {
			
			public void run() {
				mPreferenceUtil.setPrefString(LED_WEATHER_TIME, info.getTimeingWeatherReportTime());
				LogManager.e(info.getTimeingWeatherReportTime());
				KeyList.TTSUtil.setWeatherTime(info.getTimeingWeatherReportTime());
			}
		});
	}

	@Override
	public void delete(WifiWeatherTimeInfo info) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updata(final WifiWeatherTimeInfo info) {
		KeyList.messageQueue.post(new Runnable() {
			
			public void run() {
				mPreferenceUtil.setPrefString(LED_WEATHER_TIME, info.getTimeingWeatherReportTime());
				LogManager.e(info.getTimeingWeatherReportTime());
				KeyList.TTSUtil.setWeatherTime(info.getTimeingWeatherReportTime());
			}
		});
	}

	@Override
	public WifiWeatherTimeInfo select() {
		// TODO Auto-generated method stub
		WifiWeatherTimeInfo info = new WifiWeatherTimeInfo();
		String weather = mPreferenceUtil.getPrefString(LED_WEATHER_TIME, "工作日0700");
		if (weather != null && !weather.equals("")) {
			info.setTimeingWeatherReportTime(weather);
		}
		return info;
	}

	@Override
	public void SetWeatherReportCityName(String info) {
		// TODO Auto-generated method stub
		mPreferenceUtil.setPrefString(KeyList.PKEY_ASS_CITY_NAME, info);
	}

}
