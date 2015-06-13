package com.smallzhi.TTS.Engine;

import android.content.Context;
import android.os.RemoteException;
import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SpeechUtility;
import com.iflytek.speech.SynthesizerListener;
import com.smallzhi.TTS.Main.TTSSameple;

/***
 * 讯飞在线语音合成引擎。TTS在线 暂时不用
 * 
 * @author Peter
 * @data 2015年4月15日下午1:44:32
 */
public class TTSXunFeiNet implements ITTSPlayer {

	private Context mContext;
	private ITTSStatusListen mListen;
	private String[] mName = { "vinn", "xiaoyu", "xiaoyan", "vixx", "vils", "vixm", "vixl", "vixr", "vixyun", "vixk", "vixqa", "vixying" };
	private String mTempText = "";
	private SpeechSynthesizer mTts = null; // 语音合成

	private InitListener mTtsInitListener = new InitListener() {

		@Override
		public void onInit(ISpeechModule arg0, int code) {
			if (code == ErrorCode.SUCCESS) {
			}
		}
	};
	// * 合成回调监听。

	private SynthesizerListener mTtsListener = new SynthesizerListener.Stub() {
		@Override
		public void onBufferProgress(int progress) throws RemoteException {
			if (mListen != null) {
			}
		}

		@Override
		public void onCompleted(int code) throws RemoteException {
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
			if (mListen != null) {
				mListen.onEnd();
			}
		}

		@Override
		public void onSpeakProgress(int progress) throws RemoteException {
			if (mListen != null) {
			}
		}

		@Override
		public void onSpeakResumed() throws RemoteException {
			if (mListen != null) {
				mListen.onBegin();
			}
		}
	};

	public TTSXunFeiNet(Context context) {
		mContext = context;
		SpeechUtility.getUtility(mContext).setAppid("j62m54c0");
		// 初始化合成对象
		mTts = new SpeechSynthesizer(mContext, mTtsInitListener);
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
		mTts.setParameter(SpeechSynthesizer.SPEED, "50");
		mTts.setParameter(SpeechSynthesizer.PITCH, "50");
		mTts.setParameter(SpeechSynthesizer.VOLUME, "100");
	}

	@Override
	public void play(String text) {
		mTts.setParameter(SpeechSynthesizer.STREAM_TYPE, String.valueOf(TTSSameple.CURRENT_STREAM_TYPE));
		int code = mTts.startSpeaking(text, mTtsListener);
		if (code != 0) {
			mListen.onError();
		} else {
			// showTip("start speak success.");
		}

	}

	@Override
	public void stop() {
		mTts.stopSpeaking(mTtsListener);
		if (mListen != null) {
			mListen.onEnd();
		}
	}

	@Override
	public void setListen(ITTSStatusListen listen) {
		mListen = listen;
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		mTts.stopSpeaking(mTtsListener);
		mTts.destory();
	}

	@Override
	public void setVoiceType(int type) {
		// TODO Auto-generated method stub
		mTts.setParameter(SpeechSynthesizer.VOICE_NAME, mName[type]);
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
		mTts.setParameter(SpeechSynthesizer.STREAM_TYPE, String.valueOf(type));
		return true;
	}

}
