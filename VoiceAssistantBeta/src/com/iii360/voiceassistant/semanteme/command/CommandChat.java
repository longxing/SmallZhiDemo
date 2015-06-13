package com.iii360.voiceassistant.semanteme.command;

import android.content.Context;

import com.base.data.CommandInfo;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.parse.IVoiceCommand;

/**
 * 
 *
 */
public class CommandChat extends AbstractVoiceCommand {

	String mAnswer;

	public CommandChat(BasicServiceUnion union, CommandInfo commandInfo) {
		super(union, commandInfo, COMMAND_NAME_CHAT, "聊天");
		mAnswer = commandInfo.getArg(0);
	}

	@Override
	public IVoiceCommand execute() {
		super.execute();
		sendAnswerSession(mAnswer);
		return null;
	}

}
