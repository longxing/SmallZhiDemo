package com.iii360.voiceassistant.semanteme.command;

import android.content.Context;

import com.base.data.CommandInfo;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.parse.IVoiceCommand;
import com.voice.assistant.main.KeyList;
import com.voice.assistant.main.R;

public class CommandHandleError extends AbstractVoiceCommand {

	public static String ERROR_MSG = "";

	public static final int CMD_ERR_UNKNOWN = 0;
	public static final int CMD_ERR_NORESULT = 1;
	public static final int CMD_ERR_NOIMPL = 2;
	private int mErrorType = CMD_ERR_UNKNOWN;

	public CommandHandleError(BasicServiceUnion union, CommandInfo commandInfo) {
		super(union, commandInfo, COMMAND_NAME_HANDLE_ERR, "错误处理");
		// TODO Auto-generated constructor stub
	}

	@Override
	public IVoiceCommand execute() {
		super.execute();
		// TODO Auto-generated method stub
		LogManager.e("");
		if (!getUnion().getBaseContext().getGlobalBoolean(KeyList.GKEY_BOOL_CHATMODE)) {
			getUnion().getRecogniseSystem().startCaptureVoice();
		} else {
			ERROR_MSG = mContext.getString(R.string.sorry_Error);
			sendAnswerSession(ERROR_MSG);
		}

		return null;
	}

}
