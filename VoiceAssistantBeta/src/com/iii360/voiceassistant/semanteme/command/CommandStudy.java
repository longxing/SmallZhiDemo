package com.iii360.voiceassistant.semanteme.command;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.base.data.CommandInfo;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.parse.IVoiceCommand;

public class CommandStudy extends AbstractVoiceCommand {
	private String mQuestion;
	private String mAnswer;

	public CommandStudy(BasicServiceUnion union, CommandInfo commandInfo) {
		super(union, commandInfo, COMMAND_NAME_STUDY, "用户教学");
		// TODO Auto-generated constructor stub
		mQuestion = commandInfo._question;
		mAnswer = commandInfo.getArg(0);
	}

	@Override
	public IVoiceCommand execute() {
		super.execute();
		// TODO Auto-generated method stub
		if (null != mAnswer && !"".equals(mAnswer)) {
			sendAnswerSession(mAnswer, true);
		} else {
			getUnion().getRecogniseSystem().startCaptureVoice();
		}
		// Map<String, Object> map = new HashMap<String, Object>();
		// map.put(KeyList.GKEY_OBJ_SEARCH_QUESTION_CONTENT, mQuestion);
		// sendWidget("WidgetSearch", map);

		return null;
	}
}
