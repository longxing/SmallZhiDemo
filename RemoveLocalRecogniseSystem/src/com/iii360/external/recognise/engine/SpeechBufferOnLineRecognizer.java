package com.iii360.external.recognise.engine;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.export.engines.AICloudASREngine;
import com.aispeech.export.listeners.AIASRListener;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.recognise.IRecogniseSystem;
import com.iii360.base.umeng.OnlineConfigurationUtil;
import com.iii360.base.umeng.UmengOnlineConfig;
import com.iii360.base.umeng.UmengUtil;
import com.iii360.external.recognise.RecogniseSystemBufferBuildFactory.IUSCStateListener;

/**
 * 思必驰在线识别引擎
 * 
 * @author Peter
 * @data 2015年4月3日下午1:08:27
 */
public class SpeechBufferOnLineRecognizer extends AbstractBufferEngine {
	private final static String TAG = "SpeechBufferOnLineRecognizer";
	private AICloudASREngine mEngine;
	private IUSCStateListener mUSCStateListener;
	private long ts = 0;
	private boolean isOpenVad = false;
	private int vadPauseTime = 900;

	/**
	 * 思必驰在线识别引擎构造方法
	 * 
	 * @param context 上下文对象
	 * @param listener 引擎资源加载状态监听
	 */
	public SpeechBufferOnLineRecognizer(Context context, IUSCStateListener listener) {
		super(context);
		mUSCStateListener = listener;
		initEngine(context);

	}

	@Override
	public void initEngineAndsetEngineParam(Object... arg) {
		isOpenVad = (Boolean) arg[0];
		vadPauseTime = (Integer) arg[1];
		if (isOpenVad) {
			mEngine.setVadEnable(true);
			mEngine.setPauseTime(vadPauseTime);
		} else {
			mEngine.setVadEnable(false);
		}
	}

	@Override
	protected void start() {
		mEngine.start();
		beforeStartRecord();
	}

	@Override
	protected void stop() {
		// 在线语音Debug播报
		if (KeyList.IS_TTS_DEBUG) {
			KeyList.VOICE_RECOGNIZER_BEGIN = System.currentTimeMillis();
		}
		mEngine.stopRecording();
		ts = System.currentTimeMillis();
	}

	@Override
	protected void cancel() {
		// TODO Auto-generated method stub
		mEngine.cancel();
	}

	@Override
	public void onDestory() {
		// TODO Auto-generated method stub
		super.onDestory();
		mEngine.destroy();
	}

	@Override
	public void writePCMData(boolean isLast, byte[] buffer, int bufferLength) {
		buffer = generateNewFixLengthBuffer(buffer, bufferLength);
		mEngine.feedData(buffer);
		LogManager.d(TAG, "writePCMData  to AICloudASREngine  buffer"+buffer.length);
		record(buffer);
	}

	/**
	 * 初始化在线识别引擎
	 */
	private void initEngine(Context context) {
		OnlineConfigurationUtil onLineConfigurationUtil = new OnlineConfigurationUtil(
				context);
		String path = onLineConfigurationUtil
				.getOnLineParam(UmengOnlineConfig.UMKEY_SPEECH_FAR_DISTANT_RECONGNISE_URL);
		String resName = onLineConfigurationUtil
				.getOnLineParam(UmengOnlineConfig.UMKEY_SPEECH_FAR_DISTANT_RES_NAME);
		LogManager.d(TAG, "set online farDistant recognise pat:" + path
				+ "==>>resName:" + resName);
		mEngine = AICloudASREngine.createInstance();
		mEngine.init(mContext, mSpeechListener, SpeechOnLineRecognizer.APPKEY, SpeechOnLineRecognizer.SECRETKEY);
		mEngine.setNBest(0);
		mEngine.setUseCustomFeed(true);
		mEngine.setUseMock(true);
		// // 是否模拟真实录音速率
		mEngine.setIsSimulateSpeed(false);
		mEngine.setVolEnable(true);
		// 断点检测
		mEngine.setVadResource("vad.0.10");
		mEngine.setVadEnable(true);
		mEngine.setPauseTime(vadPauseTime);
		// mEngine.setCustom("zn360");// 模型资源
		if (path != null && !path.equals("null") && resName != null
				&& !resName.equals("null")) {
			mEngine.setServer(path);
			mEngine.setRes(resName);
		}
	}

