//ID20120723002 zhanglin begin
package com.parser.command;

import java.util.regex.Matcher;

import com.base.data.CommandInfo;

public class CommandTranslationParser extends AbstractCommandParser {

	private String mRecContent, mGroup1;

	public CommandTranslationParser(String arg) {
		super(COMMAND_NAME_TRANSLATION, arg);

	}

	public CommandTranslationParser(Matcher matcher) {
		super(COMMAND_NAME_TRANSLATION, matcher, true);

		mRecContent = matcher.group();
		mGroup1 = matcher.group(1);

		mRecContent = mRecContent.replace("。", "");
	}

	public CommandInfo parser() {
		// TODO Auto-generated method stubS
		// super.excute();
		String content = getParams(0);
		String languge = getParams(1);
		CommandInfo cmdInfo = new CommandInfo();
		cmdInfo._commandName = COMMAND_NAME_TRANSLATION;
		cmdInfo.addArg(content);
		cmdInfo.addArg(languge);
		if (languge != null && languge.contains("英") || languge == null || languge.trim().length() == 0) {

		} else {
			cmdInfo._answer = "很抱歉，小智暂时不支持非英语翻译功能，您可以试试说翻译我爱你";

		}

		return cmdInfo;
	}

}
// ID20120723002 zhanglin end