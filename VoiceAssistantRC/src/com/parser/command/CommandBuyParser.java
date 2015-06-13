//ID20120818001 zhanglin begin
package com.parser.command;

import java.util.regex.Matcher;

import javax.crypto.Mac;

import com.base.data.CommandInfo;

public class CommandBuyParser extends AbstractCommandParser {

	private String mAction;
	private String mName;
	private String mMumber;
	private String mQustion;

	public CommandBuyParser(Matcher matcher) {
		super(COMMAND_NAME_BUY, matcher);
		// TODO Auto-generated constructor stub
		mQustion = matcher.group();

		mMumber = matcher.group(3);
		if (mMumber == null || mMumber.equals("null")) {
			mMumber = "";
		}
		mAction = matcher.group(4);
		if (mAction == null || mAction.equals("null")) {
			mAction = "";
		}
		mName = matcher.group(5);
		if (mName == null || mName.equals("null")) {
			mName = "";
		}

	}

	@Override
	public CommandInfo parser() {
		// TODO Auto-generated method stub
		CommandInfo commandInfo = new CommandInfo();
		commandInfo._question = mQustion;
		commandInfo._commandName = getCommandName();
		commandInfo._answer = "很抱歉,小智不支持购买功能";
		commandInfo.addArg(mName);
		commandInfo.addArg(mMumber);
		commandInfo.addArg(mAction);

		return commandInfo;
	}
}
// ID20120818001 zhanglin end