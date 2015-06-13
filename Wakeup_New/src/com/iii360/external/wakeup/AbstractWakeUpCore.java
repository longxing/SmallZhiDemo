package com.iii360.external.wakeup;


public abstract class AbstractWakeUpCore {
	
	
	/**
	 * @brief 提供给外部调用者的 关于WakeupCore的回调接口
	 * 
	 */	
	public static interface ISpeechSensitive 
	{
		public static final int EVENT_NONE_EVNET = 0;
		public static final int EVENT_WAKE_UP = 2;

		// 接收到"小智"关键字 系统被唤醒回调函数,  Listen-->WakenUp状态切换
		// 注意: 这个回调函数在 思必驰引擎 的回调线程中
		public void onStart();

		// 结束语音录入的回调函数, WakenUp-->Stopped 状态切换
		// 注意: 这个回调函数在内部缓冲区管理线程中
		public void onStop();

		// 音频PCM数据到到达回调
		// 注意: 这个回调函数在内部缓冲区管理线程中
		public void onBufferReceived(byte[] buffer);

		// 事件回调函数
		// 注意: 这个回调函数在内部缓冲区管理线程中
		public void onEvent(int event);

	}
	
	
	/**
	 * @brief 提供给外部调用者的  打开、关闭主界面识别图标
	 * 
	 */	
	public static interface SendShowOrHiddenFlagInterface 
	{
		// 显示
		public void onShow();

		// 关闭
		public void onHidden();
	}
	

	protected abstract void stopWakup();

	protected abstract void offerSilenceBuffer(byte[] buffer);
	
	protected abstract void setSpeechSensitive(ISpeechSensitive speechSensitive, SendShowOrHiddenFlagInterface sendShowOrHiddenFlagInterface);
	
	public abstract void stop();
	
	public abstract void destroy();
}
