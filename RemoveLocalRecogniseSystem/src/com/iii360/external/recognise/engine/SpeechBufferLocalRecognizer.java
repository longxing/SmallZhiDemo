package com.iii360.external.recognise.engine;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.common.Util;
import com.aispeech.export.engines.AILocalASREngine;
import com.aispeech.export.engines.AILocalGrammarEngine;
import com.aispeech.export.listeners.AIASRListener;
import com.aispeech.export.listeners.AILocalGrammarListener;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.recognise.IRecogniseSystem;
import com.iii360.base.umeng.UmengUtil;
import com.iii360.external.recognise.RecogniseSystemBufferBuildFactory.IUSCStateListener;
import com.iii360.external.recognise.util.GrammarHelper;

/**
 * 思必驰本地识别引擎，目前主要用家电命令的识别。
 * 
 * @author Peter
 * @data 2015年4月3日下午1:16:29
 */
public class SpeechBufferLocalRecognizer extends AbstractBufferEngine {
	public static final String TAG = "SpeechBufferLocalRecognizer";
	private AILocalGrammarEngine mGrammarEngine;
	private AILocalASREngine mAsrEngine;
	private Context mContext;
	private IUSCStateListener mUSCStateListener;
	private long ts = 0;
	private boolean isOpenVad = false;
	private int vadPauseTime = 900;

	/***
	 * 创建思必驰本地识别对象类
	 * 
	 * @param context 上下问对象
	 * @param listener 识别引擎资源加载状态监听
	 */
	public SpeechBufferLocalRecognizer(Context context, IUSCStateListener listener) {
		super(context);
		mContext = context;
		mUSCStateListener = listener;
		initGrammarEngine();
		// 检测是否已生成并存在识别资源，若已存在，则立即初始化本地识别引擎，否则等待编译生成资源文件后加载本地识别引擎
		if (new File(Util.getResourceDir(mContext) + File.separator + AILocalGrammarEngine.OUTPUT_NAME).exists()) {
			initAsrEngine();
		}

	}

	@Override
	public void initEngineAndsetEngineParam(Object... arg) {
		isOpenVad = (Boolean) arg[0];
		vadPauseTime = (Integer) arg[1];
		if (isOpenVad) {
			mAsrEngine.setVadEnable(true);
			// 断点检测时间
			mAsrEngine.setPauseTime(vadPauseTime);
		} else {
			mAsrEngine.setVadEnable(false);
		}
	}

	@Override
	protected void start() {
		// TODO Auto-generated method stub
		mAsrEngine.start();
		beforeStartRecord();
	}

	@Override
	protected void stop() {
		// TODO Auto-generated method stub
		mAsrEngine.stopRecording();
		ts = System.currentTimeMillis();
	}

	@Override
	protected void cancel() {
		// TODO Auto-generated method stub
		if (mAsrEngine != null) {
			mAsrEngine.cancel();
		}
		if (mGrammarEngine != null) {
			mGrammarEngine.cancel();
		}
	}

	@Override
	public void onDestory() {
		// TODO Auto-generated method stub
		super.onDestory();
		if (mAsrEngine != null) {
			mAsrEngine.destroy();
		}
		if (mGrammarEngine != null) {
			mGrammarEngine.destroy();
		}
	}

	@Override
	public void writePCMData(boolean isLast, byte[] buffer, int bufferLength) {
		// TODO Auto-generated method stub
		buffer = generateNewFixLengthBuffer(buffer, bufferLength);
		mAsrEngine.feedData(buffer);
		LogManager.d(TAG, "writePCMData  to AILocalASREngine  buffer"+buffer.length);
		record(buffer);
	}

	/**
	 * 初始化资源编译引擎
	 */
	private void initGrammarEngine() {
		if (mGrammarEngine != null) {
			mGrammarEngine.destroy();
		}
		mGrammarEngine = AILocalGrammarEngine.createInstance();
		mGrammarEngine.setResFileName("ebnfc.i360.0.0.1.bin");
		mGrammarEngine.init(mContext, new AILocalGrammarListenerImpl(), SpeechOnLineRecognizer.APPKEY, SpeechOnLineRecognizer.SECRETKEY);
	}

	/**
	 * 初始化本地合成引擎
	 */
	@SuppressLint("NewApi")
	private void initAsrEngine() {
		if (mAsrEngine != null) {
			mAsrEngine.destroy();
		}
		mAsrEngine = AILocalASREngine.createInstance();
		mAsrEngine.setResBin("ebnfr.i360.0.0.1.bin");
		mAsrEngine.setNetBin(AILocalGrammarEngine.OUTPUT_NAME, true);
		mAsrEngine.init(mContext, new AIASRListenerImpl(), SpeechOnLineRecognizer.APPKEY, SpeechOnLineRecognizer.SECRETKEY);
		mAsrEngine.setUseXbnfRec(true);
		mAsrEngine.setUseCustomFeed(true);
		mAsrEngine.setUseConf(true);
		mAsrEngine.setVolEnable(true);
		// 断点检测时间
		mAsrEngine.setVadResource("vad.0.10");
		mAsrEngine.setVadEnable(true);
		mAsrEngine.setPauseTime(vadPauseTime);

	}

