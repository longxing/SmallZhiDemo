package com.iii360.base.inf;

import java.util.HashMap;
import java.util.Map;

import com.iii360.base.inf.recognise.IRecogniseSystem;

public interface ITTSController {
	public interface ITTSStateListener {
		/**
		 * 初始化的时候调用
		 */
		public void onInit();

		/**
		 * 开始播放前调用
		 */
		public void onStart();

		/**
		 * 结束播放调用
		 */
		public void onEnd();

		/**
		 * 错误的时候调用
		 */
		public void onError();
	}

	public void setListener(ITTSStateListener ttsStateListener);

	public void play(String text);
	
	public void play(String ...strings);
	
	public void syncPlay(String text);

	public void stop();

	public void destroy();

	public void setRecSystem(IRecogniseSystem recogniseSystem);

	public void setType(int type);
	public void playMore(String ...text);
}
