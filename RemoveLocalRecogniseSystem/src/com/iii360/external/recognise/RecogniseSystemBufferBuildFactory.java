package com.iii360.external.recognise;

import android.content.Context;

import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.recognise.IRecogniseSystem;
import com.iii360.external.recognise.engine.AbstractBufferEngine;
import com.iii360.external.recognise.engine.AbstractRecogniseEngine;
import com.iii360.external.recognise.engine.IRecogniseBaseEngine;
import com.iii360.external.recognise.engine.RecognizerEngine;
import com.iii360.external.recognise.engine.SpeechBufferLocalRecognizer;
import com.iii360.external.recognise.engine.SpeechBufferOnLineRecognizer;
import com.iii360.external.recognise.engine.SpeechOnLineRecognizer;

/**
 * 识别引擎创建工厂类，暂时使用思必驰的在线，思必驰离线识别引擎
 * 
 * @author Peter
 * @data 2015年4月3日下午2:37:44
 */
public class RecogniseSystemBufferBuildFactory {

	private final static String TAG = "RecogniseSystemBufferBuildFactory";

	public static final int ENGINE_TYPE_BUFFER_DOUBLE_DEFAULT = 0; // 默认识别引擎，包括思必驰在线

	public static final int ENGINE_TYPE_BUFFER_ONLINE_AISPEECH = 1; // 思必驰在线

	public static final int ENGINE_TYPE_BUFFER_LOCAL_AISPEECH = 2; // 思必驰离线

	public static int mStartRes = 0;
	public static int mStartChatModeRes = 0;
	public static int mConfirmRes = 0;
	public static int mCancelRes = 0;

	/**
	 * 引擎加载完成回调接口
	 * 
	 * @author Peter
	 * @data 2015年5月6日下午7:33:43
	 */
	public interface IUSCStateListener {
		public void onLoadComplete(AbstractBufferEngine engine);
	}

	/**
	 * 创建识别引擎，根据引擎类型ID
	 * 
	 * @param context
	 * @param online
	 * @param offline
	 * @return
	 */
	public static IRecogniseSystem buildRecogniseSystem(Context context, int typeId) {
		LogManager.i(TAG, "buildRecogniseSystem----typeId:" + typeId);
		final AbstractRecogniseEngine engine = createRecogniseEngine(context, typeId);
		IRecogniseSystem recogniseSystem = new RecogniseSystem(engine, context);
		return recogniseSystem;
	}

	/**
	 * 设置TTS播报资源文件
	 * 
	 * @param startMusicRes
	 * @param startMusicChatModeRes
	 * @param confirmMusicRes
	 * @param cancelMusicRes
	 */
	public static void setMusicRes(int startMusicRes, int startMusicChatModeRes, int confirmMusicRes, int cancelMusicRes) {
		mStartRes = startMusicRes;
		mStartChatModeRes = startMusicChatModeRes;
		mConfirmRes = confirmMusicRes;
		mCancelRes = cancelMusicRes;
	}

	public static AbstractBufferEngine buildBufferRecogniseEngine(Context context, int engineType, IUSCStateListener listener) {
		AbstractBufferEngine bufferEngine = null;
		switch (engineType) {
		case ENGINE_TYPE_BUFFER_ONLINE_AISPEECH:
			bufferEngine = new SpeechBufferOnLineRecognizer(context, listener);
			break;
		case ENGINE_TYPE_BUFFER_LOCAL_AISPEECH:
			bufferEngine = new SpeechBufferLocalRecognizer(context, listener);
			break;
		}
		return bufferEngine;

	}

	public static AbstractRecogniseEngine createRecogniseEngine(Context context, int engineType) {
		LogManager.i(TAG, "createRecogniseEngine");
		AbstractRecogniseEngine engine = null;
		switch (engineType) {
		case ENGINE_TYPE_BUFFER_DOUBLE_DEFAULT:
			engine = new RecognizerEngine(context, mStartRes, mStartChatModeRes, mConfirmRes, mCancelRes, ENGINE_TYPE_BUFFER_ONLINE_AISPEECH);
			break;
		}
		return engine;
	}

	public static IRecogniseBaseEngine createRecogniseBaseEngine(Context context, int engineType) {
		LogManager.i(TAG, "createRecogniseEngine");
		IRecogniseBaseEngine engine = null;
		switch (engineType) {
		case ENGINE_TYPE_BUFFER_ONLINE_AISPEECH:
			engine = new SpeechOnLineRecognizer(context);
			break;
		}
		return engine;
	}
}
