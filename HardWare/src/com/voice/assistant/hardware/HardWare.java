package com.voice.assistant.hardware;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.os.ILedsService;
import android.os.RemoteException;
import android.os.ServiceManager;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.voice.assistant.hardware.ButtonHandler.TouchStatus;

public class HardWare implements IHardWare {

	private String lastButtonName = "";

	static {
		NameMap.put(LIGHT_LOGO, 0);
		NameMap.put(LIGHT_WAKE_UP, 1);
		NameMap.put(LIGHT_NET, 2);
	}

	static {
		ButtonNameMap.put(1, BUTTON_LOGO);
		ButtonNameMap.put(3, BUTTON_VOLUME_INCREASE);
		ButtonNameMap.put(2, BUTTON_VOLUME_DECREASE);
		ButtonNameMap.put(4, BUTTON_RESET);
		ButtonNameMap.put(5, "NULL");
	}

	private Context mContext;
	private BaseContext mBaseContext;
	private Map<String, ButtonHandler> storeButtonHandlers;

	public HardWare(Context context) {
		mContext = context;
		mBaseContext = new BaseContext(mContext);
	}

	public void regestOnClickListen(String buttonName, ButtonHandler handler) {
		buttonHandlers.put(buttonName, handler);
	}

	@Override
	public void restore() {// 存
		storeButtonHandlers = new HashMap(buttonHandlers);
	}

	public void recover() {
		for (Entry<String, ButtonHandler> entry : storeButtonHandlers.entrySet()) {
			entry.getValue().prepare();
			buttonHandlers.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void onClick(String buttonName) {
		// TODO Auto-generated method stub
		LogManager.d(buttonName);
		if (lastButtonName.equals(buttonName)) {
			if (buttonHandlers.containsKey(buttonName)) {
				buttonHandlers.get(buttonName).onStatusClick(TouchStatus.TOUCH_ED);
			}
		} else {
			if (buttonHandlers.containsKey(lastButtonName)) {
				buttonHandlers.get(lastButtonName).onStatusClick(TouchStatus.TOUCH_END);
			}
			if (buttonHandlers.containsKey(buttonName)) {
				buttonHandlers.get(buttonName).onStatusClick(TouchStatus.TOUCH_BEGIN);
			}
		}

		lastButtonName = buttonName;
	}

	@Override
	public void controlLight(String LightName, int brightness) {
		ILedsService mledsev = ILedsService.Stub.asInterface(ServiceManager.getService("leds"));
		try {
			mledsev.setLedsBrightness(NameMap.get(LightName), 0, brightness);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destory() {

	}

}
