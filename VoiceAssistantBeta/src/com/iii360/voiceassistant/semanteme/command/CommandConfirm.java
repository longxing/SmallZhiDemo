package com.iii360.voiceassistant.semanteme.command;

import android.content.Context;
import android.content.Intent;

import com.base.data.CommandInfo;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.ITTSController.ITTSStateListener;
import com.iii360.base.inf.parse.IVoiceCommand;
import com.voice.assistant.main.KeyList;

public class CommandConfirm extends AbstractVoiceCommand {
	String mAnswer;
	String mConfirmText;
	String mNegText;

	public CommandConfirm(BasicServiceUnion union, CommandInfo commandInfo) {
		super(union, commandInfo, COMMAND_NAME_CONFIRM, "纠错确认");
		// TODO Auto-generated constructor stub
		if (commandInfo != null) {
			mAnswer = commandInfo.getArg(0);
			mConfirmText = commandInfo.getArg(1);
			mNegText = commandInfo.getArg(2);
		}
	}

	@Override
	public IVoiceCommand execute() {
		super.execute();
		// TODO Auto-generated method stub
		mBaseContext.setGlobalString(KeyList.GKEY_STR_COMFIRM_RECOVERY, mConfirmText);
		// ID20121030001 hujinrong begin
		mBaseContext.setGlobalString(KeyList.GKEY_STR_NEGTEXT, mNegText);
		sendAnswerSession(mAnswer);
		startRecogniseImediatelyAfterTtsOver();
		return null;
	}

}
