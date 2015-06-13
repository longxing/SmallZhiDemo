package com.iii360.voiceassistant.Bean.HardWare;

import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.recognise.ILightController;
import com.voice.assistant.hardware.WakeUpLightControl;

/**
 * 灯状态接口的实现
 * 
 * @author Peter
 * @data 2015年4月24日下午8:24:25
 */

public class LightControllerImp implements ILightController {

	private static final String TAG = "LightControllerImp";

	private WakeUpLightControl LightController = null;

	public LightControllerImp(WakeUpLightControl lightController) {
		this.LightController = lightController;
	}

	@Override
	public void updateState(int state, Object params) {
		LogManager.d(TAG, "updateState light:state=" + state + "=======params:" + params);
		LightController.updateStateOnRunnable(state, params);
	}

	@Override
	public int getState() {
		LogManager.d(TAG, "getState light:state= 0");
		return 0;
	}

	@Override
	public void updateMode(int mode) {
		LogManager.d(TAG, "========>updateMode");
	}

	@Override
	public int getMode() {
		LogManager.d(TAG, "========>getMode");
		return 0;
	}

	@Override
	public void reconiseStartAnimation() {
		LogManager.d(TAG, "========>reconiseStartAnimation");
		LightController.reconiseStartAnimation();

	}

	@Override
	public void reconiseStopAnimation() {
		LogManager.d(TAG, "========>reconiseStopAnimation");
		LightController.reconiseStopAnimation();
	}

	@Override
	public void updateStateOnRunnable(int state, Object params) {
		LogManager.d(TAG, "updateStateOnRunnable light:state=" + state + "=======params:" + params);
		LightController.updateStateOnRunnable(state, params);
	}

}
