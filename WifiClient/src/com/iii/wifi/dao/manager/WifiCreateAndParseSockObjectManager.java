package com.iii.wifi.dao.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import android.view.inputmethod.InputBinding;

import com.google.gson.Gson;
import com.iii.wifi.dao.info.HuanTengAccount;
import com.iii.wifi.dao.info.WifiBoxModeInfo;
import com.iii.wifi.dao.info.WifiBoxModeInfos;
import com.iii.wifi.dao.info.WifiBoxSystemInfo;
import com.iii.wifi.dao.info.WifiBoxSystemInfos;
import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiControlInfos;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiDeviceInfos;
import com.iii.wifi.dao.info.WifiJSONObjectForBoxMode;
import com.iii.wifi.dao.info.WifiJSONObjectForBoxSystem;
import com.iii.wifi.dao.info.WifiJSONObjectForControlInfo;
import com.iii.wifi.dao.info.WifiJSONObjectForDeviceInfo;
import com.iii.wifi.dao.info.WifiJSONObjectForHuanTeng;
import com.iii.wifi.dao.info.WifiJSONObjectForLearnHF;
import com.iii.wifi.dao.info.WifiJSONObjectForLearnHFs;
import com.iii.wifi.dao.info.WifiJSONObjectForLedInfo;
import com.iii.wifi.dao.info.WifiJSONObjectForLedTimeInfo;
import com.iii.wifi.dao.info.WifiJSONObjectForMusic;
import com.iii.wifi.dao.info.WifiJSONObjectForPositionInfo;
import com.iii.wifi.dao.info.WifiJSONObjectForReminderInfo;
import com.iii.wifi.dao.info.WifiJSONObjectForRoomInfo;
import com.iii.wifi.dao.info.WifiJSONObjectForString;
import com.iii.wifi.dao.info.WifiJSONObjectForTTSInfo;
import com.iii.wifi.dao.info.WifiJSONObjectForUpdate;
import com.iii.wifi.dao.info.WifiJSONObjectForUserDataInfo;
import com.iii.wifi.dao.info.WifiJSONObjectForUserInfo;
import com.iii.wifi.dao.info.WifiJSONObjectForWeatherInfo;
import com.iii.wifi.dao.info.WifiJSONObjectForWeatherTimeInfo;
import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiJSONObjectMyTagInfo;
import com.iii.wifi.dao.info.WifiJSONObjectVolumeInfo;
import com.iii.wifi.dao.info.WifiLedStatusInfo;
import com.iii.wifi.dao.info.WifiLedStatusInfos;
import com.iii.wifi.dao.info.WifiLedTimeInfo;
import com.iii.wifi.dao.info.WifiLedTimeInfos;
import com.iii.wifi.dao.info.WifiMusicInfo;
import com.iii.wifi.dao.info.WifiMusicInfos;
import com.iii.wifi.dao.info.WifiMyTag;
import com.iii.wifi.dao.info.WifiPositionInfo;
import com.iii.wifi.dao.info.WifiPositionInfos;
import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii.wifi.dao.info.WifiRoomInfos;
import com.iii.wifi.dao.info.WifiStringInfo;
import com.iii.wifi.dao.info.WifiStringInfos;
import com.iii.wifi.dao.info.WifiTTSVocalizationTypeInfo;
import com.iii.wifi.dao.info.WifiTTSVocalizationTypeInfos;
import com.iii.wifi.dao.info.WifiUpdateInfo;
import com.iii.wifi.dao.info.WifiUpdateInfos;
import com.iii.wifi.dao.info.WifiUserData;
import com.iii.wifi.dao.info.WifiUserInfo;
import com.iii.wifi.dao.info.WifiUserInfos;
import com.iii.wifi.dao.info.WifiVolume;
import com.iii.wifi.dao.info.WifiWeatherStatusInfo;
import com.iii.wifi.dao.info.WifiWeatherStatusInfos;
import com.iii.wifi.dao.info.WifiWeatherTimeInfo;
import com.iii.wifi.dao.info.WifiWeatherTimeInfos;
import com.iii.wifi.dao.newmanager.AbsWifiCRUDForObject;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.file.ObjUtil;
import com.voice.common.util.MusicParserUtils;

