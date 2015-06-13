package com.iii360.external.recognise.engine;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.export.engines.AICloudASREngine;
import com.aispeech.export.listeners.AIASRListener;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.recognise.IRecogniseSystem;
import com.iii360.base.umeng.UmengUtil;
import com.iii360.external.recognise.util.RecordUpLoadHandler;
import com.iii360.sup.common.utl.SupKeyList;

/**
 * 主要用于按钮长按识别逻辑 设置语音最长录音时间为：10s; 通过 AIASRListener 监听接口以及IRecogniseListenrAdapter 接口将结果回调给 RecogniseSystem
 * 
 * @author Peter
 * @data 2015年4月3日下午6:41:18
 */
public class SpeechOnLineRecognizer extends IRecogniseBaseEngine {

	private static final String TAG = "SpeechOnLineRecognizer";

	public static final String APPKEY = "1362108422000003";
	public static final String SECRETKEY = "000bc2f69fc8a3d9ae21834df94abcd9";
	private IRecogniseListenrAdapter mAdapter;
	private AICloudASREngine mEngine;
	private Context mContext;
	private RecordUpLoadHandler mRecordUpLoadHandler = new RecordUpLoadHandler();
	private AIASRListener mSpeechListener;
	private long ts = 0;
	private Handler handler = new Handler();