	/*
	 * 实例化思必驰在线引擎监听器
	 */
	private AIASRListener mSpeechListener = new AIASRListener() {
		@Override
		public void onBeginningOfSpeech() {
			LogManager.d(TAG, "onBeginningOfSpeech----检测到说话");
		}

		@Override
		public void onEndOfSpeech() {
			// ts = System.currentTimeMillis();
			LogManager.d(TAG, "callback result SpeechBufferOnLineRecognizer onEndOfSpeech----检测到语音停止，开始识别...");
			if (mSpeechOnEndListener != null && isOpenVad) {
				mSpeechOnEndListener.onEnd();
			}
		}

		@Override
		public void onRmsChanged(float rmsdB) {
		}

		@Override
		public void onResults(AIResult results) {
			if (results.isLast()) {
				ts = System.currentTimeMillis() - ts;
				LogManager.d(TAG, results.getResultObject().toString());
				// 结果按概率由大到小排序
				if (results.getResultType() == AIConstant.AIENGINE_MESSAGE_TYPE_JSON) {
					try {
						JSONObject jsonResult = new JSONObject((String) results.getResultObject()).getJSONObject("result");
						String result = jsonResult.getString("rec");
						result = result == null ? null : result.replaceAll(" ", "");
						BaseContext baseContext = new BaseContext(mContext);
						String wakeupWord = baseContext.getGlobalString(KeyList.STRING_WAKEUP_WORD, "");
						if (result == null || result.equals("")) {
							result = wakeupWord + "$Error";
						}
						String path = stopRecord(true, "AISpeech_" + result);
						baseContext.setGlobalString(KeyList.PKEY_STRING_SPEECH_ONLINE_FILE_PATH, path);
						baseContext.setGlobalLong(KeyList.PKEY_STRING_SPEECH_ONLINE_TIME, ts);
						LogManager.d(TAG,"callback result  SpeechBufferOnLineRecognizer end use time:" + ts + "===>>result:" + result);
						if (mStateListener != null) {
							mStateListener.onResult("0," + result);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						LogManager.printStackTrace(e);
					}

				}
			}

		}

		@Override
		public void onError(AIError aiError) {
			// TODO Auto-generated method stub
			LogManager.e(TAG, "onError----在线语音识别结果:" + aiError.getErrId());
			ts = System.currentTimeMillis() - ts;
			BaseContext baseContext = new BaseContext(mContext);
			String wakeupWord = baseContext.getGlobalString(KeyList.STRING_WAKEUP_WORD, "");
			// 发送误唤醒统计
			baseContext.sendUmengEvent(UmengUtil.ERROR_WAKEUP, UmengUtil.ERROR_WAKEUP_CONTENT);
			String path = stopRecord(false, "AISpeech_" + wakeupWord + "$Error");
			baseContext.setGlobalString(KeyList.PKEY_STRING_SPEECH_ONLINE_FILE_PATH, path);
			baseContext.setGlobalLong(KeyList.PKEY_STRING_SPEECH_ONLINE_TIME, ts);
			if (aiError != null && mStateListener != null) {
				System.out.println("aiError is " + aiError);
				mStateListener.onError(mappingError(aiError));

			}
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
		public void onInit(int status) {
			LogManager.d(TAG, "onInit----Speech buffer onLine Init result:" + status);
			mUSCStateListener.onLoadComplete(SpeechBufferOnLineRecognizer.this);
		}

		@Override
		public void onRecordReleased() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onReadyForSpeech() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onBufferReceived(byte[] arg0) {
			// TODO Auto-generated method stub
		}

	};

}
