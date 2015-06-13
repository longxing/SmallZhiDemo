package com.voice.upgrade.util;

import android.content.Context;

import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.inf.ITTSController.ITTSStateListener;
import com.smallzhi.TTS.Engine.TTSPlayerFactory;
import com.smallzhi.TTS.Main.TTSSameple;

public class TTSUtil {

	private ITTSController mRealTTSController;
	private ITTSStateListener mTTSStateListener;
	
	private Context context;
	
	private static TTSUtil self = null;
	
	public static TTSUtil getInstance(Context context) {
		if (self == null) {
			self = new TTSUtil(context);
		}
		return self;
	}
	
	private TTSUtil(Context context) {
		this.context = context;
		this.initTTSController();
	}
	
	private void initTTSController() {
		TTSSameple t = null;
		try {
			TTSSameple.initContext(context);
			int role = 0;
			int speed = 50;
			t = new TTSSameple(TTSPlayerFactory.TYPE_XUNFEI, role, speed);
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}

		if (t != null) {
			mRealTTSController = t;
		}

	}

	public void setTTSStateListener(ITTSStateListener mTTSStateListener) {
		this.mTTSStateListener = mTTSStateListener;
		this.mRealTTSController.setListener(mTTSStateListener);
	}
	
	public void play(String text) {
		LogManager.e(text);
		this.mRealTTSController.play(text);
	}
}
