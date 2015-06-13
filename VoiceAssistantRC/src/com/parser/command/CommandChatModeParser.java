package com.parser.command;

import java.util.regex.Matcher;

import com.base.data.CommandInfo;

public class CommandChatModeParser extends AbstractCommandParser {

	public CommandChatModeParser(Matcher matcher) {
		super(COMMAND_NAME_CHAT_MODE, matcher);
		// TODO Auto-generated constructor stub

	}

	public CommandChatModeParser(String matcher) {
		super(COMMAND_NAME_CHAT_MODE, matcher);
		// TODO Auto-generated constructor stub

	}

	@Override
	public CommandInfo parser() {
		// TODO Auto-generated method stub
		CommandInfo info = new CommandInfo();
		info._commandName = getCommandName();

		return info;
	}

}