public class WifiCreateAndParseSockObjectManager {
	// 操作事件
	public static final String WIFI_INFO_TYPE_CONTROL = "0";
	public static final String WIFI_INFO_TYPE_TTS = "1";
	public static final String WIFI_INFO_TYPE_USERS = "2";
	public static final String WIFI_INFO_TYPE_LED = "3";
	public static final String WIFI_INFO_TYPE_LED_TIME = "4";
	public static final String WIFI_INFO_TYPE_WEATHER = "5";
	public static final String WIFI_INFO_TYPE_POSITION = "6";
	public static final String WIFI_INFO_TYPE_ROOM = "7";
	public static final String WIFI_INFO_TYPE_DEVICE = "8";
	public static final String WIFI_INFO_TYPE_WEATHER_TIME = "9";
	public static final String WIFI_INFO_TYPE_COMMON_OPRITE = "10";
	public static final String WIFI_INFO_TYPE_REMIND = "11";
	public static final String WIFI_INFO_TYPE_VOLUME = "12";
	public static final String WIFI_INFO_TYPE_MYTAG = "13";
	public static final String WIFI_INFO_TYPE_USER_DATA = "14";
	public static final String WIFI_INFO_TYPE_BOX_MODE = "15";
	public static final String WIFI_INFO_TYPE_UPDATE = "17";
	public static final String WIFI_INFO_TYPE_STRING = "18";
	// peter add
	public static final String WIFI_INFO_TYPE_LED_SLEEP_SET = "19"; // LED休眠时间设置
	public static final String WIFI_INFO_TYPE_WEATHER_REPORT = "20"; // 定时天气预报设置
	// terry add
	public static final String WIFI_INFO_TYPE_HUANTENG = "21";
	public static final String WIFI_INFO_TYPE_TTS_VOLUME = "22"; // TTS声音大小设置

	// new interface
	public static final String WIFI_INFO_TYPE_MUSIC = "com.iii.wifi.manager.impl.WifiResponseForMusic";
	public static final String WIFI_INFO_TYPE_BOX_SYSTEM = "com.iii.wifi.manager.impl.WifiResponseForBoxSystem";

	// 响应时间代码
	public static final String WIFI_INFO_SUCCESS = "1";
	public static final String WIFI_INFO_ERROR = "-1";
	public static final String WIFI_INFO_DEFAULT = "0";
	public static final String WIFI_INFO_REPEAT = "2";

