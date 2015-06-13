package com.iii360.base.umeng;

import android.content.Context;

import com.iii360.base.umeng.OnlineConfigurationUtil;

/**
 * Umeng参数统一管理
 * 
 * @author Jerome.Hu
 * 
 */
public class UmengOnlineConfig {
	public final static String UMKEY_IFLYTEC_DOWNLOAD_URL = "umkey_xunfei";

	public final static String UMKEY_IS_SET_DEFAULT_RECOGNISE_TO_AI = "set_ai_recognise_default";

	public final static String UMKEY_SPEECH_FAR_DISTANT_RECONGNISE_URL = "speech_far_diatant_recognise_url"; // 思必驰远场识别路径

	public final static String UMKEY_SPEECH_FAR_DISTANT_RES_NAME = "speech_far_diatant_recognise_resname"; // 思必驰远场识别资源名称
	
	public final static String UMKEY_WAKEUP_ENGINE_TYPE ="wakeup_engine_type";

	public static void getOnLineConfigAndProcess(final Context context) {

		getOnLineParams(context);

	}

	public static void getOnLineParams(Context context) {
		OnlineConfigurationUtil onLineConfigurationUtil = new OnlineConfigurationUtil(
				context);

		onLineConfigurationUtil.loadOnLineConfig(UMKEY_IFLYTEC_DOWNLOAD_URL,
				"http://down.360iii.com/download/tts/xunfei_tts.apk");

		onLineConfigurationUtil.loadOnLineConfig(
				UMKEY_IS_SET_DEFAULT_RECOGNISE_TO_AI, "false");
 
		//默认值：http://s-test.api.aispeech.com:36100/api/v3.0/score  资源名称：home
		onLineConfigurationUtil.loadOnLineConfig(
				UMKEY_SPEECH_FAR_DISTANT_RECONGNISE_URL, "http://s.api.aispeech.com/api/v3.0/score");
		onLineConfigurationUtil.loadOnLineConfig(
				UMKEY_SPEECH_FAR_DISTANT_RES_NAME, "home");
		onLineConfigurationUtil.loadOnLineConfig(
				UMKEY_WAKEUP_ENGINE_TYPE, "Speech");
	}

}
