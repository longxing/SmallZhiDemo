package com.parser.command;

import java.util.regex.Matcher;

import com.base.data.CommandInfo;

public class CommandPlayMediaHeziNullParser extends AbstractCommandParser {

	public CommandPlayMediaHeziNullParser(String arg) {
		super(COMMAND_PLAYMEDIA_HEZI_NULL, arg);
		// TODO Auto-generated constructor stub
	}


	@Override
	public CommandInfo parser() {
		// TODO Auto-generated method stub
		CommandInfo info = new CommandInfo();
		info._answer = getParams(1);
		info._commandName = getCommandName();
		return info;
	}
}