	public static String createWifiControlInfos(String type, String error, List<WifiControlInfo> infos) {
		WifiControlInfos info = new WifiControlInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_CONTROL);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiControlInfos(String type, String error, WifiControlInfo infos) {
		WifiControlInfos info = new WifiControlInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_CONTROL);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiDeviceInfos(String type, String error, List<WifiDeviceInfo> infos) {
		WifiDeviceInfos info = new WifiDeviceInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_DEVICE);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiDeviceInfos(String type, String error, WifiDeviceInfo infos) {
		WifiDeviceInfos info = new WifiDeviceInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_DEVICE);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiWifiRoomInfos(String type, String error, WifiRoomInfo infos) {
		WifiRoomInfos info = new WifiRoomInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_ROOM);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiWifiRoomInfos(String type, String error, List<WifiRoomInfo> infos) {
		WifiRoomInfos info = new WifiRoomInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_ROOM);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiTTSVocalizationTypeInfos(String type, String error, List<WifiTTSVocalizationTypeInfo> infos) {
		WifiTTSVocalizationTypeInfos info = new WifiTTSVocalizationTypeInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_TTS);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiTTSVocalizationTypeInfos(String type, String error, WifiTTSVocalizationTypeInfo infos) {
		WifiTTSVocalizationTypeInfos info = new WifiTTSVocalizationTypeInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_TTS);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiTTSVocalizationTypeInfos(String type, String error, String TTSType) {
		WifiTTSVocalizationTypeInfo info = new WifiTTSVocalizationTypeInfo();
		if (TTSType != null && !TTSType.equals("")) {
			info.setType(TTSType);
		}
		WifiTTSVocalizationTypeInfos infos = new WifiTTSVocalizationTypeInfos();
		infos.setType(type);
		infos.setWifiInfo(info);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_TTS);
		obj.setError(error);
		obj.setObject(infos);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiUserInfos(String type, String error, List<WifiUserInfo> infos) {
		WifiUserInfos info = new WifiUserInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_USERS);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiUserInfos(String type, String error, WifiUserInfo infos) {
		WifiUserInfos info = new WifiUserInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_USERS);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiUserInfos(String type, String error, String name, String passWord) {
		WifiUserInfo info = new WifiUserInfo();
		info.setName(name);
		info.setPassWord(passWord);
		WifiUserInfos infos = new WifiUserInfos();
		infos.setType(type);
		infos.setWifiInfo(info);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_USERS);
		obj.setError(error);
		obj.setObject(infos);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiLedInfos(String type, String error, List<WifiLedStatusInfo> infos) {
		WifiLedStatusInfos info = new WifiLedStatusInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_LED);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiLedInfos(String type, String error, WifiLedStatusInfo infos) {
		WifiLedStatusInfos info = new WifiLedStatusInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_LED);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiLedInfos(String type, String error, String ledName) {
		WifiLedStatusInfo info = new WifiLedStatusInfo();
		if (!ledName.equals("")) {
			info.setLedName(ledName);
		}
		WifiLedStatusInfos infos = new WifiLedStatusInfos();
		infos.setType(type);
		infos.setWifiInfo(info);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_LED);
		obj.setError(error);
		obj.setObject(infos);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiLedTimeInfos(String type, String error, List<WifiLedTimeInfo> infos) {
		WifiLedTimeInfos info = new WifiLedTimeInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_LED_TIME);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiLedTimeInfos(String type, String error, WifiLedTimeInfo infos) {
		WifiLedTimeInfos info = new WifiLedTimeInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_LED_TIME);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	/**
	 * @deprecated led before interface
	 * @param type
	 * @param error
	 * @param ledTimeName
	 * @return
	 */
	public static String createWifiLedTimeInfos(String type, String error, String ledTimeName) {
		WifiLedTimeInfo info = new WifiLedTimeInfo();
		if (ledTimeName != null && !ledTimeName.equals("")) {
			info.setLedName(ledTimeName);
		}
		WifiLedTimeInfos infos = new WifiLedTimeInfos();
		infos.setType(type);
		infos.setWifiInfo(info);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_LED_TIME);
		obj.setError(error);
		obj.setObject(infos);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	/**
	 * peter add set led sleep time
	 * 
	 * @param type
	 * @param error
	 * @param ledTimeName
	 * @param operateTime
	 * @param isOpen
	 * @return
	 */
	public static String createWifiLedTimeInfos(String type, String error, String ledTimeName, String operateTime, boolean isOpen) {
		WifiLedTimeInfo info = new WifiLedTimeInfo();
		info.setLedName(ledTimeName);
		info.setOpen(isOpen);
		WifiLedTimeInfos infos = new WifiLedTimeInfos();
		infos.setType(type);
		infos.setWifiInfo(info);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_LED_SLEEP_SET);
		obj.setError(error);
		obj.setObject(infos);
		obj.setOperateTime(operateTime);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiWeatherInfos(String type, String error, List<WifiWeatherStatusInfo> infos) {
		WifiWeatherStatusInfos info = new WifiWeatherStatusInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_WEATHER);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiWeatherInfos(String type, String error, WifiWeatherStatusInfo infos) {
		WifiWeatherStatusInfos info = new WifiWeatherStatusInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_WEATHER);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiWeatherInfos(String type, String error, String weatherName) {
		WifiWeatherStatusInfo info = new WifiWeatherStatusInfo();
		if (weatherName != null && !weatherName.equals("")) {
			info.setLedName(weatherName);
		}
		WifiWeatherStatusInfos infos = new WifiWeatherStatusInfos();
		infos.setType(type);
		infos.setWifiInfo(info);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_WEATHER);
		obj.setError(error);
		obj.setObject(infos);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiPositionInfos(String type, String error, List<WifiPositionInfo> infos) {
		WifiPositionInfos info = new WifiPositionInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_POSITION);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiPositionInfos(String type, String error, WifiPositionInfo infos) {
		WifiPositionInfos info = new WifiPositionInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_POSITION);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiPositionInfos(String type, String error, String positionName) {
		WifiPositionInfo info = new WifiPositionInfo();
		if (positionName != null && !positionName.equals("")) {
			info.setLedName(positionName);
		}
		WifiPositionInfos infos = new WifiPositionInfos();
		infos.setType(type);
		infos.setWifiInfo(info);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_POSITION);
		obj.setError(error);
		obj.setObject(infos);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiWeatherTimeInfos(String type, String error, List<WifiWeatherTimeInfo> infos) {
		WifiWeatherTimeInfos info = new WifiWeatherTimeInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_WEATHER_TIME);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiWeatherTimeInfos(String type, String error, WifiWeatherTimeInfo infos) {
		WifiWeatherTimeInfos info = new WifiWeatherTimeInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_WEATHER_TIME);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	/**
	 * @deprecated
	 * @param type
	 * @param error
	 * @param positionName
	 * @return
	 */
	public static String createWifiWeatherTimeInfos(String type, String error, String positionName) {
		WifiWeatherTimeInfo info = new WifiWeatherTimeInfo();
		if (positionName != null && !positionName.equals("")) {
			info.setTimeingWeatherReportTime(positionName);
		}
		WifiWeatherTimeInfos infos = new WifiWeatherTimeInfos();
		infos.setType(type);
		infos.setWifiInfo(info);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_WEATHER_TIME);
		obj.setError(error);
		obj.setObject(infos);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	/**
	 * peter add set timer weather report
	 * 
	 * @param type
	 * @param error
	 * @param positionName
	 * @param operateTime 当天气设置为操作类型时是：城市名称
	 * @param isOpen
	 * @return
	 */
	public static String createWifiWeatherTimeInfos(String type, String error, String reportTime, String operateTime, boolean isOpen) {
		WifiWeatherTimeInfo info = new WifiWeatherTimeInfo();
		if (type.equals(AbsWifiCRUDForObject.OPERATION_TYPE_SET)) { //城市名称设置
			if (reportTime != null && !reportTime.equals("")) {
				info.setWeartherCityName(reportTime);
			}
		} else {
			if (reportTime != null && !reportTime.equals("")) {
				info.setTimeingWeatherReportTime(reportTime);
			}
		}
		info.setOpen(isOpen);
		WifiWeatherTimeInfos infos = new WifiWeatherTimeInfos();
		infos.setType(type);
		infos.setWifiInfo(info);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_WEATHER_REPORT);
		obj.setError(error);
		obj.setObject(infos);
		obj.setOperateTime(operateTime);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiVolumeInfos(String error, WifiVolume wifiVolume) {
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_VOLUME);
		obj.setError(error);
		obj.setObject(wifiVolume);

		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createTTSWifiVolumeInfos(String error, WifiVolume wifiVolume) {
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_TTS_VOLUME);
		obj.setError(error);
		obj.setObject(wifiVolume);

		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiMyTagInfos(String error, WifiMyTag myTag) {
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_MYTAG);
		obj.setError(error);
		obj.setObject(myTag);

		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiUserDataInfos(String error, WifiUserData userData) {
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_USER_DATA);
		obj.setError(error);
		obj.setObject(userData);

		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiHuanTengInfos(String error, HuanTengAccount huantenginfo) {
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_HUANTENG);
		obj.setError(error);
		obj.setObject(huantenginfo);

		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiBoxModeInfos(String type, String error, WifiBoxModeInfo info) {
		WifiBoxModeInfos infos = new WifiBoxModeInfos();
		infos.setType(type);
		infos.setWifiInfo(info);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_BOX_MODE);
		obj.setError(error);
		obj.setObject(infos);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiBoxModeInfos(String type, String error, List<WifiBoxModeInfo> infos) {
		WifiBoxModeInfos info = new WifiBoxModeInfos();
		info.setType(type);
		info.setWifiInfos(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_BOX_MODE);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		return gson.toJson(obj);
	}

