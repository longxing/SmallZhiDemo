package com.iii360.box.set;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.iii.wifi.dao.manager.WifiCRUDForLedStatus;
import com.iii.wifi.dao.manager.WifiCRUDForLedTime;
import com.iii.wifi.dao.manager.WifiCRUDForTTS;
import com.iii.wifi.dao.manager.WifiCRUDForWeatherStatus;
import com.iii.wifi.dao.manager.WifiCRUDForWeatherTime;
import com.iii360.box.common.BasePreferences;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.WaitUtils;
import com.iii360.box.util.WifiCRUDUtil;

/**
 * 获取盒子设置相关数据
 * 
 * @author hefeng
 * 
 */
public class GetBoxDataHelper {
	private final static String dataHeader = "Get box data is ";
	private final static boolean isDebug = true;
	private BasePreferences mBasePreferences;
	private Context context;
	private Handler mHandler;

	public static final int HADNLER_VOICE_MAN = 0;
	public static final int HADNLER_LED_SWTICH = 1;
	public static final int HADNLER_LED_TIME = 2;
	public static final int HADNLER_WEATHER_SWTICH = 3;
	public static final int HADNLER_WEATHER_TIME = 4;

	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}

	public GetBoxDataHelper(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mBasePreferences = new BasePreferences(context);
	}

	public void pullBoxSetData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				getVoiceMan();
				WaitUtils.sleep(100);

				// getLedSwtich();
				// WaitUtils.sleep(100);
				//
				// getLedTime();
				// WaitUtils.sleep(100);

				getLedTime();
				WaitUtils.sleep(100);

				// getWeatherSwitch();
				// WaitUtils.sleep(100);

				getWeatherTime();
			}
		}).start();
	}

	public void getVoiceMan() {
		WifiCRUDForTTS ttsMan = new WifiCRUDForTTS(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
		ttsMan.select(new WifiCRUDForTTS.ResultForTTSListener() {
			@Override
			public void onResult(String type, String errorCode, String ttsName) {
				// TODO Auto-generated method stub
				if (isDebug) {
					LogManager.i(dataHeader + "voiceman : " + ttsName);
				}

				if (WifiCRUDUtil.isSuccessAll(errorCode) && !TextUtils.isEmpty(ttsName)) {
					ttsName = TTSVoice.indexToName(ttsName);
					mBasePreferences.setPrefString(KeyList.GKEY_VOICE_MAN, ttsName);
					if (mHandler != null) {
						mHandler.sendEmptyMessage(GetBoxDataHelper.HADNLER_VOICE_MAN);
					}
				}
			}
		});
	}

	/**
	 * @deprecated
	 */
	public void getLedSwtich() {
		WifiCRUDForLedStatus ledStatus = new WifiCRUDForLedStatus(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
		ledStatus.select(new WifiCRUDForLedStatus.ResultForLedListener() {
			@Override
			public void onResult(String type, String errorCode, String ledStatus) {
				// TODO Auto-generated method stub
				if (isDebug) {
					LogManager.i(dataHeader + "ledStatus : " + ledStatus);
				}

				if (WifiCRUDUtil.isSuccessAll(errorCode) && !TextUtils.isEmpty(ledStatus)) {
					if (ledStatus.contains("true")) {
						mBasePreferences.setPrefBoolean(KeyList.GKEY_LED_SWITCH, true);
					} else {
						mBasePreferences.setPrefBoolean(KeyList.GKEY_LED_SWITCH, false);
					}
					if (mHandler != null) {
						mHandler.sendEmptyMessage(GetBoxDataHelper.HADNLER_LED_SWTICH);
					}
				}
			}
		});
	}

	public void getLedTime() {
		WifiCRUDForLedTime wLedTime = new WifiCRUDForLedTime(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
		wLedTime.selete(new WifiCRUDForLedTime.ResultForLedStateAndTimeListener(){
			@Override
			public void onResult(String type, String errorCode, String ledName, boolean isOpen) {
				// TODO Auto-generated method stub
				// 时间格式拼接：23000700
				if (isDebug) {
					LogManager.i(dataHeader + "ledName(LedTime) : " + ledName);
				}
				if (WifiCRUDUtil.isSuccessAll(errorCode) && !TextUtils.isEmpty(ledName)) {
					String startTime = ledName.substring(0, ledName.length() / 2);
					String endTime = ledName.substring(ledName.length() / 2, ledName.length());

					mBasePreferences.setPrefString(KeyList.GKEY_LED_START_TIME, startTime);
					mBasePreferences.setPrefString(KeyList.GKEY_LED_END_TIME, endTime);
					if (isOpen) {
						mBasePreferences.setPrefBoolean(KeyList.GKEY_LED_SWITCH, true);
					} else {
						mBasePreferences.setPrefBoolean(KeyList.GKEY_LED_SWITCH, false);
					}
					if (mHandler != null) {
						mHandler.sendEmptyMessage(GetBoxDataHelper.HADNLER_LED_SWTICH);
						mHandler.sendEmptyMessage(GetBoxDataHelper.HADNLER_LED_TIME);
					}
				}
			}
		});
	}
	


	/**
	 * @deprecated
	 */
	public void getWeatherSwitch() {
		WifiCRUDForWeatherStatus weatherStatus = new WifiCRUDForWeatherStatus(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
		weatherStatus.select(new WifiCRUDForWeatherStatus.ResultForWeatherListener() {
			@Override
			public void onResult(String type, String errorCode, String weatherStatus) {
				// TODO Auto-generated method stub
				if (isDebug) {
					LogManager.i(dataHeader + "weatherStatus : " + weatherStatus);
				}
				if (WifiCRUDUtil.isSuccessAll(errorCode) && !TextUtils.isEmpty(weatherStatus)) {
					if (weatherStatus.contains("true")) {
						mBasePreferences.setPrefBoolean(KeyList.GKEY_WEATHER_SWITCH, true);

					} else {
						mBasePreferences.setPrefBoolean(KeyList.GKEY_WEATHER_SWITCH, false);
					}

					if (mHandler != null) {
						mHandler.sendEmptyMessage(GetBoxDataHelper.HADNLER_WEATHER_SWTICH);
					}
				}
			}
		});
	}

	public void getWeatherTime() {
		WifiCRUDForWeatherTime wTime = new WifiCRUDForWeatherTime(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
		wTime.select(new WifiCRUDForWeatherTime.ResultForWeatherStateAndTimeListener() {
			@Override
			public void onResult(String type, String errorCode, String weatherTime, boolean iscallOpen) {
				// TODO Auto-generated method stub
				if (isDebug) {
					LogManager.i(dataHeader + "weatherTime : " + weatherTime);
				}

				if (WifiCRUDUtil.isSuccessAll(errorCode) && !TextUtils.isEmpty(weatherTime)) {
					String startTime = weatherTime.substring(0, weatherTime.length() - 4);
					String endTime = weatherTime.substring(weatherTime.length() - 4, weatherTime.length());
					mBasePreferences.setPrefString(KeyList.GKEY_WEATHER_HOLIDAY, startTime);
					mBasePreferences.setPrefString(KeyList.GKEY_WEATHER_TIME, endTime);
					if (iscallOpen) {
						mBasePreferences.setPrefBoolean(KeyList.GKEY_WEATHER_SWITCH, true);
					} else {
						mBasePreferences.setPrefBoolean(KeyList.GKEY_WEATHER_SWITCH, false);
					}
					if (mHandler != null) {
						mHandler.sendEmptyMessage(GetBoxDataHelper.HADNLER_WEATHER_SWTICH);
						mHandler.sendEmptyMessage(GetBoxDataHelper.HADNLER_WEATHER_TIME);
					}
				}
			}
		});
	}

	/**
	 * 输入0700 转化成 07:00
	 * 
	 * @param time
	 * @return
	 */
	public static String showTime(String time) {
		String start = time.substring(0, time.length() / 2);
		String end = time.substring(time.length() / 2, time.length());
		StringBuffer buff = new StringBuffer(start);
		buff.append(":");
		buff.append(end);
		return buff.toString();
	}

	/**
	 * 填补不足的数据
	 * 
	 * @param hourOfDay
	 * @param minute
	 * @return 0704
	 */
	public static String compsiteTime(int hourOfDay, int minute) {
		String h = "" + hourOfDay;
		String m = "" + minute;

		if (h.length() == 1) {
			h = "0" + h;
		}
		if (m.length() == 1) {
			m = "0" + m;
		}

		return h + m;
	}
}
