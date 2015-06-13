package com.base.platform;

import android.content.Context;

/**
 * @author Taurus @
 * 
 */
public interface IPlatform {

	public static final String SESSION_TYPE_NATIVE = "0";

	public static final String SESSION_TYPE_REMOTE = "1";

	/** Network operation timed out. */
	public static final int DATA_ERROR_NETWORK_TIMEOUT = 1;

	/** Other network related errors. */
	public static final int DATA_ERROR_NETWORK = 2;

	/** No match command input */
	public static final int DATA_ERROR_NO_MATCH = 3;

	/** Server sends error status. */
	public static final int DATA_ERROR_SERVER = 4;

	/** Other client side errors. */
	public static final int DATA_ERROR_CLIENT = 5;

	/** voice recognise errors. */
	public static final int DATA_ERROR_VOICE_RECOGNISE = 6;

	/**
	 * @return 当前设置的数据监听器
	 */
	public OnDataReceivedListener getOnDataReceivedListener();

	//
	/**
	 * 设置数据监听器
	 * 
	 * @param OnDataReceivedListener
	 *            从用户端传入的监听器
	 * @description:设置数据接收监听
	 */
	public void setOnDataReceivedListener(OnDataReceivedListener l);

	/**
	 * Only for VoiceAssistant 360
	 */
	public void setAdditionalParams(String params);

	/**
	 * Only for VoiceAssistant 360
	 */
	public void sendRemoteSession(String text);

	/**
	 * Only for VoiceAssistant 360
	 */
	public void sendRemoteSession(String text, String params);

	/**
	 * 发送会话
	 * 
	 * @param text
	 *            待解析的文本
	 */
	public void sendSession(String text);

	/**
	 * 发送会话
	 * 
	 * @param text
	 *            待解析的文本
	 * @param params
	 *            p1=v1&p2=v2... support: app_id,robot_id
	 * 
	 */
	public void sendSession(String text, String params);

}
