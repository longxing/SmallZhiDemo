package com.iii360.base.inf.recognise;

/**
 * 灯状态控制器
 * 
 * @author Peter
 * @data 2015年4月23日下午5:29:18
 */

public interface ILightController {

	// 模式
	public static final int MODE_NORMAL = 0;
	public static final int MODE_WARN = 1;
	public static final int MODE_CONFIGING_THRIDPART = 2;
	public static final int MODE_HURRY = 3;

	// 标准状态

	public static final int RECOGNISE_STATE_CLOSE = 0;
	public static final int RECOGNISE_STATE_OPEN = 1;

	// 唤醒状态
	public static final int RECOGNISE_STATE_INIT = 2;
	public static final int RECOGNISE_STATE_VOICE_LEVEL_CHANGE = 3;
	public static final int RECOGNISE_STATE_NORMAL = 4;

	// 识别状态
	public static final int RECOGNISE_STATE_RECONISING = 5;
	public static final int RECOGNISE_STATE_ERROR = 6;
	public static final int RECOGNISE_STATE_SUCCESS = 7;
	/**
	 * 更新状态
	 * 
	 * @param state
	 *            状态标示
	 * @param params
	 *            与状态相关的参数
	 * @param callback
	 *            更新完状态之后
	 */
	public abstract void updateState(int state, Object params);

	public int getState();

	public void updateMode(int mode);

	public int getMode();

	public void reconiseStartAnimation();

	public void reconiseStopAnimation();

	public abstract void updateStateOnRunnable(int state, Object params);

}
