package com.iii360.voiceassistant.semanteme.command;

import android.content.Context;

import com.base.data.CommandInfo;
import com.iii360.base.inf.BasicServiceUnion;
import com.voice.assistant.main.TTSControllerProxy;

public class CommandQueryWeatherExternal extends CommandQueryWeather {
	private Context mContext;
	private TTSControllerProxy mTTSControllerProxy;

	public CommandQueryWeatherExternal(BasicServiceUnion union, CommandInfo commandInfo) {
		super(union, commandInfo, "播报天气");
	}

	@Override
	protected void sendAnswerSession(String text) {
		if (mTTSControllerProxy == null) {
			mTTSControllerProxy = new TTSControllerProxy(mContext);
		}
		mTTSControllerProxy.play(text);
	}
}