	public static String createWifiReminderInfos(String type, String error, List<WifiUserInfo> infos) {
		WifiUserInfos info = new WifiUserInfos();
		info.setType(type);
		info.setWifiInfo(infos);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_USERS);
		obj.setError(error);
		obj.setObject(info);
		Gson gson = new Gson();
		return gson.toJson(obj);
	}

	public static String createWifiMusicInfos(String type, String error, WifiMusicInfo info) {
		WifiMusicInfos infos = new WifiMusicInfos();
		infos.setType(type);
		infos.setWifiInfo(info);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_MUSIC);
		obj.setError(error);
		obj.setObject(infos);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	/**
	 * 播放喜马拉雅 等网络资源音频
	 * 
	 * @param type
	 * @param error
	 * @param infos
	 * @return
	 */
	public static String createWifiMusicInfosForNetResource(String type, String error, WifiMusicInfos infos) {
		infos.setType(type);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_MUSIC);
		obj.setError(error);
		obj.setObject(infos);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiMusicInfosLocal(String type, String error, WifiMusicInfo info, int page) {
		WifiMusicInfos infos = new WifiMusicInfos();
		infos.setType(type);
		infos.setWifiInfo(info);
		infos.setPage(page);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_MUSIC);
		obj.setError(error);
		obj.setObject(infos);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiMusicGoodList(String type, String error, WifiMusicInfo info, int position) {
		WifiMusicInfos infos = new WifiMusicInfos();
		infos.setType(type);
		infos.setWifiInfo(info);
		infos.setPosition(position);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_MUSIC);
		obj.setError(error);
		obj.setObject(infos);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiMusicInfos(String type, String error, String playStatus, String jsonData) {
		List<WifiMusicInfo> list = MusicParserUtils.getMusicList(playStatus, jsonData);

		WifiMusicInfos infos = new WifiMusicInfos();
		infos.setType(type);
		infos.setWifiInfos(list);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_MUSIC);
		obj.setError(error);
		obj.setObject(infos);
		Gson gson = new Gson();
		return gson.toJson(obj);
	}

	public static String createWifiUpdateInfos(String type, String error, WifiUpdateInfo info) {
		WifiUpdateInfos infos = new WifiUpdateInfos();
		infos.setType(type);
		infos.setWifiInfo(info);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_UPDATE);
		obj.setError(error);
		obj.setObject(infos);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiStringInfos(String type, String error, WifiStringInfo info) {
		WifiStringInfos infos = new WifiStringInfos();
		infos.setType(type);
		infos.setInfo(info);
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setType(WIFI_INFO_TYPE_STRING);
		obj.setError(error);
		obj.setObject(infos);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiCommonOpriteInfo(String type, String deviceID) {
		WifiJSONObjectInfo obj = new WifiJSONObjectInfo();
		obj.setError(WIFI_INFO_DEFAULT);
		obj.setType(WIFI_INFO_TYPE_COMMON_OPRITE);
		WifiJSONObjectForLearnHF learnHF = new WifiJSONObjectForLearnHF();
		learnHF.setDeviceID(deviceID);
		learnHF.setType(type);

		WifiJSONObjectForLearnHFs learnhfs = new WifiJSONObjectForLearnHFs();
		learnhfs.setWifiInfo(learnHF);

		obj.setObject(learnhfs);
		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static String createWifiBoxSystemInfos(String type, String error, WifiBoxSystemInfo info) {
		WifiBoxSystemInfos infos = new WifiBoxSystemInfos();
		infos.setType(type);
		infos.setInfo(info);

		WifiJSONObjectInfo obj = createJSONObject(WIFI_INFO_TYPE_BOX_SYSTEM, error, infos);

		Gson gson = new Gson();
		LogManager.i(" =======begin");
		LogManager.i("" + gson.toJson(obj));
		return gson.toJson(obj);
	}

	public static WifiJSONObjectForLearnHF parseWifiCommonLearnHF(String obj) {
		Gson gson = new Gson();
		WifiJSONObjectInfo info = gson.fromJson(obj, WifiJSONObjectInfo.class);

		WifiJSONObjectForLearnHFs learnHF = (WifiJSONObjectForLearnHFs) info.getObject();
		return learnHF.getWifiInfoFirst();
	}

	public static WifiJSONObjectInfo ParseWifiUserInfos(String obj) {
		Gson gson = new Gson();
		LogManager.e("ParseWifiUserInfos:" + obj.toString());
		WifiJSONObjectInfo info = gson.fromJson(obj, WifiJSONObjectInfo.class);

		if (info.getType().equals(WIFI_INFO_TYPE_CONTROL)) {
			WifiJSONObjectForControlInfo infos = gson.fromJson(obj, WifiJSONObjectForControlInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObject());

		} else if (info.getType().equals(WIFI_INFO_TYPE_TTS)) {
			WifiJSONObjectForTTSInfo infos = gson.fromJson(obj, WifiJSONObjectForTTSInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObject());

		} else if (info.getType().equals(WIFI_INFO_TYPE_USERS)) {
			WifiJSONObjectForUserInfo infos = gson.fromJson(obj, WifiJSONObjectForUserInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObject());

		} else if (info.getType().equals(WIFI_INFO_TYPE_LED)) {
			WifiJSONObjectForLedInfo infos = gson.fromJson(obj, WifiJSONObjectForLedInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObject());

		} else if (info.getType().equals(WIFI_INFO_TYPE_LED_TIME)) {
			WifiJSONObjectForLedTimeInfo infos = gson.fromJson(obj, WifiJSONObjectForLedTimeInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObject());

		} else if (info.getType().equals(WIFI_INFO_TYPE_WEATHER)) {
			WifiJSONObjectForWeatherInfo infos = gson.fromJson(obj, WifiJSONObjectForWeatherInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObject());

		} else if (info.getType().equals(WIFI_INFO_TYPE_ROOM)) {
			WifiJSONObjectForRoomInfo infos = gson.fromJson(obj, WifiJSONObjectForRoomInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObject());

		} else if (info.getType().equals(WIFI_INFO_TYPE_DEVICE)) {
			WifiJSONObjectForDeviceInfo infos = gson.fromJson(obj, WifiJSONObjectForDeviceInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObject());

		} else if (info.getType().equals(WIFI_INFO_TYPE_POSITION)) {
			WifiJSONObjectForPositionInfo infos = gson.fromJson(obj, WifiJSONObjectForPositionInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObject());

		} else if (info.getType().equals(WIFI_INFO_TYPE_WEATHER_TIME)) {
			WifiJSONObjectForWeatherTimeInfo infos = gson.fromJson(obj, WifiJSONObjectForWeatherTimeInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObject());

		} else if (info.getType().equals(WIFI_INFO_TYPE_COMMON_OPRITE)) {
			// DO NOTHING HERE

		} else if (info.getType().equals(WIFI_INFO_TYPE_REMIND)) {
			WifiJSONObjectForReminderInfo infos = gson.fromJson(obj, WifiJSONObjectForReminderInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObject());

		} else if (info.getType().equals(WIFI_INFO_TYPE_VOLUME)) {
			WifiJSONObjectVolumeInfo infos = gson.fromJson(obj, WifiJSONObjectVolumeInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObj());

		} else if (info.getType().equals(WIFI_INFO_TYPE_TTS_VOLUME)) {
			WifiJSONObjectVolumeInfo infos = gson.fromJson(obj, WifiJSONObjectVolumeInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObj());
		} else if (info.getType().equals(WIFI_INFO_TYPE_MYTAG)) {
			WifiJSONObjectMyTagInfo infos = gson.fromJson(obj, WifiJSONObjectMyTagInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObj());

		} else if (info.getType().equals(WIFI_INFO_TYPE_USER_DATA)) {
			WifiJSONObjectForUserDataInfo infos = gson.fromJson(obj, WifiJSONObjectForUserDataInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObj());

		} else if (info.getType().equals(WIFI_INFO_TYPE_BOX_MODE)) {
			WifiJSONObjectForBoxMode infos = gson.fromJson(obj, WifiJSONObjectForBoxMode.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObj());

		} else if (info.getType().equals(WIFI_INFO_TYPE_MUSIC)) {

			WifiJSONObjectForMusic infos = gson.fromJson(obj, WifiJSONObjectForMusic.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObj());

		} else if (info.getType().equals(WIFI_INFO_TYPE_UPDATE)) {
			WifiJSONObjectForUpdate infos = gson.fromJson(obj, WifiJSONObjectForUpdate.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObj());

		} else if (info.getType().equals(WIFI_INFO_TYPE_STRING)) {
			WifiJSONObjectForString infos = gson.fromJson(obj, WifiJSONObjectForString.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObj());

		} else if (info.getType().equals(WIFI_INFO_TYPE_BOX_SYSTEM)) {
			WifiJSONObjectForBoxSystem infos = gson.fromJson(obj, WifiJSONObjectForBoxSystem.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObj());
		} else if (info.getType().equals(WIFI_INFO_TYPE_LED_SLEEP_SET)) {
			WifiJSONObjectForLedTimeInfo infos = gson.fromJson(obj, WifiJSONObjectForLedTimeInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObject());
		} else if (info.getType().equals(WIFI_INFO_TYPE_WEATHER_REPORT)) {
			WifiJSONObjectForWeatherTimeInfo infos = gson.fromJson(obj, WifiJSONObjectForWeatherTimeInfo.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObject());
		} else if (info.getType().equals(WIFI_INFO_TYPE_HUANTENG)) {
			WifiJSONObjectForHuanTeng infos = gson.fromJson(obj, WifiJSONObjectForHuanTeng.class);
			info = createJSONObject(infos.getType(), infos.getError(), infos.getObj());
		}

		return info;
	}

	private static WifiJSONObjectInfo createJSONObject(String type, String error, Object obj) {
		WifiJSONObjectInfo mWifiJSONObjectInfo = new WifiJSONObjectInfo();
		mWifiJSONObjectInfo.setType(type);
		mWifiJSONObjectInfo.setError(error);
		if (obj != null) {
			mWifiJSONObjectInfo.setObject(obj);
		}
		return mWifiJSONObjectInfo;
	}
}