	/**
	 * 开始生成识别资源
	 */
	private void startResGen() {
		// 生成ebnf语法
		GrammarHelper gh = new GrammarHelper(mContext);
		String contactString = gh.getConatcts();
		String appString = gh.getApps();
		// 如果手机通讯录没有联系人
		if (TextUtils.isEmpty(contactString)) {
			contactString = "无联系人";
		}
		String ebnf = gh.importAssets(contactString, appString, "grammar.xbnf");
		Log.d(TAG, ebnf);
		// 设置ebnf语法
		mGrammarEngine.setEbnf(ebnf);
		// 启动语法编译引擎，更新资源
		mGrammarEngine.update();
	}

	/**
	 * 语法编译引擎回调接口，用以接收相关事件
	 */
	private class AILocalGrammarListenerImpl implements AILocalGrammarListener {

		@Override
		public void onError(AIError error) {
			LogManager.e(TAG, "资源生成发生错误");
		}

		@Override
		public void onInit(int status) {
			if (status == 0) {
				startResGen();
				LogManager.d(TAG, "资源定制引擎加载成功");
			} else {
				LogManager.d(TAG, "资源定制引擎加载失败");
			}
		}

		@Override
		public void onUpdateCompleted(String arg0, String arg1) {
			// TODO Auto-generated method stub
			LogManager.d(TAG, "资源生成/更新成功\npath=" + arg0 + "\n重新加载识别引擎...");
			initAsrEngine();
		}
	}

	/**
	 * 本地识别引擎回调接口，用以接收相关事件
	 */
	private class AIASRListenerImpl implements AIASRListener {

		@Override
		public void onBeginningOfSpeech() {
			LogManager.d(TAG, "onBeginningOfSpeech----检测到说话");

		}

		@Override
		public void onEndOfSpeech() {
			LogManager.d(TAG, "callback result SpeechBufferLocalRecognizer onEndOfSpeech----检测到语音停止，开始识别");
			if (mSpeechOnEndListener != null && isOpenVad) {
				mSpeechOnEndListener.onEnd();
			}
		}

		@Override
		public void onReadyForSpeech() {
			LogManager.i(TAG, "onReadyForSpeech----请说话...");
		}

		@Override
		public void onRmsChanged(float rmsdB) {
		}

		@Override
		public void onError(AIError error) {
			LogManager.i(TAG, "onError----识别发生错误");
			if (!isNeedBackResult()) {
				cancel();
				return;
			}
			ts = System.currentTimeMillis() - ts;
			BaseContext baseContext = new BaseContext(mContext);
			String wakeupWord = baseContext.getGlobalString(KeyList.STRING_WAKEUP_WORD, "");
			if (!wakeupWord.equals("")) {
				wakeupWord += "_";
			}
			// 发送误唤醒统计
			baseContext.sendUmengEvent(UmengUtil.ERROR_WAKEUP, UmengUtil.ERROR_WAKEUP_CONTENT);
//			String path = stopRecord(false, "AISpeechOffline_" + wakeupWord + "$Error");
//			baseContext.setGlobalString(KeyList.PKEY_STRING_SPEECH_LOCAL_FILE_PATH, path);
			baseContext.setGlobalLong(KeyList.PKEY_STRING_SPEECH_LOCAL_TIME, ts);
			if (error != null) {
				System.out.println("aiError is " + error);
				mStateListener.onError(mappingError(error));

			}
			cancel();
		}

		private int mappingError(AIError aiError) {
			switch (aiError.getErrId()) {
			case 70200:
				return IRecogniseSystem.ERROR_NETWORK;
			default:
				return IRecogniseSystem.ERROR_ICANNOT_HEAR;
			}
		}

		@Override
		public void onResults(AIResult results) {
			if (!isNeedBackResult()) {
				cancel();
				return;
			}
			ts = System.currentTimeMillis() - ts;
			LogManager.d(TAG, results.getResultObject().toString());
			if (results.getResultType() == AIConstant.AIENGINE_MESSAGE_TYPE_JSON) {
				try {
					JSONObject jsonResult = new JSONObject((String) results.getResultObject()).getJSONObject("result");
					String result = jsonResult.getString("rec");
					// 会报错
					String conf = jsonResult.getString("conf");
					result = result == null ? null : result.replaceAll(" ", "");
					BaseContext baseContext = new BaseContext(mContext);
					String wakeupWord = baseContext.getGlobalString(KeyList.STRING_WAKEUP_WORD, "");
					if (result == null || result.equals("")) {
						result = wakeupWord + "$Error";
					}
					LogManager.d(TAG, "callback result  SpeechBufferLocalRecognizer end use time:" + ts + "===>>result:" + result);
					if (mStateListener != null) {
						mStateListener.onResult("1," + conf + "," + result);
					}
//					String path = stopRecord(true, "AISpeechOffline_" + result);
//					baseContext.setGlobalString(KeyList.PKEY_STRING_SPEECH_LOCAL_FILE_PATH, path);
					baseContext.setGlobalLong(KeyList.PKEY_STRING_SPEECH_LOCAL_TIME, ts);

				} catch (JSONException e) {
					LogManager.printStackTrace(e);
					// 本地识别，发生错误
					AIError error = new AIError(41003);
					onError(error);
					return;
				}
			}
			cancel();
		}

		@Override
		public void onInit(int status) {
			if (status == 0) {
				LogManager.d(TAG, "本地识别引擎加载成功");
				mUSCStateListener.onLoadComplete(SpeechBufferLocalRecognizer.this);
			} else {
				LogManager.d(TAG, "本地识别引擎加载失败");
			}
		}

		@Override
		public void onRecordReleased() {
		}

		@Override
		public void onBufferReceived(byte[] arg0) {

		}
	}

}
