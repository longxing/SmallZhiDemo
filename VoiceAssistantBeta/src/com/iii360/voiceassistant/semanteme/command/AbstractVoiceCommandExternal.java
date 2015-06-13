package com.iii360.voiceassistant.semanteme.command;

import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.inf.parse.ICommandEngine;
import com.iii360.base.inf.parse.IVoiceCommand;
import com.iii360.sup.common.utl.UploadVoiceLogToServer;

/**
 * 适用于需要离线播报，或者在线语音识别
 * @author ldear
 *
 */
public abstract class AbstractVoiceCommandExternal implements IVoiceCommand {

	ITTSController mTTSControllerProxy;
	ICommandEngine mCommandEngineExternal;
	private String mCommandDesc;
	private BasicServiceUnion mUnion;

	public AbstractVoiceCommandExternal(BasicServiceUnion union, String commandDesc) {
		this.mUnion = union;
		mTTSControllerProxy = union.getTTSController();
		mCommandEngineExternal = union.getCommandEngine();
		this.mCommandDesc = commandDesc;
	}

	public BasicServiceUnion getUnion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		// mTTSControllerProxy.destroy();
	}


	protected void sendAnswerSession(String text) {
		mTTSControllerProxy.play(text);
	}

	protected void sendToServer(String text) {
		mCommandEngineExternal.handleText(text, true, false);
	}
	
	@Override
	public IVoiceCommand execute() {
		// 是否支持TTS DEBUG模式
		if (com.iii360.base.common.utl.KeyList.IS_TTS_DEBUG) {
			mUnion.getTTSController().syncPlay("识别结果为" + mCommandDesc + "命令");
		}
		return null;
	}

}
