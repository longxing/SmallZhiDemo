package com.voice.common.util.nlp;

import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;

public class AnswerOperite {
	private BasicServiceUnion mUnion;
	private SentenceObject mObject;
	private String text;
	public String command;
	// 是否执行后要播报结果
	private boolean isNeedTTS = true;

	public AnswerOperite(SentenceObject s, BasicServiceUnion union) {
		mUnion = union;
		mObject = s;
		command = mObject.getClearContent();
	}

	public void excute() {
		LogManager.printStackTrace();
		if (mUnion.getControlInterface() != null) {
			LogManager.e(mObject.getClearContent());
			text = mUnion.getControlInterface().sendMsg(mObject.getOrder(), mObject.getTarget());
		} else {
			text = "请稍等，正在连接设备中";
		}
		if (text == null || text.length() < 1) {
			text = "好的，";
		}
		if (isNeedTTS) {
			mUnion.getMainThreadUtil().sendNormalWidget(text);
		}
	}

	public void setNeedTTS(boolean isNeedTTS) {
		this.isNeedTTS = isNeedTTS;
	}
	
	
}
