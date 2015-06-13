package com.iii360.external.recognise.engine;


public interface IRecogniseEngine {
	
    public final static int FEED_BACK_TYPE_NONE = 0 ;
    
    public final static int FEED_BACK_TYPE_MUSIC = 1;
    
    public final static int FEED_BACK_TYPE_VIBRATE = 2 ;
    
	public static interface IRecogniseListenrAdapter {
		 /**
		  * 音量回调转接口
		  * @param level
		  */
		  public void onRmsChanged(int level);
		  /**
		   * 结果回调转接口
		   * @param results
		   */
		  public void onResults(String results) ;
		  /**
		   * 音频数据回传回调转接口
		   * @param buffer
		   */
		  public void onBufferReceived(byte[] buffer);
		  /**
		   * 错误回调转接口
		   * @param error
		   */
		  public void onError(int error);
		  /**
		   * 说话声音结束回调转接口,这里的操作可能有播放暂停读取麦克风时候的声音
		   */
		  public void onEndOfSpeech();
		  /**
		   * 用户点击初始化在这里
		   */
		  public void onInit();
		  /**
		   * 一次完整的识别过程结束
		   */
		  public void onEnd();
		  /**
		   * 识别开始之前状态
		   */
		  public void onBeforeInit();
	}
	/**
	 * 开始录音之前反馈
	 */
	public void startCaptureVoiceFeedBack();
	/**
	 * 停止录音后的反馈
	 */
	public void startRecogniseFeedBack();
	/**
	 * 取消识别的反馈
	 */
	public void cancelRecogniseFeedBack();
	/**
	 * 捕捉麦克风数据
	 */
	public void start();
	/**
	 * 停止捕捉麦克风数据
	 */
	public void stop();
	/**
	 * 取消识别
	 */
	public void cancel();
	/**
	 * 设置识别回调函数
	 * @param listener
	 */
	public void setRecognitionAdapter(IRecogniseListenrAdapter listener);
	/**
	 * 资源释放
	 */
	public void destroy() ;
	
}
