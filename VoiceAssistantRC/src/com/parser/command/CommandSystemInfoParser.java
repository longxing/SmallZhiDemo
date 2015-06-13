package com.parser.command;

import java.util.regex.Matcher;

import com.base.data.CommandInfo;

public class CommandSystemInfoParser extends AbstractCommandParser {
	String mQustion;

	public CommandSystemInfoParser(Matcher matcher) {
		super(COMMAND_SYSTEM_INFO, matcher);
		// TODO Auto-generated constructor stub
		mQustion = matcher.group();
	}

	@Override
	public CommandInfo parser() {
		// TODO Auto-generated method stub
		CommandInfo info = new CommandInfo();
		info._question = mQustion;
		info._commandName = getCommandName();
		return info;
	}

}
