package com.iii360.voiceassistant.semanteme.command;

import com.base.data.CommandInfo;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.parse.IVoiceCommand;

public class CommandContiuOperite extends AbstractVoiceCommand {
	private CommandInfo mCommandInfo;

	public CommandContiuOperite(BasicServiceUnion union, CommandInfo commandInfo) {
		super(union, commandInfo, COMMAND_CONTIU_OPERITE, "唤醒监听");
		// TODO Auto-generated constructor stub

	}

	@Override
	public IVoiceCommand execute() {
		super.execute();
		sendAnswerSession(mCommandInfo.getArg(0));
		startRecogniseImediatelyAfterTtsOver();
		return null;
	}

}
