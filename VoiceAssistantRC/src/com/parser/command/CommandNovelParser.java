//ID20120818001 zhanglin begin
package com.parser.command;

import java.util.regex.Matcher;

import com.base.data.CommandInfo;

public class CommandNovelParser extends AbstractCommandParser {

	private String mWriter = "";
	private String mName = "";
	private String mType = "";
	private String mQustion;

	public CommandNovelParser(Matcher matcher) {
		super(COMMAND_NAME_NOVEL, matcher);
		// TODO Auto-generated constructor stub
		mQustion = matcher.group();
		mType = matcher.group(6);
		if (mType == null || mType.equals("null")) {
			mType = "";
		}
		mWriter = matcher.group(4);
		if (mWriter == null || mWriter.equals("null")) {
			mWriter = "";
		}
		mName = matcher.group(9);
		if (mName == null || mName.equals("null")) {
			mName = "";
		}
	}

	@Override
	public CommandInfo parser() {
		// TODO Auto-generated method stub
		CommandInfo info = new CommandInfo();
		info.addArg(mType);
		info.addArg(mWriter);
		info.addArg(mName);

		info._question = mQustion;
		info._commandName = getCommandName();
		info._answer = "很抱歉，小智暂时不支持小说功能";
		return info;
	}

}
// ID20120818001 zhanglin end