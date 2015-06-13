package com.iii360.voiceassistant.semanteme.command;

import java.util.Map;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.base.data.CommandInfo;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.parse.IVoiceCommand;
import com.voice.assistant.weather.CityDBHelper;
import com.voice.assistant.weather.CityInfo;
import com.voice.assistant.weather.HttpHandler;
import com.voice.assistant.weather.PraseData;
import com.voice.assistant.weather.WeatherInfo;
import com.voice.common.util.LocUtil;
import com.voice.common.util.StringCallback;

/***
 * 查询天气
 * 
 * @author Peter
 * @data 2015年6月5日下午4:22:36
 */
public class CommandQueryWeather extends AbstractVoiceCommand {

	/****************************** Member Variables ******************************************************/

	private static final String TAG = "CommandQueryWeather";
	public static final String QUERY_URL = "weather/getWeather?city=";
	public static final String QUERY_REALTIME_URL = "http://www.weather.com.cn/data/sk/";
	public static final String WEATHER_TEXT_FORMAT = "(city)(date)(weather)，(wind)，气温：(degree)。";

	private static final int DATA_INDEX_FIRST_DAY = 0;
	private static final int DATA_INDEX_SECOND_DAY = 1;
	private static final int DATA_INDEX_THIRD = 2;
	private static final int DATA_INDEX_FOURTH = 3;
	private static final int DATA_INDEX_FIFTH = 4;
	private String mCityName = null;
	private WeatherInfo mWeatherInfo = null;
	private String mCityCode = "";
	private int mDateIndex = -1;
	private final static int MSG_GET_WHEATHER = 2;

	boolean mIsOutDate = false;
	private static int queryWeatherErrorCount = 0;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GET_WHEATHER:
				LogManager.d("query success");
				String text = getWeatherText().replace("~", "至").replace("N/A,", "").replace("N/A，", "");

