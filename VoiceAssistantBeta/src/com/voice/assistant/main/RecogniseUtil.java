package com.voice.assistant.main;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.inf.recognise.IRecogniseSystem;
import com.iii360.base.umeng.OnlineConfigurationUtil;
//import com.iii360.external.recognise.RecogniseSystemBuildFactory;

import android.content.Context;

public class RecogniseUtil {
	public static final int VOICE_RECOGNISE_TYPE_IFLYTEK = 0;
	// ID20120925002 hujinrong begin
	public static final int VOICE_RECOGNISE_TYPE_INSIDE = 1;
	public static final int VOICE_RECOGNISE_TYPE_GOOGLE = 4;
	// ID20120925002 hujinrong end
	public static final int VOICE_RECOGNISE_TYPE_CUSTOM = 2;
	// ID20120504003 liaoyixuan begin
	public static final int VOICE_RECOGNISE_TYPE_OUTSIDE = 3;
	// ID20120504003 liaoyixuan end
	// ID20121106001 hujinrong begin
	public static final int VOICE_RECOGNISE_TYPE_SNDA = 5;
	// ID20121106001 hujinrong end
	// hujinrong begin
	public static final int VOICE_RECOGNISE_TYPE_AICLOUD = 7;
	// hujinrong end
	// ID20130613002 hujinrong begin
	public static final int VOICE_RECOGNISE_TYPE_USC = 8;

	// ID20130613002 hujinrong end

	public static String getRecogniseType(Context context) {
		BaseContext baseContext = new BaseContext(context);
		String type = baseContext.getPrefString(KeyList.PKEY_RECOGNISE_ENGINE, "8");
		if ("1".equals(type) || "5".equals(type)) {
			type = VOICE_RECOGNISE_TYPE_AICLOUD + "";
		}
		return type;
	}

	public static void setRecogniseType(Context context, String type) {
		BaseContext baseContext = new BaseContext(context);
		baseContext.setPrefString(KeyList.PKEY_RECOGNISE_ENGINE, type);
	}

	public static int mappingType(Context context) {
		// BaseContext baseContext = new BaseContext(context);
		// String type =
		// baseContext.getPrefString(KeyList.PKEY_RECOGNISE_ENGINE,VOICE_RECOGNISE_TYPE_USC+"");
		// int typeInt = Integer.parseInt(type);
		// int defaultType = RecogniseSystemBuildFactory.ENGINE_TYPE_USC ;
		//
		// OnlineConfigurationUtil on = new OnlineConfigurationUtil(context);
		// String isAIDefault = on.getOnLineParam("set_ai_recognise_default") ;
		//
		// if( typeInt == VOICE_RECOGNISE_TYPE_AICLOUD ) {
		// defaultType = RecogniseSystemBuildFactory.ENGINE_TYPE_AICLOUD ;
		// } else if( typeInt == VOICE_RECOGNISE_TYPE_IFLYTEK ) {
		// defaultType = RecogniseSystemBuildFactory.ENGINE_TYPE_IFLYTEC ;
		// } else if( typeInt == VOICE_RECOGNISE_TYPE_USC) {
		// if("true".equals(isAIDefault)) {
		// defaultType = RecogniseSystemBuildFactory.ENGINE_TYPE_AICLOUD ;
		// }
		// }
		// return defaultType ;
		return 0;
	}

	/**
	 * 判断是否使用的是云之声识别
	 * 
	 * @param context
	 * @return
	 */
	// ID20130730001 hujinrong begin
	public static boolean isUseUsc(Context context) {
//		BaseContext baseContext = new BaseContext(context);
//		boolean isUseLocalEngine = baseContext.getPrefBoolean(
//				com.iii360.external.recognise.KeyList.PKEY_IS_USE_LOCAL_ENGINE, false);
//		int typeInt = mappingType(context);
//		return typeInt == RecogniseSystemBuildFactory.ENGINE_TYPE_USC && !isUseLocalEngine;
		return false;
	}

	// ID20130730001 hujinrong end

	public static boolean isUseLocalEngine(Context context) {
//		BaseContext baseContext = new BaseContext(context);
//		boolean isUseLocalEngine = baseContext.getPrefBoolean(
//				com.iii360.external.recognise.KeyList.PKEY_IS_USE_LOCAL_ENGINE, false);
//		return isUseLocalEngine;
		return false;
	}

}
