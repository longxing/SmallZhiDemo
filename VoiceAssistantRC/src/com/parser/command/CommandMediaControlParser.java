package com.parser.command;

import java.util.HashMap;
import java.util.regex.Matcher;

import com.base.data.CommandInfo;

public class CommandMediaControlParser extends AbstractCommandParser {
	public static boolean mCanOprationOrder = false;
	private String mCtrlParam = null;


	public CommandMediaControlParser(Matcher matcher) {
		super(COMMAND_NAME_MEDIA_CONTROL, matcher);
		mCtrlParam = matcher.group(2);

	}

	@Override
	public CommandInfo parser() {

		CommandInfo info = null;
		if (mCtrlParam != null && !mCtrlParam.trim().equals("")) {
			info = new CommandInfo();
			info.addArg(mCtrlParam);
			info._commandName = COMMAND_NAME_MEDIA_CONTROL;
		}

		return info;
	}
}