				if (text == null || text.trim().equals("")) {
					sendAnswerSession("不好意思，您的网速不给力。请确保您的数据连接正常可用，或者稍后再试。");
					LogManager.e(TAG, "obtain weather info failed");
				} else {
					if (mIsOutDate) {
						sendAnswerSession("您查找的日期超出了小智的能力,给您说说今天的天气吧," + text);
					} else {
						sendAnswerSession(text);
					}
					Map<String, Object> data = getMap();
					data.put("weatherInfo", mWeatherInfo);
				}
				break;
			default:
				break;
			}
		}
	};

	public CommandQueryWeather(BasicServiceUnion union, CommandInfo commandInfo) {
		this(union, commandInfo, "询问天气");
	}

	public CommandQueryWeather(BasicServiceUnion union, CommandInfo commandInfo, String commandDesc) {
		super(union, commandInfo, COMMAND_NAME_WEATHER, commandDesc);
		LogManager.d(TAG, "commandInfo ===>>:" + commandInfo.toCommandString());
		mDateIndex = getDateIndex(commandInfo.getArg(1));
		mCityName = commandInfo.getArg(0);
	}

	@Override
	public IVoiceCommand execute() {
		super.execute();
		mCityCode = getCityCodeAccordCityName(mCityName);
		if (TextUtils.isEmpty(mCityCode)) {
			getCity();
		} else {
			new Thread() {
				@Override
				public void run() {
					queryWeather();
					mHandler.sendEmptyMessage(MSG_GET_WHEATHER);

				}
			}.start();
		}
		return null;
	}

	/**
	 * 获取温度信息
	 * 
	 * @return
	 */
	private boolean queryWeather() {
		HttpHandler httpHandler = new HttpHandler();
		String data = httpHandler.getData(KeyList.DOMAIN_NAME + QUERY_URL + mCityCode);
		String dataTemp = httpHandler.getData(QUERY_REALTIME_URL + mCityCode + ".html");
		PraseData prase = new PraseData(data);
		mWeatherInfo = prase.getWeatherInfo();
		prase.SetData(dataTemp);
		mWeatherInfo._temp = prase.getCurrentTemperature();
		return true;
	}

	/**
	 * 根据语音结果中，城市名称查询CityCode
	 * 
	 * @param cityName
	 * @return
	 */
	private String getCityCodeAccordCityName(String cityName) {
		if (cityName != null && !cityName.equals("")) {
			CityDBHelper db = new CityDBHelper(mContext);
			CityInfo info = db.getCityInfoByName(cityName);
			if (info == null || info._cityCode == null || info._cityCode.equals("")) {
				LogManager.w(TAG, "get cityCode from cityDB is null!");
			} else {
				LogManager.w(TAG, "get cityCode from cityDB is " + info._cityCode);
				return info._cityCode;
			}
		}
		return null;
	}

	private int getDateIndex(String src) {

		if (src != null) {
			int day = 0;
			try {
				day = Integer.valueOf(src);
				if (day > 4 || day < 0) {
					mIsOutDate = true;
					day = 0;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return day;

		}

		return DATA_INDEX_FIRST_DAY;
	}

	private String getWeatherText() {
		String ret = "";
		String date = "";
		String info = "";
		if (mWeatherInfo != null && mWeatherInfo.ItemInfoList != null) {

			switch (mDateIndex) {
			case DATA_INDEX_FIRST_DAY:
				date = "今天";
				info = mWeatherInfo._info;
				break;
			case DATA_INDEX_SECOND_DAY:
				date = "明天";
				break;
			case DATA_INDEX_THIRD:
				date = "后天";
				break;
			case DATA_INDEX_FOURTH:
				date = "大后天";
				break;
			case DATA_INDEX_FIFTH:
				date = "大大后天";
				break;
			default:
				date = "今天";
				info = mWeatherInfo._info;
				break;
			}

			if (mCityName == null) {
				mCityName = "";
			}
			ret = formatWeather(mCityName, date, mWeatherInfo.ItemInfoList[mDateIndex]._weatherName, mWeatherInfo.ItemInfoList[mDateIndex]._wind,
					mWeatherInfo.ItemInfoList[mDateIndex]._temp.replace("-", "零下"), info);
		}
		LogManager.i("CommandQueryWeather", "getWeatherText", "weather info:" + ret);
		return ret;
	}

	private String formatWeather(String city, String date, String weather, String wind, String degree, String note) {
		String ret;
		ret = WEATHER_TEXT_FORMAT.replace("(city)", city);
		ret = ret.replace("(date)", date);
		ret = ret.replace("(weather)", weather);
		ret = ret.replace("(wind)", wind);
		ret = ret.replace("(degree)", degree);
		// ret = ret.replace("(note)", note);

		return ret;
	}

	/**
	 * 根据助手端推送的城市名称或者根据IP定位获取的城市名称播报天气
	 */
	private void getCity() {
		String cityName = mBaseContext.getPrefString(KeyList.PKEY_ASS_CITY_NAME);
		if (TextUtils.isEmpty(cityName)) {
			getCityNameAndReportWeather(cityName);
		} else {
			LocUtil mLocUtil = new LocUtil();
			mLocUtil.getLocationInfo(new StringCallback() {
				@Override
				public void back(String paramString) {
					// TODO Auto-generated method stub
					getCityNameAndReportWeather(paramString);
				}
			});
		}
	}

	/**
	 * 根据手机定位的城市名称，或者根据当前IP定位得到的城市名称，获取citycode
	 * 
	 * @param paramString
	 */
	private void getCityNameAndReportWeather(String paramString) {
		mCityCode = mBaseContext.getPrefString(KeyList.PKEY_ASS_CITY_CODE, null);
		if (TextUtils.isEmpty(mCityCode)) {
			if (TextUtils.isEmpty(paramString)) {
				mCityCode = null;
			} else {
				paramString = paramString.replaceAll("市|县", "");
				mCityCode = getCityCodeAccordCityName(paramString);
			}

		}
		LogManager.d(TAG, "get cityCode from properties or from cityDB cityCode:" + mCityCode);
		mBaseContext.setGlobalBoolean(KeyList.GKEY_CURRENT_CITYCODE_IS_NULL, false);
		if (mCityCode != null && !mCityCode.equals("")) {
			queryWeatherErrorCount = 0;
			new Thread(new Runnable() {
				@Override
				public void run() {
					queryWeather();
					mBaseContext.setPrefString(KeyList.PKEY_ASS_CITY_CODE, mCityCode);
					mHandler.sendEmptyMessage(MSG_GET_WHEATHER);
				}
			}).start();
		} else {
			mBaseContext.setGlobalBoolean(KeyList.GKEY_CURRENT_CITYCODE_IS_NULL, true);
			queryWeatherErrorCount = mBaseContext.getGlobalInteger(KeyList.GKEY_CURRENT_CITYCODE_ERROR_COUNT, 0);
			queryWeatherErrorCount++;
			String errorTTS = "";
			if (queryWeatherErrorCount == 1) {
				errorTTS = "请说出您所在的城市天气比如说上海天气";
			} else if (queryWeatherErrorCount == 2) {
				errorTTS = "未找到你所在城市的天气请再说一次";
			} else if (queryWeatherErrorCount == 3) {
				queryWeatherErrorCount = 0;
				errorTTS = "未能找到您所在城市的天气";
				mBaseContext.setGlobalBoolean(KeyList.GKEY_CURRENT_CITYCODE_IS_NULL, false);
			}
			sendTTSpaly(errorTTS);
		}
		mBaseContext.setGlobalInteger(KeyList.GKEY_CURRENT_CITYCODE_ERROR_COUNT, queryWeatherErrorCount);
	}

}