	public SpeechOnLineRecognizer(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	private void initEngine() {
		mSpeechListener = new AIASRListener() {

			@Override
			public void onBeginningOfSpeech() {
				LogManager.i(TAG, "onBeginningOfSpeech");
				handler.postDelayed(runnable, 1000 * 10);
			}

			@Override
			public void onEndOfSpeech() {
				// TODO Auto-generated method stub
				LogManager.i(TAG, "onEndOfSpeech");
				removeDelayRunable();

			}

			@Override
			public void onError(AIError aiError) {
				// TODO Auto-generated method stub
				LogManager.i(TAG, "onError");
				removeDelayRunable();
				BaseContext baseContext = new BaseContext(mContext);
				baseContext.setGlobalBoolean(KeyList.PKEY_STRING_SPEECH_LOGO_IS_START, false);
				mAdapter.onError(mappingError(aiError));
				mRecordUpLoadHandler.stopRecordToSaveFile(false, "AISpeech_$Error");
				baseContext.setGlobalBoolean(KeyList.GKEY_IS_NOW_RECOGNING, false);
				// 设置识别引擎返回识别结果用时
				ts = System.currentTimeMillis() - ts;
				baseContext.setGlobalLong(SupKeyList.PKEY_STRING_SPEECH_ONLINE_TIME_FOR_LONGCLICK, ts);
				baseContext.sendUmengEvent(UmengUtil.ERROR_WAKEUP, UmengUtil.ERROR_WAKEUP_CONTENT);
			}

			@Override
			public void onInit(int arg0) {
				// TODO Auto-generated method stub
				LogManager.i(TAG, "onInit");
			}

			@Override
			public void onReadyForSpeech() {
				// TODO Auto-generated method stub
				LogManager.i(TAG, "onReadyForSpeech");
				BaseContext baseContext = new BaseContext(mContext);
				baseContext.setGlobalBoolean(KeyList.GKEY_IS_WAKEUP_TO_RECOGNISE, false);
			}

			@Override
			public void onResults(AIResult aiResult) {
				// TODO Auto-generated method stub
				LogManager.i(TAG, "onResults");
				BaseContext baseContext = new BaseContext(mContext);
				// 设置识别引擎返回识别结果用时
				ts = System.currentTimeMillis() - ts;
				baseContext.setGlobalLong(SupKeyList.PKEY_STRING_SPEECH_ONLINE_TIME_FOR_LONGCLICK, ts);
				LogManager.i(TAG, "callback result use time:" + ts);

				baseContext.setGlobalBoolean(KeyList.PKEY_STRING_SPEECH_LOGO_IS_START, false);
				if (aiResult.isLast()) {
					if (aiResult.getResultType() == AIConstant.AIENGINE_MESSAGE_TYPE_JSON) {
						try {
							JSONObject jsonResult = new JSONObject((String) aiResult.getResultObject()).getJSONObject("result");
							String result = jsonResult.getString("rec");
							result = result == null ? null : result.replaceAll(" ", "");
							if (result == null || result.equals("")) {
								result = "$Error";
							}
							mAdapter.onResults(result);
							mRecordUpLoadHandler.stopRecordToSaveFile(true, "AISpeech_" + result);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							LogManager.printStackTrace(e);
						}

					}
				}
			}

			@Override
			public void onRmsChanged(float voiceLevel) {
				// TODO Auto-generated method stub
				LogManager.i(TAG, "onRmsChanged----rmsdb is " + voiceLevel);
				mAdapter.onRmsChanged(mappingVoiceLevel(voiceLevel));
			}

			@Override
			public void onRecordReleased() {
				// TODO Auto-generated method stub
				LogManager.i(TAG, "onRecordReleased");
				KeyList.VOICE_RECOGNIZER_BEGIN = System.currentTimeMillis();
				mAdapter.onEndOfSpeech();
				BaseContext baseContext = new BaseContext(mContext);
				baseContext.setGlobalBoolean(KeyList.GKEY_IS_NOW_RECOGNING, false);
			}

			@Override
			public void onBufferReceived(byte[] buffer) {
				// TODO Auto-generated method stub
				LogManager.i(TAG, "onBufferReceived----");
				mRecordUpLoadHandler.record(buffer);
			}

		};
		mEngine = AICloudASREngine.createInstance();
		mEngine.setVadEnable(true);
		mEngine.setVadResource("vad.0.10");
		mEngine.setRes("comm");// 加速家电命令
//		mEngine.setRes("home"); //现在远场识别需要设置的资源
		// mEngine.setCustom("zn360");// 模型资源
		mEngine.init(mContext, mSpeechListener, SpeechOnLineRecognizer.APPKEY, SpeechOnLineRecognizer.SECRETKEY);
	}

	@Override
	public void setRecognitionAdapter(IRecogniseListenrAdapter listener) {
		// TODO Auto-generated method stub
		mAdapter = listener;
		LogManager.i(TAG, "setRecognitionAdapter-----");
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		LogManager.i(TAG, "destroy-----");

	}

	@Override
	protected void realStart() {
		LogManager.i(TAG, "realStart-----");
		mRecordUpLoadHandler.beforeStartRecord();
		mAdapter.onInit();
		mEngine.setNBest(0);
		mEngine.setUseCustomFeed(false);
		mEngine.setVolEnable(true);
		mEngine.setNoSpeechTimeOut(10 * 1000);
		mEngine.start();
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		LogManager.i(TAG, "start----");
		BaseContext baseContext = new BaseContext(mContext);
		baseContext.setGlobalBoolean(KeyList.GKEY_IS_NOW_RECOGNING, true);
		initEngine();
		mAdapter.onBeforeInit();
	}

	@Override
	public void stop() {
		LogManager.i(TAG, "stop recording------");
		// 在线语音Debug播报
		if (KeyList.IS_TTS_DEBUG) {
			KeyList.VOICE_RECOGNIZER_BEGIN = System.currentTimeMillis();
		}
		// 记录语音识别的开始时间
		ts = System.currentTimeMillis();
		BaseContext baseContext = new BaseContext(mContext);
		baseContext.setGlobalBoolean(KeyList.GKEY_IS_NOW_RECOGNING, false);
		mEngine.stopRecording();
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		LogManager.i(TAG, "cancel");
		mAdapter.onEnd();
		if (mEngine != null) {
			mEngine.cancel();
		}
		BaseContext baseContext = new BaseContext(mContext);
		baseContext.setGlobalBoolean(KeyList.GKEY_IS_NOW_RECOGNING, false);
	}

	private int mappingError(AIError aiError) {
		switch (aiError.getErrId()) {
		case 70200:
			return IRecogniseSystem.ERROR_NETWORK;
		default:
			return IRecogniseSystem.ERROR_ICANNOT_HEAR;
		}
	}

	/**
	 * 设置最长录音时长为10s;
	 */
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			LogManager.i(TAG, "speech  call back timeout!");
			stop();
		}

	};

	/**
	 * 移除语音识别定时器
	 */
	private void removeDelayRunable() {
		if (handler != null) {
			handler.removeCallbacks(runnable);
		}
	}

	private int mappingVoiceLevel(float voiceLevel) {
		int level = 0;
		if (voiceLevel > 0) {
			voiceLevel = (float) (15 * Math.log10(voiceLevel) + 75);
		} else if (voiceLevel < 0) {
			voiceLevel = (float) (75 - 15 * Math.log10(-voiceLevel));
		}
		if (voiceLevel < 75) {
			level = 0;
		} else if (voiceLevel >= 75 && voiceLevel < 80) {
			level = 1;
		} else if (voiceLevel >= 80 && voiceLevel < 84) {
			level = 2;
		} else if (voiceLevel >= 84 && voiceLevel < 86) {
			level = 3;
		} else if (voiceLevel >= 88 && voiceLevel < 90) {
			level = 4;
		} else if (voiceLevel >= 92 && voiceLevel < 94) {
			level = 5;
		} else if (voiceLevel >= 94 && voiceLevel < 96) {
			level = 6;
		} else if (voiceLevel >= 96 && voiceLevel < 98) {
			level = 7;
		} else if (voiceLevel >= 98 && voiceLevel < 100) {
			level = 8;
		}
		return level;
	}

}
