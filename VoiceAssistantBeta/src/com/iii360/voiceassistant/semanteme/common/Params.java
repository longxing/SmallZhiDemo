package com.iii360.voiceassistant.semanteme.common;

import android.content.Context;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.EnCodingUtil;
import com.iii360.base.common.utl.LogManager;
import com.voice.assistant.main.KeyList;

/**
 * 配置语义请求访问服务的请求参数
 * 
 * @author Peter
 * @data 2015年5月20日下午1:22:20
 */
public class Params {
	private Context mContext;
	private BaseContext mBaseContext;
	/**
	 * 此版本号用于的服务返回数据的差异
	 * 
	 * 不同于 SystemUtil.getVersionInfo(mContext); 获取APK定义的版本号
	 */
	private static final String VersionName = "V3.0.7.2";

	public Params(Context context) {
		mContext = context;
		mBaseContext = new BaseContext(mContext);
	}

	String getCommonParams(String channelid, String pUserName, String pCity, String pMainCity, boolean isLogin, String pRecovery, String pNegText, String pLongitude, String pLatitude,
			String pIsNeedClean, String validTime) {
		final String userNamePrefix = "&username=";
		final String channel = "&channel=" + channelid;
		final String ver = "&ver=" + VersionName;
		String userName = userNamePrefix + pUserName;
		final String city = "&city=" + pCity;
		final String mainCity = "&main_city=" + pMainCity;
		if (!isLogin) {
			userName = userNamePrefix;
		}
		final String utfRecovery = "&recovery=" + pRecovery;
		final String negTextUrl = "&negate=" + pNegText;
		final String latitudeUft = "&latitude=" + pLatitude;

		final String longitudeUtf = "&longitude=" + pLongitude;
		final String valid_time = "&valid_time=" + validTime;

		String params = mainCity + city + userName + ver + channel + utfRecovery + negTextUrl + latitudeUft + longitudeUtf + valid_time;

		if (!mBaseContext.getGlobalBoolean(KeyList.GKEY_BOOL_AUTO_CHATED_MODE)) {
			params += "&match_cmd=" + "[(2)]";
			mBaseContext.setGlobalInteger(KeyList.GKEY_VOICE_COMMUNICATION_MODE, KeyList.DEFAULT_MODE);
		} else {
			mBaseContext.setGlobalInteger(KeyList.GKEY_VOICE_COMMUNICATION_MODE, KeyList.AUTO_CHATED_MODE);
		}
		/**
		 * add voice communication mode peter
		 */
		int communicationType = mBaseContext.getGlobalInteger(KeyList.GKEY_VOICE_COMMUNICATION_MODE);
		int communication_cause = mBaseContext.getGlobalInteger(KeyList.GKEY_VOICE_COMMUNICATION_CAUSE);
		if (communicationType == KeyList.AUTO_CHATED_MODE) {
			params += "&communicationType=" + communicationType;
			// 聊天模式下，只有一种场景长按
			if (communication_cause == KeyList.NULL_SCENE_CAUSE || communication_cause == KeyList.LONG_CLICK_CAUSE) {
				params += "&voicemode=" + communication_cause;
			}
		} else {
			params += "&communicationType=" + communicationType;
			params += "&voicemode=" + communication_cause;
		}
		mBaseContext.setGlobalInteger(KeyList.GKEY_VOICE_COMMUNICATION_CAUSE, KeyList.NULL_SCENE_CAUSE);
		LogManager.e("CommonParams:" + params);
		return params;
	}

	public String getCommonParams() {
		String channelid = null;
		String userName = null;
		String city = null;
		String mainCity = null;
		String recovery = null;
		String negText = null;
		String longitude = null;
		String latitude = null;
		boolean isLogin = false;
		String isNeedCleanContext = "";
		String valid_time = "";
		channelid = mBaseContext.getPrefString(KeyList.PKEY_CHANNEL_ID, "");
		userName = mBaseContext.getPrefString(KeyList.PKEY_USER_NAME, "");
		city = mBaseContext.getGlobalString(KeyList.PKEY_ASS_CITY_NAME, "");

		city = EnCodingUtil.getUtfString(city);

		mainCity = mBaseContext.getGlobalString(KeyList.PKEY_ASS_MAIN_CITY_NAME, "");
		mainCity = EnCodingUtil.getUtfString(mainCity);

		recovery = mBaseContext.getGlobalString(KeyList.GKEY_STR_COMFIRM_RECOVERY, "");
		mBaseContext.setGlobalString(KeyList.GKEY_STR_COMFIRM_RECOVERY, "");
		recovery = EnCodingUtil.getUtfString(recovery);

		negText = mBaseContext.getGlobalString(KeyList.GKEY_STR_NEGTEXT, "");
		mBaseContext.setGlobalString(KeyList.GKEY_STR_NEGTEXT, "");
		negText = EnCodingUtil.getUtfString(negText);

		longitude = mBaseContext.getGlobalString(KeyList.GKEY_STR_LONGITUDE, "");
		latitude = mBaseContext.getGlobalString(KeyList.GKEY_STR_LATITUDE, "");
		isLogin = mBaseContext.getGlobalBoolean(KeyList.PKEY_IN_LOGIN, false);

		isNeedCleanContext = mBaseContext.getPrefString(KeyList.PKEY_IS_NEED_CLEAN_CONTEXT, "0");

		mBaseContext.setPrefString(KeyList.PKEY_IS_NEED_CLEAN_CONTEXT, "0");
		valid_time = mBaseContext.getPrefString(KeyList.PKEY_VALID_TIME, (3000 * 1000) + "");
		return getCommonParams(channelid, userName, city, mainCity, isLogin, recovery, negText, longitude, latitude, isNeedCleanContext, valid_time);
	}
}
