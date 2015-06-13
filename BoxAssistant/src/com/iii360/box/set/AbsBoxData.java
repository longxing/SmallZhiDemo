package com.iii360.box.set;

import android.content.Context;

public abstract class AbsBoxData {
	protected Context context;

	public AbsBoxData(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public abstract void sendTtsPeople(String voiceMan);

	public abstract void sendLedSwitch(String ledSwtich);

	public abstract void sendLedTime(String ledTime, boolean isOpen);

	public abstract void sendWeatherSwitch(String weatherSwitch);

	public abstract void sendWeatherTime(String weatherTime, boolean isOpen);

}
