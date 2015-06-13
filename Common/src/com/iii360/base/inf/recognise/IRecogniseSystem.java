package com.iii360.base.inf.recognise;


/**
 * 可以理解为语音识别系统的抽象
 * 
 * @author HuJinrong
 * 
 */
public interface IRecogniseSystem {
	/**
	 * 无状态
	 */
	public static int STATE_NORMAL = 0;
	/**
	 * 开始处于监听麦克风状态
	 */
	public static int STATE_STARTED_CAPTURE_VOICE = 1;
	/**
	 * 停止监听麦克风进入开始识别状态
	 */
	public static int STATE_STOP_CAPTURE_AND_START_RECOGNING = 2;

	// //////////////////////////////////////////////////////////
	/**
	 * 默认的错误代码。对应我没听清
	 */
	/*
	 *  识别错误代号
	 */
	public static final int ERROR_NETWORK = 0;// 网络错误
	public static final int ERROR_ICANNOT_HEAR = -1;// 环境错误
	/**
	 * 错误代码，网络错误。
	 */
	public static int ERROR_NET_WORK_ERROR = 0;

	public static int ERROR_IFLYTEK_NO_INSTALL_ERROR = -2;

	/**
	 * 语音识别回调接口
	 * 
	 * @author Jerome.Hu
	 * 
	 */
	public static interface IOnResultListener {
		/**
		 * 识别开始回调，可以在里面进行一些其他操作。
		 */
		public void onStart();

		/**
		 * 得到结果时候回调。
		 * 
		 * @param text
		 *            识别引擎结果
		 */
		public void onResult(String text);

		/**
		 * 识别出错的时候回调。
		 * 
		 * @param errorCode
		 *            错误代码
		 */
		public void onError(int errorCode);

		/**
		 * 结束回调
		 */
		public void onEnd();
	}

	/**
	 * 开始捕捉音频数据
	 */
	public void startCaptureVoice();

	/**
	 * 停止捕捉音频数据
	 */
	public void stopCaptureVoice();

	/**
	 * 停止识别
	 */
	public void cancelRecognising();

	/**
	 * 资源释放
	 */
	public void destroy();

	/**
	 * 
	 * @param onResultListener
	 *            回调监听。
	 */
	public void setOnResultListener(IOnResultListener onResultListener);

	/**
	 * 
	 * @param recogniseButton
	 * @param onTouchListener
	 *            若之前有对View进行过事件监听则传入进来。对之前的事件处理无影响。若没有则传入空
	 * 
	 */
	public void bindRecogniseButton(ILightController recogniseButton);

	public void dispatchUserAction();

	public void startWakeup();

	public void stopWakeup();
	
}
