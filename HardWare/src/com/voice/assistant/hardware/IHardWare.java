package com.voice.assistant.hardware;

import java.util.HashMap;
import java.util.Map;

public interface IHardWare {
	// 按键亮度
	/**
	 * 全亮
	 */
	static final int LIGHT_ON = 10;
	/**
	 * 亮灯
	 */
	static final int LIGHT_OFF = 3;
	/**
	 * 灭
	 */
	static final int LIGHT_CLOSE = 0;

	public static String LIGHT_WAKE_UP = "LIGHT_WAKE_UP";
	public static String LIGHT_NET = "LIGHT_NET";
	public static String LIGHT_LOGO = "LIGHT_LOGO";

	public static String BUTTON_LOGO = "BUTTON_LOGO";
	public static String BUTTON_VOLUME_INCREASE = "BUTTON_VOLUME_INCREASE";
	public static String BUTTON_VOLUME_DECREASE = "BUTTON_VOLUME_DECREASE";
	public static String BUTTON_RESET = "BUTTON_RESET";

	Map<String, ButtonHandler> buttonHandlers = new HashMap<String, ButtonHandler>();
	static Map<String, Integer> NameMap = new HashMap<String, Integer>();

	static Map<Integer, String> ButtonNameMap = new HashMap<Integer, String>();

	public void regestOnClickListen(String buttonName, ButtonHandler handler);

	public void restore();
	
	public void onClick(String buttonName);

	public void controlLight(String LightName, int brightness);

	public void destory();
}
