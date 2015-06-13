package com.voice.assistant.set.weatherbroadcast.receiver;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.inf.recognise.IRecogniseSystem;
import com.iii360.sup.common.utl.NetWorkUtil;
import com.voice.assistant.weather.CityDBHelper;
import com.voice.assistant.weather.CityInfo;
import com.voice.assistant.weather.HttpHandler;
import com.voice.assistant.weather.PraseData;
import com.voice.assistant.weather.WeatherInfo;
import com.voice.assistant.weather.WeatherInfo.ItemInfo;

public class WeatherBroadCast extends BroadcastReceiver {

	public static final String QUERY_URL = "weather/getWeather?city=";
	public static final String QUERY_REALTIME_URL = "http://www.weather.com.cn/data/sk/";
	public static final String IWEATHER_ADDRESS = "http://www.google.com/ig/api?hl=zh-cn&weather=,,,";
	public static final String WEATHER_TEXT_FORMAT = "(city)(date)(weather)，(wind)，气温：(degree)。(note)";

	private static final String DEF_CITY_CODE = "DEF_CITY_CODE";
	private static final int DATA_INDEX_FIRST_DAY = 0;

	private Context mContext;
	private String mCity;
	private WeatherInfo mWeatherInfo;
	private String mCityCode;
	private Editor mEditor;
	private BaseContext mBaseContext;

	private final static int MSG_GET_WHEATHER = 0;
	private long mCurrentTime;
	private long mWeatherTime;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GET_WHEATHER:
				ItemInfo[] mInfo = mWeatherInfo.ItemInfoList;
				if (null == mInfo) {
					LogManager.e("网络不给力，无法播报天气");
					return;
				}

				ItemInfo mItemInfo = mWeatherInfo.ItemInfoList[DATA_INDEX_FIRST_DAY];
				if (null == mItemInfo) {
					LogManager.e("网络不给力，无法播报天气");
					return;
				}

				String text = formatWeather(mCity, "今天", mItemInfo._weatherName, mItemInfo._wind, mItemInfo._temp.replace("-", "零下"), mWeatherInfo._info).replace("~", "至");

				if (text != null && !text.trim().equals("")) {
					try {
					} catch (Exception e) {
						text = "网络异常，无法获取天气信息。";
						return;
					}

				}
				break;
			default:
				break;
			}
		}
	};
	protected IRecogniseSystem mRecogniseSystem;
	protected ITTSController mTTSController;

	@Override
	public void onReceive(Context context, Intent intent) {

		mContext = context;
		mBaseContext = new BaseContext(context);
		mEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		mWeatherTime = mBaseContext.getPrefLong(KeyList.PKEY_SAVE_SET_WEATHER_TIME, System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR);
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(System.currentTimeMillis());
		ca.set(Calendar.SECOND, 0);
		ca.set(Calendar.MILLISECOND, 0);
		mCurrentTime = ca.getTimeInMillis();
		if (isWeek() && formatDate(mCurrentTime).equals(formatDate(mWeatherTime))) {
			if (mBaseContext.getPrefBoolean(KeyList.PKEY_TTS_WEATHER_VIEW, false)) {
				new Thread() {
					@Override
					public void run() {
						if (NetWorkUtil.isNetworkConnected(mContext)) {
							mCity = mBaseContext.getPrefString(KeyList.PKEY_ASS_CITY_NAME, "");
							mCityCode = getDefaultCityCode();

							if (mCityCode != null && mCityCode.equals(DEF_CITY_CODE)) {

							} else if (mCityCode != null && !mCityCode.equals("")) {
								queryWeather();
								mHandler.sendEmptyMessage(MSG_GET_WHEATHER);

							}
						}
					}
				}.start();
			}
			// mPlay.setPlayListener(WeatherPlayListener.getInstance(context));
		}
	}

	/*
	 * 获取当前的星期
	 */
	public int getCurrentWeek() {
		Calendar mCalendar = Calendar.getInstance();
		return mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
	}

	private boolean queryWeather() {
		HttpHandler httpHandler = new HttpHandler(); // HttpHandler
		String data = httpHandler.getData(KeyList.DOMAIN_NAME + QUERY_URL + mCityCode);
		String dataTemp = httpHandler.getData(QUERY_REALTIME_URL + mCityCode + ".html");
		PraseData prase = new PraseData(data);
		mWeatherInfo = prase.getWeatherInfo();
		prase.SetData(dataTemp);
		mWeatherInfo._temp = prase.getCurrentTemperature();
		return true;
	}

	private String getDefaultCityCode() {
		String cityCode = mBaseContext.getPrefString(KeyList.PKEY_ASS_CITY_CODE, null);
		if (cityCode == null || cityCode.equals("")) {
			String city = mBaseContext.getPrefString(KeyList.GKEY_MAP_LOACTION_INFO_CITY, "浦东新区");
			CityDBHelper db = new CityDBHelper(mContext);
			CityInfo info = db.getCityInfoByName(city);
			if (info == null || info._cityCode == null || info._cityCode.equals("")) {
				return DEF_CITY_CODE;
			} else {
				cityCode = info._cityCode;
				mEditor.putString(KeyList.PKEY_ASS_CITY_CODE, cityCode);
				mEditor.commit();
			}
		}
		return cityCode;
	}

	private String formatWeather(String city, String date, String weather, String wind, String degree, String note) {
		String ret;
		ret = WEATHER_TEXT_FORMAT.replace("(city)", city);
		ret = ret.replace("(date)", date);
		ret = ret.replace("(weather)", weather);
		ret = ret.replace("(wind)", wind);
		ret = ret.replace("(degree)", degree);
		ret = ret.replace("(note)", note);
		return ret;
	}

	public boolean isWeek() {

		String allWeek = mBaseContext.getPrefString(KeyList.PKEY_SAVE_SELECT_WEEK, "星期一 星期二 星期三 星期四 星期五 星期六 星期日");

		String weekNumber = mBaseContext.getPrefString(KeyList.PKEY_SET_WEEK_NUMBER, "1 2 3 4 5 6 0");
		String[] allWeekItem = weekNumber.split(" ");
		if (allWeekItem[0].equals("")) {
			return false;
		} else {
			for (int i = 0; i < allWeekItem.length; i++) {
				if (getCurrentWeek() == Integer.parseInt(allWeekItem[i])) {

					return true;
				}
			}
		}
		return false;
	}

	public boolean isOnLinePlay() {
		boolean isOnLine = false;
		final int preSmsOnLine = mBaseContext.getPrefInteger(KeyList.PKEY_TTS_PLAY_CHOOSE, 0);

		if (3 == (preSmsOnLine)) {
			isOnLine = true;
		}
		return isOnLine;
	}

	private String formatDate(long time) {
		Date date = new Date(time);
		java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("hh:mm");
		return f.format(date);
	}
}
