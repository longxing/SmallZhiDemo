package com.iii360.voiceassistant.semanteme.command;

import android.content.Context;

import com.base.data.CommandInfo;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.parse.IVoiceCommand;

public class CommandChatExternal extends AbstractVoiceCommandExternal {

	private String mAnswer;

	public CommandChatExternal(BasicServiceUnion union, CommandInfo commandInfo) {
		super(union, "播报");
		mAnswer = commandInfo.getArg(0);
	}

	@Override
	public IVoiceCommand execute() {
		super.execute();
		// TODO Auto-generated method stub
		sendAnswerSession(mAnswer);
		return null;
	}

}
