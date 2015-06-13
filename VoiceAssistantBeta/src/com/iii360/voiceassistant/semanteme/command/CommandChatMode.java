package com.iii360.voiceassistant.semanteme.command;

import com.base.data.CommandInfo;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.parse.IVoiceCommand;
import com.voice.assistant.main.KeyList;

public class CommandChatMode extends AbstractVoiceCommand {

	public CommandChatMode(BasicServiceUnion union, CommandInfo commandInfo) {
		super(union, commandInfo, COMMAND_NAME_CHAT, "聊天模式");
		// TODO Auto-generated constructor stub
	}

	@Override
	public IVoiceCommand execute() {
		super.execute();
		// TODO Auto-generated method stub
		// com.voice.recognise.KeyList.GKEY_BOOL_CHATMODE
		getUnion().getBaseContext().setGlobalBoolean(KeyList.GKEY_BOOL_CHATMODE, true);

		sendAnswerSession("小智已进入聊天模式，主人，您想聊点啥？");
		getUnion().getBaseContext().setGlobalLong(KeyList.GKEY_LONG_CHATMODE_BEGINTIME, System.currentTimeMillis());
		getUnion().getBaseContext().setGlobalBoolean(KeyList.GKEY_BOOL_AUTO_CHATED_MODE, true);
		getUnion().getBaseContext().setGlobalInteger(KeyList.GKEY_INT_AUTO_CHAT_MODE_NUMBER, 0);
		return null;
	}
}
