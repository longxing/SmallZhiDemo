package com.iii.wifi.dao.imf;

import javax.crypto.Cipher;

import android.content.Context;

import com.iii.wifi.dao.info.WifiWeatherTimeInfo;
import com.iii.wifi.dao.inter.IWifiWeatherTimeDao;
import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;

public class WifiWeatherStateAndTimeDao implements IWifiWeatherTimeDao {
	private static final String Tag = "WifiWeatherStateAndTimeDao";
	private static final String LED_WEATHER_TIME = KeyList.PKEY_WEATHER_TIME;
	private static final String LED_WEATHER_STATUS = KeyList.PKEY_WEATHER_STATUS;
	private SuperBaseContext mPreferenceUtil;

	public WifiWeatherStateAndTimeDao(Context context) {
		mPreferenceUtil = new SuperBaseContext(context);
	}

	@Override
	public void add(WifiWeatherTimeInfo info) {
		// TODO Auto-generated method stub
		mPreferenceUtil.setPrefString(LED_WEATHER_TIME, info.getTimeingWeatherReportTime());
		mPreferenceUtil.setPrefBoolean(LED_WEATHER_STATUS, info.isOpen());
//		KeyList.TTSUtil.setWeatherEnable(info.isOpen());
//		KeyList.TTSUtil.setWeatherTime(info.getTimeingWeatherReportTime());
		KeyList.TTSUtil.setWeatherStateAndTime(info.getTimeingWeatherReportTime(),info.isOpen());
	}

	@Override
	public void delete(WifiWeatherTimeInfo info) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updata(WifiWeatherTimeInfo info) {
		// TODO Auto-generated method stub
		mPreferenceUtil.setPrefString(LED_WEATHER_TIME, info.getTimeingWeatherReportTime());
		mPreferenceUtil.setPrefBoolean(LED_WEATHER_STATUS, info.isOpen());
		LogManager.e(info.getTimeingWeatherReportTime());
		KeyList.TTSUtil.setWeatherEnable(info.isOpen());
		KeyList.TTSUtil.setWeatherTime(info.getTimeingWeatherReportTime());
//		KeyList.TTSUtil.setWeatherStateAndTime(info.getTimeingWeatherReportTime(),info.isOpen());
	}

	@Override
	public WifiWeatherTimeInfo select() {
		// TODO Auto-generated method stub
		WifiWeatherTimeInfo info = new WifiWeatherTimeInfo();
		String weather = mPreferenceUtil.getPrefString(LED_WEATHER_TIME, "工作日0700");
		boolean isOpen = mPreferenceUtil.getPrefBoolean(LED_WEATHER_STATUS, false);
		if (weather != null && !weather.equals("")) {
			info.setTimeingWeatherReportTime(weather);
		}
		info.setOpen(isOpen);
		return info;
	}

	@Override
	public void SetWeatherReportCityName(String info) {
		// TODO Auto-generated method stub
		LogManager.i(Tag,"SetWeatherReportCityName cityname:"+info);
		mPreferenceUtil.setPrefString(KeyList.PKEY_ASS_CITY_NAME, info);
	}

}
