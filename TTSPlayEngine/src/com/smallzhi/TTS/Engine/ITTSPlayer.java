package com.smallzhi.TTS.Engine;
/**
 * TTS播报的接口
 * @author Peter
 * @data 2015年4月15日上午10:19:43
 */
public interface ITTSPlayer {
	/**
	 * 播放文本,同时会停止正在播报的内容
	 * 
	 * @param text
	 */
	public void play(String text);

	/**
	 * 停止播报的问题.
	 */
	public void stop();

	/**
	 * 设置播报状态
	 * 
	 * @param listen
	 */
	public void setListen(ITTSStatusListen listen);

	/**
	 * 设置声音类型
	 * 
	 * @param type
	 */
	public void setVoiceType(int type);

	/**
	 * 设置语速
	 * 
	 * @param speed
	 */
	public void setVoiceSpeed(int speed);

	/**
	 * 释放资源
	 */
	public void release();
    /**
     * 设置音频类型
     * @param type
     * @return
     */
	public boolean setStreamType(int type);

}
