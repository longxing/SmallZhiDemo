package com.smallzhi.TTS.Engine;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SpeechUtility;
import com.iflytek.speech.SynthesizerListener;
import com.iii360.base.common.utl.LogManager;
import com.smallzhi.TTS.Main.TTSSameple;

/**
 * 讯飞离线语音TTS播报
 * 
 * @author Peter
 * @data 2015年4月15日上午10:22:18
 */
public class TTSXunFeiLocal implements ITTSPlayer {

	/************************************* Members Variables ******************************************/

	private static String TAG = "TTSXunFeiLocal";
	private Context mContext = null;
	private String mTempText = null;
	private SpeechSynthesizer mTts = null; //离线语音合成
	private ITTSStatusListen mListen  = null;
	private TTSCustom mCustomtts;
	private static final String TYPES[] = { "xiaoyan", "xiaofeng", "nannan", "xiaojing", "xiaomei", "xiaoqian" };

	/**
	 * 初始化设置
	 * 
	 * @param context
	 */
	public TTSXunFeiLocal(Context context) {
		mContext = context;
		SpeechUtility.getUtility(mContext).setAppid("j62m54c0");
		// 初始化合成对象
		mTts = new SpeechSynthesizer(mContext, mTtsInitListener);
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, "local");
		mTts.setParameter(SpeechSynthesizer.VOICE_NAME, TYPES[0]);
		mTts.setParameter(SpeechSynthesizer.SPEED, "50");
		mTts.setParameter(SpeechSynthesizer.PITCH, "50");
		mCustomtts = new TTSCustom(mContext);
	}

	@Override
	public void play(String text) {
		if (mCustomtts != null && mCustomtts.isContain(text)) {
			mTts.stopSpeaking(mTtsListener);
			mCustomtts.play(text);
		} else {
			mTts.setParameter(SpeechSynthesizer.STREAM_TYPE, String.valueOf(TTSSameple.CURRENT_STREAM_TYPE));

			int code = mTts.startSpeaking(text, mTtsListener);
			LogManager.e("xunfei error" + code);
			if (code != 0) {
				mTempText = text;
			} else {
				if (mListen != null) {
					mListen.onBegin();
				}
			}
		}
	}

	@Override
	public void stop() {
		mTts.stopSpeaking(mTtsListener);
		if (mListen != null) {
			mListen.onEnd();
		}
		if (mCustomtts != null) {
			mCustomtts.stop();
		}
	}

	@Override
	public void setListen(ITTSStatusListen listen) {
		mListen = listen;
		if (mCustomtts != null) {
			mCustomtts.setListen(listen);
		}
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVoiceType(int type) {
		// TODO Auto-generated method stub
		if (type < TYPES.length) {
			mTts.setParameter(SpeechSynthesizer.VOICE_NAME, TYPES[type]);
		}
	}

	@Override
	public void setVoiceSpeed(int speed) {
		if (speed == 0) {
			speed = 1;
		}
		mTts.setParameter(SpeechSynthesizer.SPEED, String.valueOf(speed));
	}

	@Override
	public boolean setStreamType(int type) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 初期化监听。
	 */
	private InitListener mTtsInitListener = new InitListener() {

		@Override
		public void onInit(ISpeechModule arg0, int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code == ErrorCode.SUCCESS) {
				if (mTempText != null) {
					play(mTempText);
					mTempText = null;
				}
			}
		}
	};

	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener.Stub() {
		@Override
		public void onBufferProgress(int progress) throws RemoteException {
			// Log.d(TAG, "onBufferProgress :" + progress);
			if (mListen != null) {
				// mPlayListener.on

			}
		}

		@Override
		public void onCompleted(int code) throws RemoteException {
			// Log.d(TAG, "onCompleted code =" + code);

			if (mListen != null) {
				mListen.onEnd();
			}
		}

		@Override
		public void onSpeakBegin() throws RemoteException {
			if (mListen != null) {
				mListen.onBegin();
			}
		}

		@Override
		public void onSpeakPaused() throws RemoteException {
			// Log.d(TAG, "onSpeakPaused.");
			if (mListen != null) {
				mListen.onEnd();
			}
		}

		@Override
		public void onSpeakProgress(int progress) throws RemoteException {
			if (mListen != null) {
				// mPlayListener.on
			}
		}

		@Override
		public void onSpeakResumed() throws RemoteException {
			// Log.d(TAG, "onSpeakResumed.");
			if (mListen != null) {
				mListen.onBegin();
			}
		}
	};

}
